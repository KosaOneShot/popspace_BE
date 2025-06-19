package org.example.popspace.service.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.qr.ReservationStatusDTO;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.ReservationMapper;
import org.example.popspace.mapper.redis.ReservationRedisRepository;
import org.example.popspace.repository.ReservationRepository;
import org.example.popspace.service.common.OwnerAuthorityValidator;
import org.example.popspace.util.reservation.RedisKeyUtil;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationAdminService {

    private final ReservationMapper reservationMapper;
    private final OwnerAuthorityValidator authorityValidator;
    private final ReservationRepository reservationRepository;
    private final ReservationRedisRepository redisRepo;
    private final RedisKeyUtil redisKeyUtil;


    /**
     * 입장 처리
     * 1. 팝업 사장 권한 확인
     * 2. 입장 처리
     */
    public void validateAndCheckIn(long memberId, long reserveId) {
        authorityValidator.validatePopupOwnerByReservation(memberId, reserveId);
        checkIn(reserveId);
    }


    /**
     * 퇴장 처리
     * 1. 팝업 사장 권한 확인
     * 2. 퇴장 처리
     */
    public void validateAndCheckOut(long memberId, long reserveId) {
        authorityValidator.validatePopupOwnerByReservation(memberId, reserveId);
        checkOut(reserveId);
    }


    /**
     * 사전 예약자 & 현장 웨이팅 (즉시 입장x) 입장 처리
     * email_send에서 이미 entrance count 증가 시켰기 때문에 따로 증가 x
     *
     * 즉시 입장 워크인 처리: ReservationCommandService.handleImmediateWalkIn
     */
    private void checkIn(long reserveId) {
        log.info("[check in]: try check in reserve_id {}", reserveId);
        ReservationStatusDTO reservation = reservationMapper.findReservationStatus(reserveId);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        if (!"EMAIL_SEND".equals(reservation.getReservationState())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        // 프로시저
        reservationRepository.logEntranceAction(reserveId, "CHECKED_IN");
    }



    /**
     * 퇴장 처리
     * 1. plsql - reservation, entrance_log
     * 2. decrement entrance count
     */
    private void checkOut(long reserveId) {
        log.info("[check out]: try check out reserve_id {}", reserveId);
        ReservationStatusDTO reservation = reservationMapper.findReservationStatus(reserveId);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        if (!"CHECKED_IN".equals(reservation.getReservationState())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        String entranceCountKey = redisKeyUtil.entranceCountKey(reservation.getPopupId(), reservation.getReserveDate(), reservation.getReserveTime());

        try {
            // 프로시저
            reservationRepository.logEntranceAction(reserveId, "CHECKED_OUT");

            redisRepo.decrementCount(entranceCountKey);
        } catch (Exception e) {
            log.warn("[check out]: failed decrement entrance count . key={}, error={}", entranceCountKey, e.getMessage());
        }
    }

}
