package org.example.popspace.service.EntryEmail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.mapper.PopupMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationProcessor {

    private final PopupMapper popupMapper;
    private final EntranceService entranceService;
    private final NoShowService noShowService;
    private final ReservationWaitingService waitingService;

    /**
     * 공통 스케줄러 엔진 (분 단위 오프셋에 따라 동작 분기)
     * minuteOffset: 현재 스케줄링 시점 (0분 / 5분 / 10분)
     */
    public void execute(int minuteOffset, LocalDateTime baseTime) {
        LocalDateTime now = baseTime.withSecond(0).withNano(0).minusMinutes(minuteOffset);
        LocalDate today = now.toLocalDate();
        String nowTime = now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        // 현재 영업중인 활성화된 팝업 ID 조회 (오늘 날짜 + 현재 시간 기준)
        List<Long> popupIds = popupMapper.selectActivePopups(today, nowTime);
        log.info("Active popups: {}", popupIds);

//        List<Long> popupIds = List.of(40L); // 테스트용

        // 팝업 상세정보 한번에 조회

        List<PopupInfoDto> popupList = popupMapper.findPopupInfoAndReviewsByPopupIds(popupIds);
        Map<Long, PopupInfoDto> popupMap = popupList.stream()
                .collect(Collectors.toMap(PopupInfoDto::getPopupId, Function.identity()));

        log.info("popupMap: {}", popupMap);
        // 각 활성화된 팝업마다 순회 처리
        for (Long popupId : popupIds) {
            PopupInfoDto popup = popupMap.get(popupId);
            switch (minuteOffset) {
                // 입장대상 선정
                case 0 -> entranceService.processEntrance(popupId, today, nowTime, popup);
                // 1차 노쇼처리 + 추가 웨이팅 선발
                case 10 -> {
                    noShowService.processNoShow(popupId, today, nowTime);
                    waitingService.processAdditionalWaiting(popupId, today, nowTime, popup);
                }
                // 2차 최종 노쇼처리
                case 20 -> noShowService.processNoShow(popupId, today, nowTime);
            }
        }
    }
}
