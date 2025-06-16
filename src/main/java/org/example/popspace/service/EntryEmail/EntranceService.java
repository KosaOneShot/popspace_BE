package org.example.popspace.service.EntryEmail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.EntryEmail.Reservation;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.mapper.EntryEmailMapper;
import org.example.popspace.service.email.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EntranceService {

    private final EntryEmailMapper entryEmailMapper;
    private final EmailService mailService;

    /**
     * 입장대상 선정 (사전예약 + 웨이팅 인원 선발)
     */
    @Transactional
    public void processEntrance(Long popupId, LocalDate today, String reserveTime, PopupInfoDto popup) {
        // 사전예약자 리스트 조회
        List<Reservation> advanceList = entryEmailMapper.selectAdvanceReservations(popupId, today, reserveTime);
        // 최대수용인원에서 사전예약자 제외한 vacancy 계산
        int vacancy = Math.max(popup.getMaxReservations() - advanceList.size(), 0);
        // 웨이팅에서 vacancy 수만큼 대기자 선발
        entryEmailMapper.updateWaitingReservationsToPending(popupId, today, vacancy);

        // pending 상태인 사람만 조회
        List<Reservation> pendingList = entryEmailMapper.selectPendingReservations(popupId, today);

        // 사전예약 입장대상 상태 업데이트 및 알림 발송
        for (Reservation reservation : advanceList) {
            entryEmailMapper.updateReservationState(reservation.getReserveId(), "EMAIL_SEND");
            mailService.sendEnterNotification(
                    reservation,
                    popup.getPopupName(),
                    popup.getLocation(),
                    calculateEndTime(reserveTime)
            );
        }

        // 웨이팅 입장대상 상태 업데이트 및 알림 발송
        for (Reservation reservation : pendingList) {
            entryEmailMapper.updateReservationState(reservation.getReserveId(), "EMAIL_SEND");
            mailService.sendEnterNotification(
                    reservation,
                    popup.getPopupName(),
                    popup.getLocation(),
                    calculateEndTime(reserveTime)
            );
        }
    }

    /**
     * 입장마감시간 계산기 (예약시간 + 10분 기준)
     * → 메일에 표시할 마감시간 문자열 생성 (HH:mm 포맷)
     */
    private String calculateEndTime(String reserveTimeStr) {
        LocalTime reserveTime = LocalTime.parse(reserveTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
        return reserveTime.plusMinutes(10).format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
