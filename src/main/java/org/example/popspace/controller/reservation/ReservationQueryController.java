package org.example.popspace.controller.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.reservation.AvailableDateResponseDTO;
import org.example.popspace.dto.reservation.AvailableTimeResponseDTO;
import org.example.popspace.service.reservation.ReservationQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 예약 가능 날짜, 시간 조회
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ReservationQueryController {

    private final ReservationQueryService reservationQueryService;

    // 예약 페이지 랜더링 - 예약 가능 날짜
    @GetMapping("/api/popups/{popupId}/available-dates")
    public ResponseEntity<AvailableDateResponseDTO> getAvailableDates(@PathVariable Long popupId) {

        return ResponseEntity.ok(reservationQueryService.getAvailableDates(popupId));
    }


    // 선택한 날짜의 예약 가능 시간대
    @GetMapping("/api/popups/{popupId}/available-times")
    public ResponseEntity<AvailableTimeResponseDTO> getAvailableTimes(
            @PathVariable Long popupId,
            @RequestParam("date") LocalDate date
    ) {
        return ResponseEntity.ok(reservationQueryService.getAvailableTimes(popupId, date));
    }


}