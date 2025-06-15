package org.example.popspace.dto.qr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QrReservationDTO {
    private Long reserveId;
    private String reservationState;
    private String reservationType;
    private LocalDate reserveDate;
    private String reserveTime;

    private String popupName;

    private String memberName;
}
