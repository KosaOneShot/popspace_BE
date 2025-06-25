package org.example.popspace.util.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthUtil {

    private final JWTUtil jwtUtil;

    public void setAuthenticationFromToken(String token) {

        Map<String, Object> payload = jwtUtil.validateToken(token);

        CustomUserDetail customUserDetail = ParseUtil.extractUserDetailFromPayload(payload);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        customUserDetail, null, customUserDetail.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("인증 완료: {}", customUserDetail.getUsername());
    }
}
