package org.example.popspace.dto.auth;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberLoginInfo {
    private Long memberId;
    private String email;
    private String password;
    private String nickname;
    private String role;
}
