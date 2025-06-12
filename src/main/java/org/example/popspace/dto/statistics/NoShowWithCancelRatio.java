package org.example.popspace.dto.statistics;

import lombok.*;
import org.example.popspace.util.statics.MathUtil;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NoShowWithCancelRatio {
    private double noShowRatio;
    private double cancelRatio;
    private double showRatio;

    public static NoShowWithCancelRatio of(long  noShowCount,long cancelCount,long advanceTotal) {
        return NoShowWithCancelRatio.builder()
                .noShowRatio(MathUtil.toPercent(noShowCount, advanceTotal))
                .cancelRatio(MathUtil.toPercent(cancelCount, advanceTotal))
                .showRatio(MathUtil.toPercent(advanceTotal - noShowCount - cancelCount, advanceTotal))
                .build();
    }
}
