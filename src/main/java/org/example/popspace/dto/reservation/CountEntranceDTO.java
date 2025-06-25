package org.example.popspace.dto.reservation;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountEntranceDTO {
    private Integer currentCount;
    private Integer maxReservations;
    private Boolean isAllowed;

    public void checkIsAllowed(int myTurn) {
        this.isAllowed = currentCount + myTurn <= maxReservations;
    }
}
