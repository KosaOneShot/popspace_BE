package org.example.popspace.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.util.auth.AuthUtil;
import org.example.popspace.util.auth.ErrorResponseUtil;
import org.example.popspace.util.auth.ExtractCookie;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


@Slf4j
@RequiredArgsConstructor
public class AccessTokenCheckFilter extends OncePerRequestFilter {

    private final String[] permittedUrls;
    private final AuthUtil authUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        //api 시작시 통과

        if(path.startsWith("/api/auth/health")){
            log.info("TokenCheckFilter 헬스 체크 통과: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        if (isPermittedPath(path)) {
            log.info("TokenCheckFilter 통과: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Token Check Filter..........................");
        log.info("TokenCheckFilter 검사: {}", path);

        String token = ExtractCookie.extractTokenFromCookies(request, "accessToken");

        if (token == null) {
            ErrorResponseUtil.sendJsonError(response, ErrorCode.NOT_FOUND_ACCESS_TOKEN);
            return;
        }

        try {

            authUtil.setAuthenticationFromToken(token);

            filterChain.doFilter(request, response);

        } catch (CustomException e) {
            ErrorResponseUtil.sendJsonError(response, e.getErrorCode());
        }
    }

    private boolean isPermittedPath(String path) {
        return Arrays.stream(permittedUrls).anyMatch(path::startsWith);
    }

}