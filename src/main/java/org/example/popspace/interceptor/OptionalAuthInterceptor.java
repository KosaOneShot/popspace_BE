package org.example.popspace.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.util.auth.AuthUtil;
import org.example.popspace.util.auth.ExtractCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
@RequiredArgsConstructor
public class OptionalAuthInterceptor implements HandlerInterceptor {

    private final AuthUtil authUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)  {

        String token = ExtractCookie.extractTokenFromCookies(request, "accessToken");
        if (token != null) {
            try {
                authUtil.setAuthenticationFromToken(token);
                log.info("Optional 인증 완료");
            } catch (CustomException e) {
                log.warn("Optional 인증 실패, 무시: {}", e.getMessage());
            }
        }
        return true;
    }
}
