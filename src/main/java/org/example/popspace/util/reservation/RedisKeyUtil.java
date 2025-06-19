package org.example.popspace.util.reservation;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RedisKeyUtil {

    // 예약에 필요한 팝업 정보
    // value: ReservationPopupCacheDTO (popupId, max, startDate, endDate, openTime, closeTime)
    public String popupInfoKey(Long popupId) {
        return String.format("popupinfo:%d", popupId);
    }

    // 시간대별 사전 에약 수
    // value: int
    public String advanceCountKey(Long popupId, LocalDate date, String time) {
        return String.format("advance:popup:%d:date:%s:time:%s", popupId, date, time);
    }

    // 해당 날짜에 이미 예약이 있는 멤버
    // value: Set(memberId)
    public String memberSetKey(Long popupId, LocalDate date) {
        return String.format("popup:%d:date:%s:members", popupId, date);
    }

    // 입장(예정) 고객 수
    // value: int
    public String entranceCountKey(Long popupId, LocalDate date, String time) {
        return String.format("entrance:popup:%d:date:%s:time:%s", popupId, date, time);
    }

    // 락
    public String entranceLockKey(Long popupId) {
        return String.format("popup:%d:entrance-lock", popupId);
    }

}

