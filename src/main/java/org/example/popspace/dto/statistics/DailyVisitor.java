package org.example.popspace.dto.statistics;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DailyVisitor {
    private LocalDate date;
    private long count;

    public static DailyVisitor of(LocalDate currentDay, long count) {
        return DailyVisitor.builder()
                .date(currentDay)
                .count(count)
                .build();
    }
}
