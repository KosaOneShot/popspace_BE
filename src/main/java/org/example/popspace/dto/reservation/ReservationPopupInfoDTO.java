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
public class ReservationPopupInfoDTO {
    private Long popupId;
    private String popupName;

    private LocalDate startDate;
    private LocalDate endDate;
    private String openTime;   // 예: "10:30"
    private String closeTime;  // 예: "18:00"

    private int maxReservations;
}