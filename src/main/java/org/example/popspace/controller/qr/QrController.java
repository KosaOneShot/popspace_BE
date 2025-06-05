package org.example.popspace.controller.qr;

import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.qr.VerifyRequestDTO;
import org.example.popspace.service.qr.ReservationQrService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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


    // QR 유효성 검사
    @PostMapping("/api/qr/verify")
    public ResponseEntity<String> verifyQr(@RequestBody VerifyRequestDTO request) {
        log.info("Received QR verify request");
        reservationQrService.verifyQr(request.getReservation_id(), request.getSig());
        return ResponseEntity.ok("QR 유효함");
    }

}
