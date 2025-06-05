package org.example.popspace.service.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.reservation.ReservationStatusDTO;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.ReservationMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationAdminService {

    private final ReservationMapper reservationMapper;

    // 입장 처리
    public void checkIn(Long reservationId) {
        ReservationStatusDTO reservation = reservationMapper.findReservationStatus(reservationId);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        if (!"1".equals(reservation.getReservationState())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        reservationMapper.updateReservationState(reservationId, "3");
    }

    // 퇴장 처리
    public void checkOut(Long reservationId) {
        ReservationStatusDTO reservation = reservationMapper.findReservationStatus(reservationId);
        if (reservation == null) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        if (!"3".equals(reservation.getReservationState())) {
            throw new CustomException(ErrorCode.INVALID_RESERVATION_STATE);
        }

        reservationMapper.updateReservationState(reservationId, "4");
    }

    // api 요청자가 팝업 사장인지 판단
    public void validateOwnerAuthority(Long reservationId) {
        CustomUserDetail user = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = user.getId();

        Long popupOwnerId = reservationMapper.findPopupOwnerIdByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        log.info("login member id: " + memberId);
        log.info("Popup owner id: " + popupOwnerId);

        if (!memberId.equals(popupOwnerId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }
}
