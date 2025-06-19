package org.example.popspace.service.EntryEmail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.EntryEmail.Reservation;
import org.example.popspace.mapper.EntryEmailMapper;
import org.example.popspace.mapper.redis.ReservationRedisRepository;
import org.example.popspace.util.reservation.RedisKeyUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EntranceStateUpdateService {

    private final EntryEmailMapper entryEmailMapper;
    private final RedisKeyUtil redisKeyUtil;
    private final ReservationRedisRepository redisRepo;

    @Transactional
    public void updateReservations(List<Reservation> reservations, String reserveTime) {
        for (Reservation reservation : reservations) {
            entryEmailMapper.updateReservationState(reservation.getReserveId(), "EMAIL_SEND", reserveTime);
            incrementCurrentCount(reservation.getPopupId(), reservation.getReserveDate(), reserveTime);
        }
    }

    @Transactional
    public void processPreviousHourReservations(Long popupId, LocalDateTime now) {
        // 현재 시간 기준 1시간 전 시간 계산
        LocalDate prevDate = now.minusHours(1).toLocalDate();
        String prevTime = now.minusHours(1).toLocalTime().withMinute(0).format(DateTimeFormatter.ofPattern("HH:mm"));

        log.info("전 시간대 처리 시작: {} {}", prevDate, prevTime);

        // 1) SEND_EMAIL → NO_SHOW
        int noShowUpdated = entryEmailMapper.updateReservationStateBatch(popupId, prevDate,"EMAIL_SEND", "NOSHOW");

        // 2) CHECKED_IN → CHECKED_OUT
        int checkoutUpdated = entryEmailMapper.updateReservationStateBatch(popupId, prevDate,"CHECKED_IN", "CHECKED_OUT");

        log.info("NO_SHOW 처리 완료: {}건, CHECKED_OUT 처리 완료: {}건", noShowUpdated, checkoutUpdated);
    }

    public void incrementCurrentCount(Long popupId, LocalDate reserveDate, String reserveTime) {
        String countkey = redisKeyUtil.advanceCountKey(popupId, reserveDate, reserveTime);
        redisRepo.incrementCount(countkey);
    }
}