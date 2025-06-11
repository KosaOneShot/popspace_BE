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


    // Time analyzers
    private static final int DAYS_IN_WEEK = 7;
    private static final int WEEK_END_OFFSET = 6;


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

    public static GenderRatio createGenderRatio(List<ReservationMemberData> list, long total) {
        long male = list.stream()
                .filter(m -> SEX_MALE.equals(m.getSex()))
                .count();
        return GenderRatio.of(male, total);
    }

    public static NoShowWithCancelRatio createAdvanceNoShowWithCancel(
            List<ReservationTypeStateCount> list, long total, String type) {

        long noShow = list.stream()
                .filter(m -> type.equals(m.getReservationType()) &&
                        STATE_NOSHOW.equals(m.getReservationState()))
                .count();

        long cancel = list.stream()
                .filter(m -> type.equals(m.getReservationType()) &&
                        STATE_CANCELED.equals(m.getReservationState()))
                .count();

        return NoShowWithCancelRatio.of(noShow, cancel, total);
    }

    public static NoShowRatio createWalkInNoShow(List<ReservationTypeStateCount> list, long total, String type) {
        long noShow = list.stream()
                .filter(m -> type.equals(m.getReservationType()) &&
                        STATE_NOSHOW.equals(m.getReservationState()))
                .count();

        return NoShowRatio.of(noShow, total);
    }

    public static long countReview(List<ReservationMemberData> list) {
        return list.stream()
                .filter(m -> m.getRating() != null)
                .count();
    }

    public static double averageRating(List<ReservationMemberData> list, long count) {
        double sum = list.stream()
                .filter(m -> m.getRating() != null)
                .mapToDouble(ReservationMemberData::getRating)
                .sum();
        return count == 0 ? 0.0 : sum / count;
    }


    public static List<HourlyVisitor> analyzeHourly(List<ReservationMemberData> list,
                                                    LocalDateTime open, LocalDateTime close) {
        Map<Integer, Long> hourCount = list.stream()
                .filter(d -> d.getCreatedAt() != null)
                .collect(Collectors.groupingBy(d -> d.getCreatedAt().getHour(), Collectors.counting()));

        return IntStream.rangeClosed(open.getHour(), close.getHour())
                .mapToObj(hour -> HourlyVisitor.of(hour, hourCount.getOrDefault(hour, 0L)))
                .toList();
    }

    public static List<WeeklyVisitor> analyzeWeekly(List<ReservationMemberData> list,
                                                    LocalDate startDate, LocalDate endDate) {

        Map<LocalDate, Long> dateCount = list.stream()
                .filter(data -> data.getReserveDate() != null)
                .collect(Collectors.groupingBy(ReservationMemberData::getReserveDate, Collectors.counting()));

        List<WeeklyVisitor> result = new ArrayList<>();
        LocalDate current = startDate.with(DayOfWeek.MONDAY);
        int week = 1;

        while (!current.isAfter(endDate)) {
            LocalDate weekStart = current;
            LocalDate weekEnd = weekStart.plusDays(WEEK_END_OFFSET).isAfter(endDate)
                    ? endDate
                    : weekStart.plusDays(WEEK_END_OFFSET);

            List<DailyVisitor> daily = IntStream.range(0, DAYS_IN_WEEK)
                    .mapToObj(weekStart::plusDays)
                    .takeWhile(d -> !d.isAfter(weekEnd))
                    .map(d -> DailyVisitor.of(d, dateCount.getOrDefault(d, 0L)))
                    .toList();

            result.add(WeeklyVisitor.of(week++, daily));
            current = current.plusWeeks(1);
        }

        return result;
    }

    public static Long calculateTotalReservationCount( List<ReservationTypeStateCount> reservationStats){
        return reservationStats.stream()
                .mapToLong(ReservationTypeStateCount::getCnt)
                .sum();
    }
}
