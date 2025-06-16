package org.example.popspace.dto.EntryEmail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    private Long reserveId;
    private Long popupId;
    private Long memberId;

    private LocalDate reserveDate;   // 날짜부분만
    private LocalTime reserveTime;   // 시간부분만

    private String reservationType;
    private String reservationState;

    private String email;
}
