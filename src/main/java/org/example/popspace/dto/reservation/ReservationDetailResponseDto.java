package org.example.popspace.dto.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class ReservationDetailResponseDto {
    private Long reserveId;
    private LocalDate reserveDate;
    private String reserveTime;
    private LocalDateTime createdAt;
    private LocalDateTime canceledAt;
    private String reservationState;
    private String reservationType;
    private String memberName;
    private Long popupId;
    private String popupName;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private String description;
    private String category;
    private Integer maxReservations;
    private String imageUrl;
}
