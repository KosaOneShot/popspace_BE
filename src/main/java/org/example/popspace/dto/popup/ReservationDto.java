package org.example.popspace.dto.popup;


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
public class ReservationDto {
    private Long memberId;
    private Long popupId;
    private Long reserveId;
    private String reservationState;
    private String reservationType;
}
