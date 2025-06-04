package org.example.popspace.dto.auth;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserStateResponse {
    private String role;
    private String nickname;

    public static UserStateResponse of(String role, String nickname) {
        return UserStateResponse.builder()
                .role(role)
                .nickname(nickname)
                .build();
    }
}
