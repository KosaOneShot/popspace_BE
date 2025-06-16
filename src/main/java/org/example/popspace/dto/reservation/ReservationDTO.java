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
public class ReservationDTO {
    private Long reserveId;
    private Long memberId;

    private Long popupId;

    private LocalDate reserveDate;
    private String reserveTime;

    private String reservationState;
    private String reservationType;
}
