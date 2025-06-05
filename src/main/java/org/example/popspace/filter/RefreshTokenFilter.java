package org.example.popspace.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.MemberLoginInfo;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.service.auth.UserDetailService;
import org.example.popspace.util.auth.ErrorResponseUtil;
import org.example.popspace.util.auth.ExtractCookie;
import org.example.popspace.util.auth.JWTUtil;
import org.example.popspace.util.auth.SendTokenUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final String refreshPath;
    private final JWTUtil jwtUtil;
    private final UserDetailService userDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("TokenCheckFilter path: {}", path);

        // 인증 없이 접근 가능한 URL은 통과
        if (!path.equals(refreshPath)) {
            log.info("인증 미필요 url 통과");
            filterChain.doFilter(request, response);
            return;
        }


        String token = ExtractCookie.extractTokenFromCookies(request, "refreshToken");

        if (token == null) {
            ErrorResponseUtil.sendJsonError(response, ErrorCode.NOT_FOUND_REFRESH_TOKEN);
            return;
        }

        try {
            // 토큰 검증 및 payload 추출
            Map<String, Object> payload = jwtUtil.validateToken(token);

            MemberLoginInfo userDTO = userDetailService.findByMemberId((((Number) payload.get("memberId")).longValue()) );

            //이상태까지 오면 무조건 AccessToken은 새로 생성
            Map<String, Object> claim = jwtUtil.createClaim
                    (userDTO.getEmail(), userDTO.getMemberId(), userDTO.getNickname(), userDTO.getRole());
            String accessTokenValue = jwtUtil.generateToken(claim, 5);

            String refreshTokenValue = null;
            if (shouldRenewRefreshToken((Integer) payload.get("exp"))) {
                log.info("🆕 Issuing new refresh token");
                refreshTokenValue = jwtUtil.generateToken(Map.of("memberId", userDTO.getMemberId()), 24 * 14);
            }
            log.info("accessTokenValue: {}", accessTokenValue);
            SendTokenUtil.sendTokens(accessTokenValue, refreshTokenValue, response, userDTO.getRole(), userDTO.getNickname());

        } catch (CustomException e) {
            ErrorResponseUtil.sendJsonError(response, e.getErrorCode());
        }
    }

    private boolean shouldRenewRefreshToken(Integer expEpochSeconds) {
        long remainingTimeMillis = (expEpochSeconds * 1000L) - System.currentTimeMillis();
        return remainingTimeMillis < (1000L * 60 * 60 * 24); // 1시간 이하
    }
}
