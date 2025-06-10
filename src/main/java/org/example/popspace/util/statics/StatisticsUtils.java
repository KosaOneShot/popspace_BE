package org.example.popspace.util.statics;

import org.example.popspace.dto.statistics.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatisticsUtils {

    // Constants
    public static final String TYPE_ADVANCE = "ADVANCE";
    public static final String TYPE_WALK_IN = "WALK_IN";
    public static final String STATE_NOSHOW = "NOSHOW";
    public static final String STATE_CANCELED = "CANCELED";
    public static final String SEX_MALE = "M";


    // Age classifier
    public static String classify(int age) {
        if (age < 10) return "under10";
        if (age < 20) return "age10s";
        if (age < 30) return "age20s";
        if (age < 40) return "age30s";
        if (age < 50) return "age40s";
        if (age < 60) return "age50s";
        return "over60";
    }

    public static AgeRatio toAgeRatio(List<ReservationMemberData> list, long entranceCount) {
        int currentYear = LocalDate.now().getYear();
        Map<String, Long> ageGroups = list.stream()
                .filter(data -> data.getBirthDate() != null)
                .map(data -> classify(data.getAge(currentYear)))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return AgeRatio.of(ageGroups, entranceCount);
    }


    // Statistics calculators
    public static long countAdvance(List<ReservationTypeStateCount> list) {
        return list.stream()
                .filter(m -> TYPE_ADVANCE.equals(m.getReservationType()))
                .count();
    }

}
