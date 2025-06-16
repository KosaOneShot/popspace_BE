package org.example.popspace.controller.auth;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.auth.MemberRegisterRequest;
import org.example.popspace.dto.auth.ResetPasswordRequest;
import org.example.popspace.dto.auth.UserStateResponse;
import org.example.popspace.service.auth.UserDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(("/api/auth"))
@RequiredArgsConstructor
public class AuthController {
    private final UserDetailService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody MemberRegisterRequest memberRegisterRequest) {
        log.info("userRegisterRequestDTO: {}", memberRegisterRequest);

        userDetailsService.existsUserCheck(memberRegisterRequest);

        userDetailsService.registerUser(memberRegisterRequest);

        return ResponseEntity.ok("Success");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {

        userDetailsService.logout(request, response);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/check")
    public ResponseEntity<UserStateResponse> check(@AuthenticationPrincipal CustomUserDetail user) {
        log.info("check");
        log.info("user: {}", user.getRole());
        return ResponseEntity.ok(UserStateResponse.builder()
                .role(user.getRole())
                .nickname(user.getNickname())
                .build());
    }
    @PostMapping("/reset-password/verify-email")
    public ResponseEntity<String> sendResetCodeToEmail(@RequestBody Map<String, String> requestMap){
        String email =requestMap.get("email");
        userDetailsService.existsEmailAndSendEmail(email);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/reset-password/verify-code")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("resetPasswordRequest: {}", resetPasswordRequest);
        userDetailsService.validResetPasswordRequestAndSendEmail(resetPasswordRequest);
        return ResponseEntity.ok("Success");
    }
    @PostMapping("/email/check-duplication")
    public ResponseEntity<String> checkEmailDuplicate(@RequestBody Map<String,String> emailMap) {

        String email = emailMap.get("email");
        log.info("email: {}", email);
        userDetailsService.existsEmail(email);

        return ResponseEntity.ok("success");
    }

    @PostMapping("/nickname/check-duplication")
    public ResponseEntity<String> checkNicknameDuplicate(@RequestBody Map<String,String> nicknameMap) {

        String nickname = nicknameMap.get("nickname");
        log.info("nickname: {}", nickname);

        userDetailsService.existNickname(nickname);

        return ResponseEntity.ok("success");
    }
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("success");
    }
}
