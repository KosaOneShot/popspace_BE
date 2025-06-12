package org.example.popspace.dto.statistics;

import lombok.*;
import org.example.popspace.util.statics.MathUtil;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NoShowRatio {
    private double noShowRatio;
    private double showRatio;

    public static NoShowRatio of(long noShowCount, long advanceTotal) {
        return NoShowRatio.builder()
                .noShowRatio(MathUtil.toPercent(noShowCount, advanceTotal))
                .showRatio(MathUtil.toPercent(advanceTotal - noShowCount, advanceTotal))
                .build();
    }
}
