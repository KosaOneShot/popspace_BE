package org.example.popspace.service.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.qr.QrReservationDTO;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.ReservationMapper;
import org.example.popspace.service.common.OwnerAuthorityValidator;
import org.example.popspace.util.hmac.HmacUtil;
import org.example.popspace.util.qr.QrCodeGenerator;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReservationQrService {

    private final HmacUtil hmacUtil;
    private final QrCodeGenerator qrCodeGenerator;
    private final ReservationMapper reservationMapper;
    private final OwnerAuthorityValidator authorityValidator;

    public QrReservationDTO processQrVerification(long memberId, long reserveId, String sig){

        // 1. 팝업 사장 여부 검증
        authorityValidator.validatePopupOwnerByReservation(memberId, reserveId);

        // 2. QR 서명 검증
        verifyQr(reserveId, sig);

        // 3. 예약 정보 조회
        QrReservationDTO dto = checkReservationStatus(reserveId);

        return dto;
    }

    // QR 생성
    public byte[] createQr(long reserveId) {
        log.info("Create Qr");

        String message = "reserveId=" + reserveId;
        String sig = hmacUtil.generateSignature(message);

        String url = "https://kosa-popspace.com/api/qr/verify?" + message + "&sig=" + sig;
        return qrCodeGenerator.generateQrImage(url);
    }

    // QR 유효성 검사
    private void verifyQr(long reserveId, String sig) {
        log.info("Verify Qr");
        String message = "reserveId=" + reserveId;

        hmacUtil.verifySignature(message, sig);

    }

    // Reservation 상태 반환
    private QrReservationDTO checkReservationStatus(long reserveId) {
        log.info("Check reservation status");

        return reservationMapper.findByReserveId(reserveId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }


}