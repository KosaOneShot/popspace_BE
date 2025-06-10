package org.example.popspace.service.common;

import lombok.RequiredArgsConstructor;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.PopupMapper;
import org.example.popspace.mapper.ReservationMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerAuthorityValidator {

    private final ReservationMapper reservationMapper;
    private final PopupMapper popupMapper;

    // api 요청자가 해당 예약의 팝업 사장인지 판단
    public void validatePopupOwnerByReservation(long userId, long reservationId) {
        long popupOwnerId = reservationMapper.findPopupOwnerIdByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (userId != popupOwnerId) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }

    // api 요청자가 해당 팝업의 사장인지 판단
    public void validatePopupOwnerByPopup(long userId, long popupId){
        long popupOwnerId = popupMapper.findPopupOwnerIdByPopupId(popupId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (userId != popupOwnerId) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }
}
