package org.example.popspace.dto.reservation;

import lombok.*;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReservationSequenceResponse {
    private Integer sequence;
    private int averageWaitTime;
    private LocalTime entranceTime;  // 예: 14:30
    private boolean isAllowed;

    public static ReservationSequenceResponse of(Integer sequence,Integer averageWaitTime ,LocalTime entranceTime,CountEntranceDTO countEntrance) {
        return ReservationSequenceResponse.builder()
                .sequence(sequence)
                .averageWaitTime(averageWaitTime)
                .entranceTime(entranceTime)
                .isAllowed(countEntrance.getIsAllowed())
                .build();
    }
}
