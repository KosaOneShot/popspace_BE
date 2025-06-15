package org.example.popspace.service.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.reservation.*;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.ReservationMapper;
import org.example.popspace.mapper.redis.ReservationRedisRepository;
import org.example.popspace.util.reservation.RedisKeyUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
예약 생성/취소 처리 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationCommandService {

    private final ReservationMapper reservationMapper;
    private final ReservationRedisRepository redisRepo;
    private final RedisKeyUtil redisKeyUtil;


    /**
     * 예약 생성 - 사전예약
     */
    @Transactional
    public long makeAdvanceReservation(Long memberId, AdvanceRequestDTO request) {
        long popupId = request.getPopupId();
        LocalDate reserveDate = request.getReserveDateAsLocalDate();
        String reserveTime = request.getReserveTime();

        // 1. 팝업 예약 가능 날짜, 최대 예약 수 확인
        ReservationPopupCacheDTO cacheDTO = loadOrCachePopupInfo(popupId);
        int max = cacheDTO.getMaxReservations();

        // 2. 예약 가능 여부 확인
        checkAdvanceReservable(memberId, popupId, reserveDate, reserveTime, cacheDTO);

        // 3. 에약 자리 남아있는지
        boolean success = tryAdvanceReservationCount(popupId, reserveDate, reserveTime, max);
        if (!success) throw new CustomException(ErrorCode.RESERVATION_FULL);

        try {
            // 4. DB 저장 (성공 시만 최종 예약 완료)
            log.info("try insert into db");
            ReservationCreateDTO reservation = ReservationCreateDTO.forAdvance(memberId, popupId, reserveDate, reserveTime);
            reservationMapper.insertReservation(reservation);

            // 생성된 예약 ID 반환
            return reservation.getReserveId();

        } catch (Exception e) {
            log.error("DB insert error", e);
            // 실패 시 redis 롤백
            rollbackAdvanceReservation(popupId, reserveDate, reserveTime, memberId);
            throw new CustomException(ErrorCode.RESERVATION_FAIL);
        }
    }


    /**
     * 예약 생성 - 웨이팅
     * 당일만 가능
     */
    @Transactional
    public long makeWalkInReservation(Long memberId, WalkInRequestDTO request) {
        long popupId = request.getPopupId();
        LocalDate reserveDate = LocalDate.now();

        // 1. 팝업 존재 여부 확인, 예약 가능 날짜 확인
        ReservationPopupCacheDTO cacheDTO = loadOrCachePopupInfo(popupId);

        // 2. 예약 가능 여부 확인
        checkWalkInReservable(memberId, popupId, reserveDate, cacheDTO);

        try {
            // 4. DB 저장 (성공 시만 최종 예약 완료)
            ReservationCreateDTO reservation = ReservationCreateDTO.forWalkIn(memberId, popupId, reserveDate);
            reservationMapper.insertReservation(reservation);

            // 생성된 ID 반환
            return reservation.getReserveId();
        } catch (Exception e) {
            log.error("DB insert error", e);
            // 실패 시 redis 롤백
            rollbackWalkInReservation(popupId, reserveDate, memberId);
            throw new CustomException(ErrorCode.RESERVATION_FAIL);
        }
    }


    /**
     * 예약 취소  - 사전예약만 취소 가능
     * 예약 취수는 경쟁 조건 x: loadOrCache* 호출 x
     * 예약 수 -1, 예약 명단에서 삭제 → 동일 사용자가 같은 날짜에 다시 예약 가능
     */
    @Transactional
    public void cancelReservation(long memberId, long reserveId) {
        ReservationDTO reservation = reservationMapper.findReservationById(reserveId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // 1. DB에서 예약 존재 여부 및 취소 권한 확인
        validateCancelableReservation(reservation, memberId);

        // 2. Redis key
        String advanceCountKey = redisKeyUtil.advanceCountKey(reservation.getPopupId(), reservation.getReserveDate(), reservation.getReserveTime());
        String setKey = redisKeyUtil.memberSetKey(reservation.getPopupId(), reservation.getReserveDate());

        try {
            // DB 먼저
            log.info("try update db");
            reservationMapper.cancelReservation(reserveId);

            // redis (예약 수 -1 / 예약 명단에서 삭제)
            redisRepo.decrementCount(advanceCountKey);
            redisRepo.removeMemberFromSet(setKey, memberId);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CANCEL_FAIL);
        }
    }


    /**
     * 예약 노쇼 처리 - 사전 예약, 웨이팅
     * - 해당 시간대가 이미 지난 시점이므로 추가적인 예약 x: 예약 카운트 신경 쓸 필요 x
     * - 예약 명단에서 삭제
     */
    @Transactional
    public void noshowReservation(long reserveId) {

        ReservationDTO reservation = reservationMapper.findReservationById(reserveId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        long memberId = reservation.getMemberId();

        // 1. Redis key
        String setKey = redisKeyUtil.memberSetKey(reservation.getPopupId(), reservation.getReserveDate());

        try {
            // DB 먼저
            reservationMapper.updateReservationState(reserveId, "NOSHOW");

            redisRepo.removeMemberFromSet(setKey, memberId);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.NOSHOW_FAIL);
        }
    }





    // Redis 팝업 정보 없으면 DB 조회 후 캐싱
    private ReservationPopupCacheDTO loadOrCachePopupInfo(Long popupId) {
        log.info("load or cache popup info");
        String key = redisKeyUtil.popupInfoKey(popupId);
        ReservationPopupCacheDTO cached = redisRepo.getPopupInfo(key);

        if (cached != null) {
            return cached;
        }

        // Redis에 없으면 DB 조회
        ReservationPopupInfoDTO fromDb = reservationMapper.findPopupTimeById(popupId)
                .orElseThrow(() -> new CustomException(ErrorCode.POPUP_NOT_FOUND));

        // DTO 변환
        ReservationPopupCacheDTO cacheDTO = ReservationPopupCacheDTO.from(fromDb);

        // Redis에 저장
        redisRepo.setPopupInfo(key, cacheDTO);

        return cacheDTO;
    }

    // Redis 예약한 멤버 내역 없으면 DB 조회 후 캐싱
    private boolean loadOrCacheReservedMember(Long memberId, Long popupId, LocalDate reserveDate) {
        log.info("load or cache reserved members");
        String setKey = redisKeyUtil.memberSetKey(popupId, reserveDate);

        // redis - 확인 & 삽입 (atomic)
        boolean alreadyReserved = redisRepo.tryAddMemberToSet(setKey, memberId);
        if (alreadyReserved) return true;

        // 캐시에 없었지만 DB엔 존재할 수도 있음 → 롤백
        boolean reservedInDb = reservationMapper.isReserved(memberId, popupId, reserveDate);
        if (reservedInDb) {
            redisRepo.removeMemberFromSet(setKey, memberId);
            return true;
        }

        return false;
    }

    // 사전 예약 가능 여부 확인 - 날짜,시간,중복 여부
    private void checkAdvanceReservable(Long memberId, Long popupId, LocalDate reserveDate, String reserveTime, ReservationPopupCacheDTO cacheDTO) {
        if (reserveDate.isAfter(cacheDTO.getEndDate()) || reserveDate.isBefore(cacheDTO.getStartDate())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_DATE);
        }

        if (reserveTime.compareTo(cacheDTO.getOpenTime()) < 0 || reserveTime.compareTo(cacheDTO.getCloseTime()) > 0) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_TIME);
        }

        if (loadOrCacheReservedMember(memberId, popupId, reserveDate)) {
            throw new CustomException(ErrorCode.ALREADY_RESERVED);
        }
    }

    // 웨이팅 예약 가능 여부 확인 - 날짜 중복 여부
    private void checkWalkInReservable(Long memberId, Long popupId, LocalDate reserveDate, ReservationPopupCacheDTO cacheDTO) {
        if (reserveDate.isAfter(cacheDTO.getEndDate()) || reserveDate.isBefore(cacheDTO.getStartDate())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_DATE);
        }

        if (loadOrCacheReservedMember(memberId, popupId, reserveDate)) {
            throw new CustomException(ErrorCode.ALREADY_RESERVED);
        }
    }

    // 사전 예약 시도
    private boolean tryAdvanceReservationCount(Long popupId, LocalDate reserveDate, String reserveTime, int max) {
        log.info("try advance reservation");
        String countKey = redisKeyUtil.advanceCountKey(popupId, reserveDate, reserveTime);

        // redis key 없으면 DB 조회해서 set (setIfAbsent 방식으로)
        if (!redisRepo.hasKey(countKey)) {
            int count = reservationMapper.countConfirmedAdvance(popupId, reserveDate, reserveTime);
            log.info("advance count {}", count);
            redisRepo.setCountIfAbsent(countKey, count);
        }

        String count = redisRepo.getCount(countKey);
        log.info("증가 전 count {}", count);

        // count < max 확인 (atomic)
        return redisRepo.tryIncrementCount(countKey, max);
    }

    // 사전 예약 실패 시 롤백
    private void rollbackAdvanceReservation(Long popupId, LocalDate reserveDate, String reserveTime, Long memberId) {
        String countKey = redisKeyUtil.advanceCountKey(popupId, reserveDate, reserveTime);
        String setKey = redisKeyUtil.memberSetKey(popupId, reserveDate);

        try {
            redisRepo.decrementCount(countKey);
            redisRepo.removeMemberFromSet(setKey, memberId);
        } catch (Exception e) {
            log.warn("[advance]: rollback failed for memberId={}, popupId={}, reserveDate={}, reserveTime={}",
                    memberId, popupId, reserveDate, reserveTime, e);
        }
    }

    // 웨이팅 예약 실패 시 롤백
    private void rollbackWalkInReservation(Long popupId, LocalDate reserveDate, Long memberId) {
        String setKey = redisKeyUtil.memberSetKey(popupId, reserveDate);

        try {
            redisRepo.removeMemberFromSet(setKey, memberId);
        } catch (Exception e) {
            log.warn("[walk-in]: rollback failed for memberId={}, popupId={}, reserveDate={}",
                    memberId, popupId, reserveDate, e);
        }
    }

    // 취소 가능한 예약인지 확인
    private void validateCancelableReservation(ReservationDTO reservation, long requesterId) {
        if (reservation.getMemberId() != requesterId) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        if (!reservation.getReservationState().equals("RESERVED") ||
                !reservation.getReservationType().equals("ADVANCE")) {
            throw new CustomException(ErrorCode.RESERVATION_CANNOT_BE_CANCELLED);
        }
    }

}