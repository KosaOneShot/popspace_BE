package org.example.popspace.dto.reservation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ReservationStatusDTO {
    private Long reservationId;
    private String reservationState;
}
