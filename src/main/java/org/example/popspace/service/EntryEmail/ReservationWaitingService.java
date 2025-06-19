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
public class ReservationWaitingService {

    private final EntryEmailMapper entryEmailMapper;
    private final EmailService mailService;
    private final EntranceStateUpdateService entranceStateUpdateService;

    /**
     * 추가 웨이팅 선발 (노쇼 수 만큼 추가 1회 선발)
     */
    @Transactional
    public void processAdditionalWaiting(Long popupId, LocalDate date, String reserveTime, PopupInfoDto popup) {
        log.info("Processing reservation with popupId: " + popupId);
        log.info("Reservation date: " + date);
        log.info("Reservation time: " + reserveTime);
        int confirmedCount = entryEmailMapper.countConfirmedReservations(popupId, date, reserveTime);
        log.info("Confirmed reservations: {}", confirmedCount);
        int vacancy = Math.max(popup.getMaxReservations() - confirmedCount, 0);
        log.info("Vacancy: {}", vacancy);
        if (vacancy == 0) return;

        //선발된 인원 EMAIL_PENDING 처리
        entryEmailMapper.updateWaitingReservationsToPending(popupId, date, vacancy);

        // 2단계: pending 상태인 사람만 조회
        List<Reservation> pendingList = entryEmailMapper.selectPendingReservations(popupId, date);

        entranceStateUpdateService.updateReservations(pendingList, reserveTime);
        sendEmails(pendingList, popup, reserveTime);
    }

    private String calculateEndTime(String reserveTimeStr) {
        LocalTime reserveTime = LocalTime.parse(reserveTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
        return reserveTime.plusMinutes(20).format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public void sendEmails(List<Reservation> reservations, PopupInfoDto popup, String reserveTime) {
        for (Reservation reservation : reservations) {
            try {
                long start = System.currentTimeMillis();
                log.info("📧 Sending email for reservationId={}, email={}", reservation.getReserveId(), reservation.getEmail());

                mailService.sendEnterNotification(
                        reservation,
                        popup.getPopupName(),
                        popup.getLocation(),
                        calculateEndTime(reserveTime)
                );

                long end = System.currentTimeMillis();
                log.info("✅ Email sent to {} (took {} ms)", reservation.getEmail(), (end - start));

            } catch (Exception e) {
                log.error("❌ 이메일 전송 실패: {}", reservation.getEmail(), e);
            }
        }
    }
}

