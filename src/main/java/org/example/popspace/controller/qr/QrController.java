package org.example.popspace.controller.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.qr.VerifyRequestDTO;
import org.example.popspace.dto.reservation.QrReservationDTO;
import org.example.popspace.service.qr.ReservationQrService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
public class QrController {

    private final ReservationQrService reservationQrService;

    // QR 생성
    @GetMapping(value = "/api/qr/{reservationId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQr(@PathVariable Long reservationId) {
        byte[] image = reservationQrService.createQr(reservationId);

        HttpHeaders headers = new HttpHeaders();

        // 캐시: public (브라우저 + CDN), max-age 1일 (86400초)
        headers.setCacheControl(CacheControl
                .maxAge(1, TimeUnit.DAYS)
                .cachePublic()
                .mustRevalidate()); // 캐시 무조건 유효성 검사

        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }


    // QR 유효성 검사, 예약 정보 반환
    @PostMapping("/api/qr/verify")
    public ResponseEntity<QrReservationDTO> verifyQr(@RequestBody VerifyRequestDTO request) {
        log.info("Received QR verify request");

        // 1. 팝업 사장 여부 검증
        reservationQrService.validateOwnerAuthority(request.getReservation_id());

        // 2. QR 서명 검증
        reservationQrService.verifyQr(request.getReservation_id(), request.getSig());

        // 3. 예약 정보 조회
        QrReservationDTO dto = reservationQrService.checkReservationStatus(request.getReservation_id());
        return ResponseEntity.ok(dto);
    }

}
