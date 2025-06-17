package org.example.popspace.util.auth;

import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.auth.MemberLoginInfo;

import java.util.Map;

@Slf4j
public class ParseUtil {

    public static CustomUserDetail extractUserDetailFromPayload(Map<String, Object> payload) {

        MemberLoginInfo member = MemberLoginInfo.builder()
                .email((String) payload.get("email"))
                .nickname((String) payload.get("nickname"))
                .role((String) payload.get("role"))
                .memberId(((Number) payload.get("memberId")).longValue())
                .build();

        return CustomUserDetail.from(member);
    }

    public static Map<String, Object> createClaim(String email, long memberId, String nickname, String roleName) {
        return Map.of(
                "email", email,                          // username
                "memberId", memberId,
                "nickname", nickname,
                "role", roleName
        );
    }
}
