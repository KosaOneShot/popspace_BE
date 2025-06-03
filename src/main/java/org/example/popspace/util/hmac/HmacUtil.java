package org.example.popspace.util.hmac;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HmacUtil {

    private static final String SECRET_KEY = "it-is-my-secret-key";
    private static final String ALGORITHM = "HmacSHA256";

    public static String generateSignature(String message) throws Exception {
        Mac hmac = Mac.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        hmac.init(keySpec);
        byte[] rawHmac = hmac.doFinal(message.getBytes());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
    }

    public static boolean verifySignature(String message, String signature) throws Exception {
        String expected = generateSignature(message);
        return expected.equals(signature);
    }
}
