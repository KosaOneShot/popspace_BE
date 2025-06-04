package org.example.popspace.controller.auth;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.auth.MemberRegisterRequest;
import org.example.popspace.dto.auth.UserStateResponse;
import org.example.popspace.service.auth.UserDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(("/auth"))
@RequiredArgsConstructor
public class AuthController {
    private final UserDetailService userDetailsService;

    @GetMapping("/test")
    public ResponseEntity<String> test(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            log.info(cookie.getName() + ":" + cookie.getValue());
        }
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody MemberRegisterRequest memberRegisterRequest) {
        log.info("userRegisterRequestDTO: {}", memberRegisterRequest);

        userDetailsService.existsUserCheck(memberRegisterRequest);

        userDetailsService.registerUser(memberRegisterRequest);

        return ResponseEntity.ok("Success");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {

        userDetailsService.logout(response);

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

}
