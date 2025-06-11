package org.example.popspace.controller.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.member.PasswordChangeRequest;
import org.example.popspace.service.member.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest, @AuthenticationPrincipal CustomUserDetail user) {

        Long id = user.getId();
        memberService.changePassword(id,passwordChangeRequest);
        return ResponseEntity.ok().body("success");
    }
}
