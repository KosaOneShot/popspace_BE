package org.example.popspace.util.auth;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.UserStateResponse;
import org.springframework.http.ResponseCookie;

import java.io.IOException;
import java.time.Duration;

@Slf4j
public class SendTokenUtil {

    private static final Gson gson = new Gson();

    public static void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response, String role, String nickname) throws IOException {
        addAccessTokenCookie(accessTokenValue, response);

        if (refreshTokenValue != null) {
            addRefreshTokenCookie(refreshTokenValue, response);
        }

        sendTokenRefreshedMessage(response, role, nickname);
        log.info("send tokens success");
    }

    public static void clearTokens(HttpServletResponse response) {
        deleteCookie("accessToken", response);
        deleteCookie("refreshToken", response);
        log.info("Cleared access and refresh tokens");
    }


    private static void addAccessTokenCookie(String token, HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .secure(true)
                .sameSite("Strict") // Optional: SameSite 설정도 권장
                .build();
        response.addHeader("Set-Cookie", accessTokenCookie.toString()); //
    }

    private static void addRefreshTokenCookie(String token, HttpServletResponse response) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .path("/")
                .maxAge(Duration.ofDays(14))
                .secure(true)
                .sameSite("Strict") // Optional: SameSite 설정도 권장
                .build();

        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }

    //상태 메시지 전송
    private static void sendTokenRefreshedMessage(HttpServletResponse response, String role, String nickname) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        String json = gson.toJson(UserStateResponse.of(role,nickname));

        response.getWriter().write(json);
    }

    private static void deleteCookie(String name, HttpServletResponse response) {
        ResponseCookie deletedCookie = ResponseCookie.from(name, "")
                .path("/")
                .httpOnly(true)
                .maxAge(0)               // 즉시 만료
                .sameSite("Strict")      // 동일하게 적용
                .secure(true)            // 동일하게 적용
                .build();
        response.addHeader("Set-Cookie", deletedCookie.toString());
    }
}
