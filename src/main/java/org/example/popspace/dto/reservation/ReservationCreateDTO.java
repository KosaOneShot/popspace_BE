package org.example.popspace.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReservationCreateDTO {
    private Long reserveId;

    private Long memberId;
    private Long popupId;

    private LocalDate reserveDate;
    private String reserveTime;
    private String reservationState;
    private String reservationType;

    public static ReservationCreateDTO forAdvance(Long memberId, Long popupId, LocalDate date, String time) {
        ReservationCreateDTO dto = new ReservationCreateDTO();
        dto.setPopupId(popupId);
        dto.setMemberId(memberId);
        dto.setReserveDate(date);
        dto.setReserveTime(time);
        dto.setReservationState("RESERVED");
        dto.setReservationType("ADVANCE");
        return dto;
    }

    public static ReservationCreateDTO forWalkIn(Long memberId, Long popupId, LocalDate date) {
        ReservationCreateDTO dto = new ReservationCreateDTO();
        dto.setPopupId(popupId);
        dto.setMemberId(memberId);
        dto.setReserveDate(date);
        dto.setReserveTime(null); // 또는 생략
        dto.setReservationState("RESERVED");
        dto.setReservationType("WALK-IN");
        return dto;
    }

}
