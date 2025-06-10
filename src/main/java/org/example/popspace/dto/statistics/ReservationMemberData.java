package org.example.popspace.dto.statistics;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReservationMemberData {
    private Long memberId;
    private LocalDate birthDate;
    private String sex;
    private String reservationType;
    private String reservationState;
    private LocalDate reserveDate;
    private LocalDateTime reservationTime;
    private Double rating;
    private Long reservationId;
    private LocalDateTime createdAt;
    private String entranceState;

    public int getAge(int currentYear) {
        return currentYear - this.birthDate.getYear();
    }
}
