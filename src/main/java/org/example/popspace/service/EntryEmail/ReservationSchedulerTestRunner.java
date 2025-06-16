package org.example.popspace.service.EntryEmail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.mapper.PopupMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationSchedulerTestRunner {

    private final ReservationProcessor processor;

    /**
     * 시간 모킹으로 테스트 실행
     */
    @Transactional
    public void manualRunWithMock(int minuteOffset, LocalDate targetDate, LocalTime targetTime) {
        log.info("manual run for mock");
        log.info("targetDate: {}", targetDate);
        log.info("targetTime: {}", targetTime);
        log.info("minuteOffset: {}", minuteOffset);
        LocalDateTime mockTime = LocalDateTime.of(targetDate, targetTime);
        processor.execute(minuteOffset, mockTime);
    }
}
