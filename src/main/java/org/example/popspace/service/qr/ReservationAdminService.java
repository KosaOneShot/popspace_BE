package org.example.popspace.service.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.qr.ReservationStatusDTO;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.ReservationMapper;
import org.example.popspace.service.common.OwnerAuthorityValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationAdminService {

    private final ReservationMapper reservationMapper;
    private final OwnerAuthorityValidator authorityValidator;

    // 팝업 사장 권한 확인 후 입장 처리
    public void validateAndCheckIn(long userId, long reserveId) {
        // 1. 팝업 사장 여부 검증
        authorityValidator.validatePopupOwnerByReservation(userId, reserveId);

        // 2. 입장 처리
        checkIn(reserveId);
    }

    // 팝업 사장 권한 확인 후 퇴장 처리
    public void validateAndCheckOut(long userId, long reserveId) {
        // 1. 팝업 사장 여부 검증
        authorityValidator.validatePopupOwnerByReservation(userId, reserveId);

        // 2. 입장 처리
        checkIn(reserveId);
    }

    // 입장 처리
    private void checkIn(long reserveId) {
        ReservationStatusDTO reservation = reservationMapper.findReservationStatus(reserveId);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        if (!"RESERVED".equals(reservation.getReservationState())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        // 입장 시간 확인
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        // 예약 시간 전 입장 불가
        if (!today.equals(reservation.getReserveDate()) ||
                now.isBefore(LocalTime.parse(reservation.getReserveTime()))) {
            throw new CustomException(ErrorCode.NOT_CHECKIN_TIME);
        }

        reservationMapper.updateReservationState(reserveId, "CHECKED_IN");
    }

    // 퇴장 처리
    private void checkOut(long reserveId) {
        ReservationStatusDTO reservation = reservationMapper.findReservationStatus(reserveId);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        if (!"CHECKED_IN".equals(reservation.getReservationState())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        reservationMapper.updateReservationState(reserveId, "CHECKED_OUT");
    }

}
