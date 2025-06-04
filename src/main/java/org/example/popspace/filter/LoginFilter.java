package org.example.popspace.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.util.auth.ErrorResponseUtil;
import org.example.popspace.util.auth.JsonUtil;
import org.example.popspace.global.error.ErrorCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.util.Map;

@Slf4j

public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    //AbstractAuthenticationProcessingFilter에게 url 설정
    public LoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    //인증 시도할때 하는 메소드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Map<String, String> credentials = JsonUtil.parseJsonToMap(request);

        log.info("credentials: " + credentials);

        if (credentials == null || credentials.get("email") == null || credentials.get("password") == null) {
            ErrorResponseUtil.sendJsonError(response, ErrorCode.INVALID_LOGIN_REQUEST);
            return null;
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        credentials.get("email"),
                        credentials.get("password"));

        return getAuthenticationManager().authenticate(authenticationToken);
    }
}
