package org.example.popspace.util.hmac;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @PreDestroy
    public void destroy() {
        threadLocalMac.remove();
    }

    public String generateSignature(String message) {
        if (message == null) {
            throw new CustomException(ErrorCode.INVALID_SIGNATURE_INPUT);
        }

        Mac mac = threadLocalMac.get(); // init()에서 이미 예외 잡힘
        mac.reset();
        byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
    }

    public void verifySignature(String message, String signature) {
        String expected = generateSignature(message);
        if(!expected.equals(signature))
            throw new CustomException(ErrorCode.INVALID_SIGNATURE);
    }

}
