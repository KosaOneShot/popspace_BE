package org.example.popspace.controller.EntryEmail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.service.EntryEmail.ReservationScheduler;
import org.example.popspace.service.EntryEmail.ReservationSchedulerTestRunner;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SchedulerTestController {

    private final ReservationSchedulerTestRunner testRunner;

    //postman 테스트 url
    //ex) http://localhost:8080/run/mock?minuteOffset=10&targetDate=2025-06-16&targetTime=11:50
    @PostMapping("/run/mock")
    public String manualRunWithMock(
            @RequestParam int minuteOffset,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate targetDate,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime targetTime) {

        testRunner.manualRunWithMock(minuteOffset, targetDate, targetTime);
        return "Mock 실행 완료";
    }
}