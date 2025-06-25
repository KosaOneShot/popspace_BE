package org.example.popspace.dto.statistics;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WeeklyVisitor {
    private int weekNumber;
    private List<DailyVisitor> dailyVisitors;

    public static WeeklyVisitor of(int week, List<DailyVisitor> dailyVisitors) {
        return WeeklyVisitor.builder()
                .weekNumber(week)
                .dailyVisitors(dailyVisitors)
                .build();
    }
}
