package org.example.popspace.service.common;

import lombok.RequiredArgsConstructor;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.PopupMapper;
import org.example.popspace.mapper.ReservationMapper;
import org.springframework.stereotype.Component;

/**
 * 팝업 사장 권한 확인
 */
@Component
@RequiredArgsConstructor
public class OwnerAuthorityValidator {

    private final ReservationMapper reservationMapper;
    private final PopupMapper popupMapper;

    // api 요청자가 해당 예약의 팝업 사장인지 판단
    public void validatePopupOwnerByReservation(long memberId, long reserveId) {
        long popupOwnerId = reservationMapper.findPopupOwnerIdByReserveId(reserveId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (memberId != popupOwnerId) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }

    // api 요청자가 해당 팝업의 사장인지 판단
    public void validatePopupOwnerByPopup(long memberId, long popupId){
        long popupOwnerId = popupMapper.findPopupOwnerIdByPopupId(popupId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (memberId != popupOwnerId) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }
}
