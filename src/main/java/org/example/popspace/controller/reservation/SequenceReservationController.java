package org.example.popspace.controller.reservation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.reservation.ReservationSequenceResponse;
import org.example.popspace.service.reservation.WaitingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class SequenceReservationController {

    private final WaitingService waitingService;

    @GetMapping("/{popupId}/reservation/{reservationId}/sequence")
    public ResponseEntity<ReservationSequenceResponse> getMyWaitingSequenceInfo(@PathVariable Long reservationId,@PathVariable Long popupId) {


        ReservationSequenceResponse reservationSequence= waitingService.getMyWaitingSequenceInfo(reservationId,popupId);
        return ResponseEntity.ok(reservationSequence);
    }

    @GetMapping("/{popupId}/reservation/total/sequence")
    public ResponseEntity<ReservationSequenceResponse> getTotalWaitingInfo(@PathVariable Long popupId) {

        ReservationSequenceResponse reservationSequence= waitingService.getTotalWaitingInfo(popupId);
        return ResponseEntity.ok(reservationSequence);
    }
}
