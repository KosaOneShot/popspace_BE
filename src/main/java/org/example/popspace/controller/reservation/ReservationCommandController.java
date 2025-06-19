package org.example.popspace.controller.reservation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.reservation.AdvanceRequestDTO;
import org.example.popspace.dto.reservation.ReservationResponseDTO;
import org.example.popspace.dto.reservation.WalkInRequestDTO;
import org.example.popspace.service.reservation.ReservationCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 예약 생성, 취소
 */
@RestController
@RequiredArgsConstructor
public class ReservationCommandController {

    private final ReservationCommandService reservationCommandService;

    // 사전 예약 생성
    @PostMapping("/api/reservation/advance")
    public ResponseEntity<ReservationResponseDTO> makeAdvanceReservation(@Valid @RequestBody AdvanceRequestDTO advanceRequest,
                                                                         @AuthenticationPrincipal CustomUserDetail userDetail) {

        Long reserveId = reservationCommandService.makeAdvanceReservation(userDetail.getId(), advanceRequest);

        ReservationResponseDTO response = new ReservationResponseDTO();
        response.setMessage("예약이 완료되었습니다");
        response.setReserveId(reserveId);
        return ResponseEntity.ok(response);
    }

    // 웨이팅 (즉시 입장x) 생성
    @PostMapping("/api/reservation/walk-in")
    public ResponseEntity<ReservationResponseDTO> makeWalkInReservation(@Valid @RequestBody WalkInRequestDTO walkInRequest,
                                                                         @AuthenticationPrincipal CustomUserDetail userDetail) {

        Long reserveId = reservationCommandService.makeWalkInReservation(userDetail.getId(), walkInRequest);

        ReservationResponseDTO response = new ReservationResponseDTO();
        response.setMessage("웨이팅 등록되었습니다");
        response.setReserveId(reserveId);
        return ResponseEntity.ok(response);
    }

    // 즉시 입장 웨이팅 생성
    @PostMapping("/api/reservation/immediate-walk-in")
    public ResponseEntity<ReservationResponseDTO> makeImmediateWalkIn(@Valid @RequestBody WalkInRequestDTO walkInRequest,
                                                                        @AuthenticationPrincipal CustomUserDetail userDetail) {

        Long reserveId = reservationCommandService.makeImmediateWalkIn(userDetail.getId(), walkInRequest);

        ReservationResponseDTO response = new ReservationResponseDTO();
        response.setMessage("즉시 입장 가능합니다.");
        response.setReserveId(reserveId);
        return ResponseEntity.ok(response);
    }

    // 사전 예약 취소
    @PostMapping("/api/reservations/{reserveId}/cancel")
    public ResponseEntity<String> cancelReservation(
            @PathVariable Long reserveId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        reservationCommandService.cancelReservation(userDetail.getId(), reserveId);
        return ResponseEntity.ok("예약이 취소되었습니다");
    }

    // 노쇼 처리 - 예약, 웨이팅 - (확인용)
    @PostMapping("/api/reservations/{reserveId}/noshow")
    public ResponseEntity<String> noshowReservation(
            @PathVariable Long reserveId,
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        reservationCommandService.noshowReservation(reserveId);
        return ResponseEntity.ok("노쇼 처리되었습니다");
    }

}
