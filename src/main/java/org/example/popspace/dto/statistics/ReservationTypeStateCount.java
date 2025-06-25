package org.example.popspace.dto.statistics;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservationTypeStateCount {
    private String reservationType;
    private String reservationState;
    private Long cnt;
}
