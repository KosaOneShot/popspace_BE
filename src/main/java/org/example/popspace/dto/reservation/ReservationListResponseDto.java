package org.example.popspace.dto.reservation;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReservationListResponseDto {
    private Long reserveId;
    private String popupName;
    private LocalDate reserveDate;
    private String reserveTime;
    private String location;
    private String imageUrl;
    private String reservationType;
    private String reservationState;
}
