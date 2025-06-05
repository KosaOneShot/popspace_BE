package org.example.popspace.service.qr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.util.hmac.HmacUtil;
import org.example.popspace.util.qr.QrCodeGenerator;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReservationQrService {

    private final HmacUtil hmacUtil;

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


}