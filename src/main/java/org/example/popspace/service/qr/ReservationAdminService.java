package org.example.popspace.service.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.qr.ReservationStatusDTO;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.ReservationMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationAdminService {

    private final ReservationMapper reservationMapper;

    // 팝업 사장 권한 확인 후 입장 처리
    public void validateAndCheckIn(long userId, long reservationId) {
        // 1. 팝업 사장 여부 검증
        validateOwnerAuthority(userId, reservationId);

        // 2. 입장 처리
        checkIn(reservationId);
    }

    // 팝업 사장 권한 확인 후 퇴장 처리
    public void validateAndCheckOut(long userId, long reservationId) {
        // 1. 팝업 사장 여부 검증
        validateOwnerAuthority(userId, reservationId);

        // 2. 입장 처리
        checkIn(reservationId);
    }

    // 입장 처리
    private void checkIn(long reservationId) {
        ReservationStatusDTO reservation = reservationMapper.findReservationStatus(reservationId);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        if (!"RESERVED".equals(reservation.getReservationState())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        reservationMapper.updateReservationState(reservationId, "CHECKED_IN");
    }

    // 퇴장 처리
    private void checkOut(long reservationId) {
        ReservationStatusDTO reservation = reservationMapper.findReservationStatus(reservationId);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        if (!"CHECKED_IN".equals(reservation.getReservationState())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        reservationMapper.updateReservationState(reservationId, "CHECKED_OUT");
    }

    // api 요청자가 팝업 사장인지 판단
    private void validateOwnerAuthority(long userId, long reservationId) {

        long popupOwnerId = reservationMapper.findPopupOwnerIdByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        log.info("Popup owner id: " + popupOwnerId);

        if (userId != popupOwnerId) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }
}
