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

}
