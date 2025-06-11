package org.example.popspace.dto.popup;

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
public class PopupInfoDto {
    private Long popupId;
    private String popupName;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private String description;
    private String category;
    private int maxReservations;
    private String imageUrl;
    private Long memberId;
    private Long reviewId;
    private int rating;
    private String content;
    private LocalDateTime createdAt;
}
