package org.example.popspace.service.EntryEmail;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationProcessor processor;

    /**
     * 매시 정각 (00분) 예약 입장대상 선정 스케줄러
     * - 사전예약자 확정 및 입장 알림 발송
     * - 잔여 vacancy 만큼 현장 웨이팅 선발 후 입장 알림 발송
     */
    @Scheduled(cron = "0 30 9-22 * * *")
    @Transactional
    public void entranceSelection() {
        try {
            log.info("정각 스케줄러 실행");
            processor.execute(0, LocalDateTime.now());
        } catch (Exception e) {
            log.error("스케줄러 예외 발생", e);
        }
    }

    /**
     * 매시 5분: 노쇼처리 + 추가 웨이팅 선발 스케줄러
     * - 입장하지 않은 SELECTED → NO_SHOW 상태로 변경
     * - 노쇼 발생 수 만큼 추가 웨이팅 선발 및 입장 알림 발송
     */
    @Scheduled(cron = "0 10 9-22 * * *")
    @Transactional
    public void processAfterFiveMinutes() {
        log.info("10분 스케줄러 실행");
        processor.execute(10, LocalDateTime.now());
    }

    /**
     * 매시 10분: 최종 노쇼처리 스케줄러
     * - 추가 선발된 웨이팅 인원 중 입장하지 않은 인원 노쇼처리
     */
    @Scheduled(cron = "0 20 9-22 * * *")
    @Transactional
    public void processAfterTenMinutes() {
        log.info("20분 스케줄러 실행");
        processor.execute(20, LocalDateTime.now());
    }

    @PostConstruct
    public void test() {
        log.info("ReservationScheduler 빈 생성됨");
    }
}
