package org.example.popspace.service.EntryEmail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.EntryEmail.Reservation;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.mapper.EntryEmailMapper;
import org.example.popspace.service.email.EmailService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WaitingService {

    private final EntryEmailMapper entryEmailMapper;
    private final EmailService mailService;

    /**
     * 추가 웨이팅 선발 (노쇼 수 만큼 추가 1회 선발)
     */
    public void processAdditionalWaiting(Long popupId, LocalDate date, String reserveTime, PopupInfoDto popup, int noShowCount) {
        int confirmedCount = entryEmailMapper.countConfirmedReservations(popupId, date, reserveTime);
        //잔여인원수 = 전체 예약 가능 수 - (CHECKED_IN 또는 CHECKEDOUT된 사전예약자) - (노쇼처리된 예약자)
        int vacancy = Math.max(popup.getMaxReservations() - confirmedCount - noShowCount, 0);
        if (vacancy == 0) return;

        //선발된 인원 EMAIL_PENDING 처리
        entryEmailMapper.updateWaitingReservationsToPending(popupId, date, vacancy);

        // 2단계: pending 상태인 사람만 조회
        List<Reservation> pendingList = entryEmailMapper.selectPendingReservations(popupId, date);

        pendingList.forEach(reservation -> {
            entryEmailMapper.updateReservationState(reservation.getReserveId(), "EMAIL_SEND");
            mailService.sendEnterNotification(reservation, popup.getPopupName(), popup.getLocation(), calculateEndTime(reserveTime));
        });
    }

    private String calculateEndTime(String reserveTimeStr) {
        LocalTime reserveTime = LocalTime.parse(reserveTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
        return reserveTime.plusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}

