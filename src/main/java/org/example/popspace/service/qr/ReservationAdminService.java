package org.example.popspace.service.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.qr.ReservationStatusDTO;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.ReservationMapper;
import org.example.popspace.repository.ReservationRepository;
import org.example.popspace.service.common.OwnerAuthorityValidator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationAdminService {

    private final ReservationMapper reservationMapper;
    private final OwnerAuthorityValidator authorityValidator;
    private final ReservationRepository reservationRepository;

    // 팝업 사장 권한 확인 후 입장 처리
    public void validateAndCheckIn(long memberId, long reserveId) {
        // 1. 팝업 사장 여부 검증
        authorityValidator.validatePopupOwnerByReservation(memberId, reserveId);

        // 2. 입장 처리
        checkIn(reserveId);
    }

    // 팝업 사장 권한 확인 후 퇴장 처리
    public void validateAndCheckOut(long memberId, long reserveId) {
        // 1. 팝업 사장 여부 검증
        authorityValidator.validatePopupOwnerByReservation(memberId, reserveId);

        // 2. 퇴장 처리
        checkOut(reserveId);
    }

    // 입장 처리
    private void checkIn(long reserveId) {
        ReservationStatusDTO reservation = reservationMapper.findReservationStatus(reserveId);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        if (!"EMAIL_SEND".equals(reservation.getReservationState())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATE);
        }
        //reservationMapper.updateReservationState(reserveId, "CHECKED_IN");
        reservationRepository.callUpdateEntranceStatus(reserveId, "CHECKED_IN");
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

        //reservationMapper.updateReservationState(reserveId, "CHECKED_OUT");
        reservationRepository.callUpdateEntranceStatus(reserveId, "CHECKED_OUT");
    }

}
