package org.example.popspace.dto.qr;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class ReservationStatusDTO {
    private Long popupId;
    private Long memberId;
    private Long reserveId;
    private String reservationState;
    private LocalDate reserveDate;
    private String reserveTime;
}
