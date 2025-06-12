package org.example.popspace.dto.statistics;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HourlyVisitor {
    private int hour;
    private long count;

    public static HourlyVisitor of(int hour, long count) {
        return HourlyVisitor.builder()
                .hour(hour)
                .count(count)
                .build();
    }
}
