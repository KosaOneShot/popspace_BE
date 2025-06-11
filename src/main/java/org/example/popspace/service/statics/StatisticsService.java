package org.example.popspace.service.statics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.statistics.*;
import org.example.popspace.mapper.PopupMapper;
import org.example.popspace.util.statics.StatisticsUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {

    private final PopupMapper popupMapper;

    @Transactional
    public PopupStatisticsResponse findPopupStatistics(Long popupId) {

        //입장 인원 정보
        List<ReservationMemberData> entranceMembers =
                popupMapper.findStatisticsDataDtoByPopupId(popupId);
        log.info("Entrance members {}", entranceMembers);

        //예약자 관련 정보
        List<ReservationTypeStateCount> reservationStats =
                popupMapper.findReservationData(popupId);

        //팝업 기본정보+like 수
        PopupInfoWithLikeCount popupInfoWithLikeCount =
                popupMapper.findPopupWithLikeCount(popupId);

        return calculateStatistics(entranceMembers,reservationStats,popupInfoWithLikeCount);
    }

    private PopupStatisticsResponse calculateStatistics(List<ReservationMemberData> entranceMembers,
                                                        List<ReservationTypeStateCount> reservationStats,
                                                        PopupInfoWithLikeCount popupInfoWithLikeCount) {
        long reviewCount = StatisticsUtils.countReview(entranceMembers);
        log.info("리뷰 개수 {}", reviewCount);

        double averageRating = StatisticsUtils.averageRating(entranceMembers, reviewCount);
        log.info("리뷰 평점 {}", averageRating);

        long totalReservationCount = StatisticsUtils.calculateTotalReservationCount(reservationStats);
        log.info("총 예약자 수 {}", totalReservationCount);

        long entranceCount = entranceMembers.size();
        log.info("입장 인원 수 {}", entranceCount);

        long advanceTotal = StatisticsUtils.countAdvance(reservationStats);
        log.info("사전 예약 수 {}", advanceTotal);

        GenderRatio genderRatio = StatisticsUtils.createGenderRatio(entranceMembers, entranceCount);
        log.info("입장 인원 성비{}", genderRatio);

        NoShowWithCancelRatio advanceNoShowWithCancelRatio = StatisticsUtils.createAdvanceNoShowWithCancel(reservationStats, advanceTotal, "ADVANCE");
        log.info("사전예약 노쇼, 캔슬 비율 {}", advanceNoShowWithCancelRatio);

        NoShowRatio walkInNoShowRatio = StatisticsUtils.createWalkInNoShow(reservationStats, entranceCount - advanceTotal, "WALK_IN");
        log.info("웨이팅 노쇼 비율 {}", walkInNoShowRatio);

        AgeRatio ageRatio = StatisticsUtils.toAgeRatio(entranceMembers, entranceCount);
        log.info("입장 인원 나이 비율{}", ageRatio);

        List<HourlyVisitor> hourlyVisitors = StatisticsUtils.
                analyzeHourly(entranceMembers, popupInfoWithLikeCount.getOpenTime(), popupInfoWithLikeCount.getCloseTime());
        log.info("시간대별 입장인원 {}", hourlyVisitors);

        List<WeeklyVisitor> weeklyVisitors = StatisticsUtils.
                analyzeWeekly(entranceMembers, popupInfoWithLikeCount.getStartDate(), popupInfoWithLikeCount.getEndDate());
        log.info("일별 입장인원 주간별 {}", weeklyVisitors);

        return PopupStatisticsResponse.builder()
                .popupName(popupInfoWithLikeCount.getPopupName())
                .totalReservationCount(totalReservationCount)
                .totalEntranceCount(entranceCount)
                .likeCount(popupInfoWithLikeCount.getLikeCount())
                .reviewCount(reviewCount)
                .averageRating(averageRating)
                .genderRatio(genderRatio)
                .ageRatio(ageRatio)
                .advanceNoShowRatio(advanceNoShowWithCancelRatio)
                .walkInNoShowRatio(walkInNoShowRatio)
                .weeklyVisitors(weeklyVisitors)
                .hourlyVisitors(hourlyVisitors)
                .build();
    }
}
