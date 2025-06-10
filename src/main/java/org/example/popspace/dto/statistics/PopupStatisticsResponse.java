package org.example.popspace.dto.statistics;


import lombok.*;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PopupStatisticsResponse {
    private String popupName;
    private long totalReservationCount;
    private long totalEntranceCount; // checked_in 만 처리
    private long likeCount;
    private long reviewCount;
    private double averageRating;
    private GenderRatio genderRatio;
    private AgeRatio ageRatio;
    private NoShowWithCancelRatio advanceNoShowRatio;
    private NoShowRatio walkInNoShowRatio;
    private List<WeeklyVisitor> weeklyVisitors;
    private List<HourlyVisitor> hourlyVisitors;
}
