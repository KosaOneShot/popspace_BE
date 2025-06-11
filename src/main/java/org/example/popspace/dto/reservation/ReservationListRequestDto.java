package org.example.popspace.dto.reservation;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReservationListRequestDto {
    private String searchKeyword;
    private LocalDate searchDate;
    private String reservationType; // 예약 타입 (ADVANCE, WALK_IN)
}
