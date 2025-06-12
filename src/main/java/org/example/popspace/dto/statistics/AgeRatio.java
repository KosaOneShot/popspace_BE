package org.example.popspace.dto.statistics;

import lombok.*;
import org.example.popspace.util.statics.MathUtil;

import java.util.Map;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AgeRatio {
    private double under10;
    private double age10s;
    private double age20s;
    private double age30s;
    private double age40s;
    private double age50s;
    private double over60;

    public static AgeRatio of( Map<String, Long> ageGroups,long entranceCount){
        return AgeRatio.builder()
                .under10(MathUtil.toPercent(ageGroups.getOrDefault("under10", 0L), entranceCount))
                .age10s(MathUtil.toPercent(ageGroups.getOrDefault("age10s", 0L), entranceCount))
                .age20s(MathUtil.toPercent(ageGroups.getOrDefault("age20s", 0L), entranceCount))
                .age30s(MathUtil.toPercent(ageGroups.getOrDefault("age30s", 0L), entranceCount))
                .age40s(MathUtil.toPercent(ageGroups.getOrDefault("age40s", 0L), entranceCount))
                .age50s(MathUtil.toPercent(ageGroups.getOrDefault("age50s", 0L), entranceCount))
                .over60(MathUtil.toPercent(ageGroups.getOrDefault("over60", 0L), entranceCount))
                .build();
    }
}
