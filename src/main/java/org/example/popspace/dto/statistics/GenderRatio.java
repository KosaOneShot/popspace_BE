package org.example.popspace.dto.statistics;

import lombok.*;
import org.example.popspace.util.statics.MathUtil;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GenderRatio {
    private double maleRatio;
    private double femaleRatio;

    public static GenderRatio of(long maleCount,long entranceCount){
        return GenderRatio.builder()
                .maleRatio(MathUtil.toPercent(maleCount, entranceCount))
                .femaleRatio(MathUtil.toPercent(entranceCount - maleCount, entranceCount))
                .build();
    }
}
