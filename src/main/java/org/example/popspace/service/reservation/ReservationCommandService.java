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
import java.time.LocalTime;

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
     * 사전 예약 고객 수 < max
     * 1. try increment advance count
     * 2. db 저장, memberset 추가
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
        checkAdvanceAndImmediateReservable(memberId, popupId, reserveDate, reserveTime, cacheDTO);

        // 3. 에약 자리 남아있는지
        boolean success = tryAdvanceCountIcr(popupId, reserveDate, reserveTime, max);
        if (!success) throw new CustomException(ErrorCode.RESERVATION_FULL);

        return handleAdvanceReservation(memberId, popupId, reserveDate, reserveTime);
    }


    /**
     * 예약 생성 - 웨이팅
     * 당일만 가능
     * 현재 입장 예정 고객 수 >= max
     * 1. 예약 생성 후 db 저장, memberset 추가
     */
    @Transactional
    public long makeWalkInReservation(Long memberId, WalkInRequestDTO request) {
        long popupId = request.getPopupId();
        LocalDate reserveDate = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 락 걸려있으면 즉시 입장 x
        // (스케줄러가 email_send 처리 중)
        String lockKey = redisKeyUtil.entranceLockKey(popupId);
        if (redisRepo.isLocked(lockKey)) {
            throw new CustomException(ErrorCode.CANNOT_ENTER_IMMEDIATELY);
        }

        // 1. 팝업 예약 가능 날짜 확인, 최대 예약 수 확인
        ReservationPopupCacheDTO cacheDTO = loadOrCachePopupInfo(popupId);

        // 2. 예약 가능 여부 확인
        checkWalkInReservable(memberId, popupId, reserveDate, now, cacheDTO);

        // 3. 현장 웨이팅 등록
        return handleReservedWalkIn(memberId, popupId, reserveDate);
    }


    /**
     * 예약 생성 - 웨이팅 (즉시 입장)
     * 당일만 가능
     * 조건: 현재 시간의 입장 예정 고객 수 < max
     * 1. try increment entrance count
     * 2. db 저장, memberset 추가
     */
    @Transactional
    public long makeImmediateWalkIn(Long memberId, WalkInRequestDTO request) {
        long popupId = request.getPopupId();
        LocalDate reserveDate = LocalDate.now();

        // 입장 시간대 for redis key
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        String reserveTime = String.format("%02d:00", hour);

        // 1. 팝업 예약 가능 날짜 확인, 최대 예약 수 확인
        ReservationPopupCacheDTO cacheDTO = loadOrCachePopupInfo(popupId);
        int max = cacheDTO.getMaxReservations();

        // 2. 예약 가능 여부 확인
        checkWalkInReservable(memberId, popupId, reserveDate, now, cacheDTO);

        // 3. 바로 입장 가능 여부 확인 (current < max)
        boolean success = tryImmediateCountIcr(popupId, reserveDate, reserveTime, max);
        if (success){
            log.info("즉시 입장 가능합니다.");
            // 4. 즉시 입장 웨이팅 등록
            return handleImmediateWalkIn(memberId, popupId, reserveDate, reserveTime);
        }else{
            throw new CustomException(ErrorCode.CANNOT_ENTER_IMMEDIATELY);
        }
    }


    /**
     * 예약 취소  - 사전예약만 취소 가능
     * 경쟁 조건 x: loadOrCache* 호출 x
     * 예약 수 -1, 예약 명단에서 삭제 → 동일 사용자가 같은 날짜에 다시 예약 가능
     */
    @Transactional
    public void cancelReservation(long memberId, long reserveId) {
        ReservationDTO reservation = reservationMapper.findReservationById(reserveId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // 1. DB에서 예약 존재 여부 및 취소 권한 확인
        validateCancelableReservation(reservation, memberId);

        String advanceCountKey = redisKeyUtil.advanceCountKey(reservation.getPopupId(), reservation.getReserveDate(), reservation.getReserveTime());
        String setKey = redisKeyUtil.memberSetKey(reservation.getPopupId(), reservation.getReserveDate());

        // 2. 예약 취소
        handleCancelAdvance(reserveId, advanceCountKey, setKey, memberId);
    }


    /**
     * 예약 노쇼 처리 - 사전 예약, 웨이팅(즉시 입장x)
     * 경쟁 조건 x: loadOrCache* 호출 x
     * - decrement entrance count
     * - 예약 명단에서 삭제
     */
    @Transactional
    public void noshowReservation(long reserveId) {

        ReservationDTO reservation = reservationMapper.findReservationById(reserveId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        long memberId = reservation.getMemberId();

        // Redis key
        String setKey = redisKeyUtil.memberSetKey(reservation.getPopupId(), reservation.getReserveDate());
        String entranceCountKey = redisKeyUtil.entranceCountKey(reservation.getPopupId(), reservation.getReserveDate(), reservation.getReserveTime());

        // 노쇼처리
        handleNoShow(reserveId, entranceCountKey, setKey, memberId);
    }

// ------------------------------------------------------

    /**
     * 즉시 입장
     * DB: CHECKED_IN 상태, WALK-IN
     * (현재 시간대 입장 예정 고객 수 < max)
     */
    private long handleImmediateWalkIn(long memberId, long popupId, LocalDate reserveDate, String reserveTime){
        try {
            log.info("[immediate walk-in]: try insert into db");
            ReservationCreateDTO reservation = ReservationCreateDTO.forImmediateWalkIn(memberId, popupId, reserveDate, reserveTime);
            reservationMapper.insertReservation(reservation);

            // 생성된 ID 반환
            return reservation.getReserveId();
        } catch (Exception e) {
            log.error("[immediate walk-in]: DB insert error", e);
            // 실패 시 redis 롤백
            rollbackImmediateWalkIn(popupId, reserveDate, reserveTime, memberId);
            throw new CustomException(ErrorCode.RESERVATION_FAIL);
        }
    }


    /**
     * 사전 예약
     * DB: RESERVED 상태, ADVANCE
     * (선택 시간대 예약 수 < max)
     */
    private long handleAdvanceReservation(long memberId, long popupId, LocalDate reserveDate, String reserveTime){
        try {
            log.info("[advance]: try insert into db");
            ReservationCreateDTO reservation = ReservationCreateDTO.forAdvance(memberId, popupId, reserveDate, reserveTime);
            reservationMapper.insertReservation(reservation);

            // 생성된 ID 반환
            return reservation.getReserveId();
        } catch (Exception e) {
            log.error("[advance]: DB insert error", e);
            // 실패 시 redis 롤백
            rollbackAdvanceReservation(popupId, reserveDate, reserveTime, memberId);
            throw new CustomException(ErrorCode.RESERVATION_FAIL);
        }
    }


    /**
     * 즉시 입장 x 현장 웨이팅
     * DB: RESERVED 상태, WALK-IN
     * (현재 시간대 입장 예정 고객 수 >= max)
     */
    private long handleReservedWalkIn(long memberId, long popupId, LocalDate reserveDate) {
        try {
            log.info("[walk-in]: try insert into db");
            ReservationCreateDTO reservation = ReservationCreateDTO.forWalkIn(memberId, popupId, reserveDate);
            reservationMapper.insertReservation(reservation);

            // 생성된 ID 반환
            return reservation.getReserveId();
        } catch (Exception e) {
            log.error("[walk-in]: DB insert error", e);
            // 실패 시 redis 롤백
            rollbackWalkInReservation(popupId, reserveDate, memberId);
            throw new CustomException(ErrorCode.RESERVATION_FAIL);
        }
    }


    /**
     * 팝업 정보 캐싱
     * - redis 팝업 정보 없으면 db 조회 후 캐싱
     */
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

        ReservationPopupCacheDTO cacheDTO = ReservationPopupCacheDTO.from(fromDb);

        // Redis에 저장
        redisRepo.setPopupInfo(key, cacheDTO);
        return cacheDTO;
    }


    /**
     * 중복 멤버 제외 위한 캐싱
     * redis 멤버 내역 없으면 db 조회 후 캐싱
     * - RESERVED, CHECKED_IN, CHECKED_OUT, EMAIL_SEND
     */
    private boolean loadOrCacheReservedMember(Long memberId, Long popupId, LocalDate reserveDate) {
        log.info("load or cache reserved members");
        String setKey = redisKeyUtil.memberSetKey(popupId, reserveDate);

        log.info("before remove members = {}", redisRepo.getAllMembers(setKey));

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


    /**
     * 사전 예약 or 즉시 입장 가능 여부 확인
     * - 유효한 날짜 / 시간인지
     * - 선택 날짜에 선택 팝업 RESERVED, CHECKED_IN, CHECKED_OUT, EMAIL_SEND 상태인 예약 있는지
     */
    private void checkAdvanceAndImmediateReservable(Long memberId, Long popupId, LocalDate reserveDate, String reserveTime, ReservationPopupCacheDTO cacheDTO) {
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


    /**
     * 웨이팅 예약 가능 여부 확인
     * - 유효한 날짜, 시간인지
     * - 오늘 날짜에 선택 팝업 RESERVED, CHECKED_IN, CHECKED_OUT, EMAIL_SEND 상태인 예약 있는지
     */
    private void checkWalkInReservable(Long memberId, Long popupId, LocalDate reserveDate, LocalTime now, ReservationPopupCacheDTO cacheDTO) {
        if (    reserveDate.isAfter(cacheDTO.getEndDate()) ||
                reserveDate.isBefore(cacheDTO.getStartDate()) ||
                !now.isAfter(LocalTime.parse(cacheDTO.getOpenTime())) ||
                !now.isBefore(LocalTime.parse(cacheDTO.getCloseTime()))
        ) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_DATE);
        }

        if (loadOrCacheReservedMember(memberId, popupId, reserveDate)) {
            throw new CustomException(ErrorCode.ALREADY_RESERVED);
        }
    }


    /**
     * 사전 예약 시도 - increment advance count
     * 1. check if redis has key
     *    if not, get count from db and set to redis (sync redis with db)
     * 2. count < max 확인
     *    (atomic)
     */
    private boolean tryAdvanceCountIcr(Long popupId, LocalDate reserveDate, String reserveTime, int max) {
        log.info("[advance-redis]: try advance reservation");
        String countKey = redisKeyUtil.advanceCountKey(popupId, reserveDate, reserveTime);

        // redis key 없으면 DB 조회해서 set (setIfAbsent 방식으로)
        if (!redisRepo.hasKey(countKey)) {
            log.info("[advance-redis]: set advance count to redis from db");
            int count = reservationMapper.countConfirmedAdvance(popupId, reserveDate, reserveTime);
            log.info("[advance-db]: 증가 전 count {}", count);
            redisRepo.setCountIfAbsent(countKey, count);
        }

        String count = redisRepo.getCount(countKey);
        log.info("[advance-redis]: 증가 전 count {}", count);

        // count < max 확인 (atomic)
        return redisRepo.atomicIncrementIfBelowMax(countKey, max);
    }


    /**
     * 즉시 입장 시도 - increment entrance count
     * 1. check if redis has key
     *    if not, get count from db and set to redis (sync redis with db)
     * 2. count < max 확인
     *    (atomic)
     */
    private boolean tryImmediateCountIcr(Long popupId, LocalDate reserveDate, String reserveTime, int max) {
        log.info("[immediate-redis]: try immediate reservation");
        String countKey = redisKeyUtil.entranceCountKey(popupId, reserveDate, reserveTime);
        String timestampStr = reserveDate.toString() + " " + reserveTime;

        // redis key 없으면 DB 조회해서 set (setIfAbsent 방식으로)
        if (!redisRepo.hasKey(countKey)) {
            log.info("[immediate-redis]: set entrance count to redis from db");
            int count = reservationMapper.countConfirmedEntrance(popupId, reserveDate, reserveTime, timestampStr);
            log.info("[immediate-db]: 증가 전 count {}", count);
            redisRepo.setCountIfAbsent(countKey, count);
        }

        String count = redisRepo.getCount(countKey);
        log.info("[immediate-redis]: 증가 전 count {}", count);

        // count < max 확인 (atomic)
        return redisRepo.atomicIncrementIfBelowMax(countKey, max);
    }


    /**
     * 사전 예약 실패 시 롤백
     * 1. decrement advance count
     * 2. remove member from member set
     */
    private void rollbackAdvanceReservation(Long popupId, LocalDate reserveDate, String reserveTime, Long memberId) {
        String countKey = redisKeyUtil.advanceCountKey(popupId, reserveDate, reserveTime);
        String setKey = redisKeyUtil.memberSetKey(popupId, reserveDate);

        try {
            log.info("[advance]: rollback advance reservation");
            redisRepo.decrementCount(countKey);
            redisRepo.removeMemberFromSet(setKey, memberId);
        } catch (Exception e) {
            log.warn("[advance]: rollback failed for memberId={}, popupId={}, reserveDate={}, reserveTime={}",
                    memberId, popupId, reserveDate, reserveTime, e);
        }
    }


    /**
     * 즉시 입장 실패 시 롤백
     * 1. decrement entrance count
     * 2. remove member from member set
     */
    private void rollbackImmediateWalkIn(Long popupId, LocalDate reserveDate, String reserveTime, Long memberId) {
        String countKey = redisKeyUtil.entranceCountKey(popupId, reserveDate, reserveTime);
        String setKey = redisKeyUtil.memberSetKey(popupId, reserveDate);

        try {
            log.info("[immediate walk-in]: rollback immediate walk-in");
            redisRepo.decrementCount(countKey);
            redisRepo.removeMemberFromSet(setKey, memberId);
        } catch (Exception e) {
            log.warn("[immediate walk-in]: rollback failed for memberId={}, popupId={}, reserveDate={}, reserveTime={}",
                    memberId, popupId, reserveDate, reserveTime, e);
        }
    }


    /**
     * 웨이팅 예약 실패 시 롤백
     * 1. remove member from member set
     */
    private void rollbackWalkInReservation(Long popupId, LocalDate reserveDate, Long memberId) {
        String setKey = redisKeyUtil.memberSetKey(popupId, reserveDate);

        try {
            log.info("[walk-in]: rollback walk-in reservation");
            redisRepo.removeMemberFromSet(setKey, memberId);
        } catch (Exception e) {
            log.warn("[walk-in]: rollback failed for memberId={}, popupId={}, reserveDate={}",
                    memberId, popupId, reserveDate, e);
        }
    }


    /**
     * 예약 취소 가능 여부 확인
     * - RESERVED 상태의 유효한 사전 예약
     */
    private void validateCancelableReservation(ReservationDTO reservation, long requesterId) {
        log.info("[cancel]: check if reservation is cancelable");
        if (reservation.getMemberId() != requesterId) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        if (!reservation.getReservationState().equals("RESERVED") ||
                !reservation.getReservationType().equals("ADVANCE")) {
            throw new CustomException(ErrorCode.RESERVATION_CANNOT_BE_CANCELLED);
        }
    }


    /**
     * 사전 예약 취소
     * 1. decrement advance count
     * 2. remove member from member set
     */
    private void handleCancelAdvance(long reserveId, String advanceCountKey, String setKey, long memberId){
        try {
            // DB 먼저
            log.info("[cancel]: try update db");
            reservationMapper.cancelReservation(reserveId);

            // redis (예약 수 -1 / 예약 명단에서 삭제)
            redisRepo.decrementCount(advanceCountKey);

            log.info("remove redis set member: key={}, memberId={}", setKey, memberId);
            log.info("before remove members = {}", redisRepo.getAllMembers(setKey)); // 임시로 전체 확인
            redisRepo.removeMemberFromSet(setKey, memberId);
            log.info("after remove members = {}", redisRepo.getAllMembers(setKey)); // 임시로 전체 확인
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CANCEL_FAIL);
        }
    }


    /**
     * 노쇼 처리
     * 1. decrement entrance count
     * 2. remove member from member set
     */
    private void handleNoShow(long reserveId, String entranceCountKey, String setKey, long memberId){
        try {
            // DB 먼저
            log.info("[noshow]: try update db");
            reservationMapper.updateReservationState(reserveId, "NOSHOW");

            redisRepo.decrementCount(entranceCountKey);
            redisRepo.removeMemberFromSet(setKey, memberId);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.NOSHOW_FAIL);
        }
    }

}