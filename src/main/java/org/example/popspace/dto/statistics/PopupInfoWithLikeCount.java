package org.example.popspace.dto.statistics;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PopupInfoWithLikeCount {
    private String popupName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private int likeCount;
}
