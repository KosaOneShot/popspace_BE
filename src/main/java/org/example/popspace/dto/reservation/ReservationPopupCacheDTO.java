package org.example.popspace.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 캐싱할 팝업 정보
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReservationPopupCacheDTO {
    private Long popupId;

    private int maxReservations;

    private LocalDate startDate;
    private LocalDate endDate;

    private String openTime;
    private String closeTime;

    public static ReservationPopupCacheDTO from(ReservationPopupInfoDTO source) {
        ReservationPopupCacheDTO dto = new ReservationPopupCacheDTO();
        dto.setPopupId(source.getPopupId());
        dto.setMaxReservations(source.getMaxReservations());
        dto.setStartDate(source.getStartDate());
        dto.setEndDate(source.getEndDate());
        dto.setOpenTime(source.getOpenTime());
        dto.setCloseTime(source.getCloseTime());
        return dto;
    }
}
