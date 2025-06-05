package org.example.popspace.controller.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.service.qr.ReservationAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReservationAdminController {

    private final ReservationAdminService reservationAdminService;

    // 입장 처리
    @PostMapping("/admin/reservation/checkin")
    public ResponseEntity<String> checkIn(@RequestBody Map<String, Object> request) {
        Long reserveId = Long.valueOf(request.get("reserveId").toString());

        // 1. 팝업 사장 여부 검증
        reservationAdminService.validateOwnerAuthority(reserveId);

        // 2. 입장 처리
        reservationAdminService.checkIn(reserveId);
        return ResponseEntity.ok("입장 처리 완료");
    }

    // 퇴장 처리
    @PostMapping("/admin/reservation/checkout")
    public ResponseEntity<String> checkOut(@RequestBody Map<String, Object> request) {
        Long reserveId = Long.valueOf(request.get("reserveId").toString());

        // 1. 팝업 사장 여부 검증
        reservationAdminService.validateOwnerAuthority(reserveId);

        // 2. 퇴장 처리
        reservationAdminService.checkOut(reserveId);
        return ResponseEntity.ok("퇴장 처리 완료");
    }
}
