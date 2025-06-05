package org.example.popspace.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.auth.MemberLoginInfo;
import org.example.popspace.mapper.redis.AuthRedisRepository;
import org.example.popspace.util.auth.ErrorResponseUtil;
import org.example.popspace.util.auth.ExtractCookie;
import org.example.popspace.util.auth.JWTUtil;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class AccessTokenCheckFilter extends OncePerRequestFilter {

    private final String[] permittedUrls;
    private final JWTUtil jwtUtil;
    private final AuthRedisRepository authRedisRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        //api 시작시 통과

        if (isPermittedPath(path)) {
            log.info("TokenCheckFilter 통과: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Token Check Filter..........................");

        String token = ExtractCookie.extractTokenFromCookies(request, "accessToken");

        if (token == null) {
            ErrorResponseUtil.sendJsonError(response, ErrorCode.NOT_FOUND_ACCESS_TOKEN);
            return;
        }

        try {
            if (authRedisRepository.checkBlackList(token)) {
                throw new CustomException(ErrorCode.TOKEN_BLACKLISTED);
            }

            Map<String, Object> payload = jwtUtil.validateToken(token);

            CustomUserDetail customUserDetail = extractUserDetailFromPayload(payload);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            customUserDetail, null, customUserDetail.getAuthorities());

            //컨텍스트에 인가 정보 추가
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            log.info("token filter pass");
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            ErrorResponseUtil.sendJsonError(response, e.getErrorCode());
        }
    }

    private CustomUserDetail extractUserDetailFromPayload(Map<String, Object> payload) {

        log.info(" payload {}", payload);
        MemberLoginInfo member= MemberLoginInfo.builder()
                .email((String) payload.get("email"))
                .nickname((String) payload.get("nickname"))
                .role((String) payload.get("role"))
                .memberId(((Number) payload.get("memberId")).longValue())
                .build();

        return CustomUserDetail.from(member);
    }
    private boolean isPermittedPath(String path) {
        return Arrays.stream(permittedUrls).anyMatch(path::startsWith);
    }

}