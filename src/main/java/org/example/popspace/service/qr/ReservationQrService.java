package org.example.popspace.service.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.qr.QrReservationDTO;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.ReservationMapper;
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

    public QrReservationDTO processQrVerification(long userId, long reserveId, String sig){

        // 1. 팝업 사장 여부 검증
        validateOwnerAuthority(userId, reserveId);

        // 2. QR 서명 검증
        verifyQr(reserveId, sig);

        // 3. 예약 정보 조회
        QrReservationDTO dto = checkReservationStatus(reserveId);

        return dto;
    }

    // QR 생성
    public byte[] createQr(long reservationId) {
        log.info("Create Qr");

        String message = "reservationId=" + reservationId;
        String sig = hmacUtil.generateSignature(message);

        String url = "https://kosa-popspace.com/api/qr/verify?" + message + "&sig=" + sig;
        return qrCodeGenerator.generateQrImage(url);
    }

    // QR 유효성 검사
    private void verifyQr(long reservationId, String sig) {
        log.info("Verify Qr");
        String message = "reservationId=" + reservationId;

        hmacUtil.verifySignature(message, sig);

    }

    // Reservation 상태 반환
    private QrReservationDTO checkReservationStatus(long reservationId) {
        log.info("Check reservation status");

        return reservationMapper.findByReserveId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    // api 요청자가 팝업 사장인지 판단
    private void validateOwnerAuthority(long userId, long reservationId) {
        log.info("Validate owner authority");

        long popupOwnerId = reservationMapper.findPopupOwnerIdByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (userId != popupOwnerId) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }

}