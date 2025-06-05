package org.example.popspace.util.hmac;

import lombok.extern.slf4j.Slf4j;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class HmacUtil {

    private String secretKey;
    private static final String ALGORITHM = "HmacSHA256";
    private SecretKeySpec keySpec;
    private ThreadLocal<Mac> threadLocalMac;

    // 개발용 임시 키
    public HmacUtil(@Value("${org.zerock.hmac.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    @PostConstruct
    public void init() {
        try {
            keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            threadLocalMac = ThreadLocal.withInitial(() -> {
                try {
                    Mac mac = Mac.getInstance(ALGORITHM);
                    mac.init(keySpec);
                    return mac;
                } catch (Exception e) {
                    throw new CustomException(ErrorCode.HMAC_INIT_FAILED);
                }
            });
        } catch (Exception e) {
            throw new CustomException(ErrorCode.HMAC_INIT_FAILED);
        }
    }

    public String generateSignature(String message) {
        try {
            Mac mac = threadLocalMac.get();
            mac.reset();
            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.GENERATE_SIGNATURE_FAILED);
        }
    }

    public boolean verifySignature(String message, String signature) {
        String expected = generateSignature(message);
        return expected.equals(signature);
    }

}
