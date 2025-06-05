package org.example.popspace.service.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.reservation.QrReservationDTO;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.ReservationMapper;
import org.example.popspace.util.hmac.HmacUtil;
import org.example.popspace.util.qr.QrCodeGenerator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReservationQrService {

    private final HmacUtil hmacUtil;
    private final ReservationMapper reservationMapper;

    // QR 생성
    public byte[] createQr(Long reservationId) {
        try {
            String message = "reservation_id=" + reservationId;
            String sig = hmacUtil.generateSignature(message);
            String url = "https://kosa-popspace.com/api/qr/verify?" + message + "&sig=" + sig;
            return QrCodeGenerator.generateQrImage(url);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.QR_GENERATION_FAILED);
        }
    }

    // QR 유효성 검사
    public void verifyQr(Long reservationId, String sig) {
        String message = "reservation_id=" + reservationId;

        try {
            boolean result = hmacUtil.verifySignature(message, sig);
            if (!result) {
                log.error("Invalid signature error");
                throw new CustomException(ErrorCode.INVALID_SIGNATURE);
            }
        } catch (CustomException e) {
            // 이미 처리된 예외는 다시 던짐
            throw e;
        } catch (Exception e) {
            log.error("Signature verification error", e);
            throw new CustomException(ErrorCode.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    // Reservation 상태 반환
    public QrReservationDTO checkReservationStatus(Long reservationId) {
        return reservationMapper.findByReserveId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    // api 요청자가 팝업 사장인지 판단
    public void validateOwnerAuthority(Long reservationId) {
        CustomUserDetail user = (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = user.getId();

        Long popupOwnerId = reservationMapper.findPopupOwnerIdByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        log.info("login member id: " + memberId);
        log.info("Popup owner id: " + popupOwnerId);

        if (!memberId.equals(popupOwnerId)) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
    }



}