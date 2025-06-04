package org.example.popspace.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.util.auth.JWTUtil;
import org.example.popspace.util.auth.SendTokenUtil;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("Login Success Handler................................");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();

        log.info("핸들러 {}", userDetail); //username

//        Map<String, Object> claim = Map.of("email", authentication.getName());
        Map<String, Object> claim = jwtUtil.createClaim(userDetail.getEmail(),
                userDetail.getId(),
                userDetail.getNickname(),
                userDetail.getRole());

        log.info("claim" + claim);

        //Access Token 유효기간 1일
        String accessToken = jwtUtil.generateToken(claim, 5);
        //Refresh Token 유효기간 30일
        String refreshToken = jwtUtil.generateToken(Map.of("memberId", ((CustomUserDetail) authentication.getPrincipal()).getUserDetailId()), 24 );

        SendTokenUtil.sendTokens(accessToken, refreshToken, response, userDetail.getRole(), userDetail.getNickname());
    }
}
