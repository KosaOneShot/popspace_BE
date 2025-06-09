package org.example.popspace.controller.qr;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.qr.CheckInOutRequestDTO;
import org.example.popspace.service.qr.ReservationAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationAdminService reservationAdminService;

    // 입장 처리
    @PostMapping("/admin/reservation/checkin")
    public ResponseEntity<String> checkIn(@RequestBody @Valid CheckInOutRequestDTO request,
                                          @AuthenticationPrincipal CustomUserDetail userDetail) {

        reservationAdminService.validateAndCheckIn(userDetail.getId(), request.getReserveId());

        return ResponseEntity.ok("입장 처리 완료");
    }

    // 퇴장 처리
    @PostMapping("/admin/reservation/checkout")
    public ResponseEntity<String> checkOut(@RequestBody @Valid CheckInOutRequestDTO request,
                                           @AuthenticationPrincipal CustomUserDetail userDetail) {

        reservationAdminService.validateAndCheckOut(userDetail.getId(), request.getReserveId());

        return ResponseEntity.ok("퇴장 처리 완료");
    }
}
