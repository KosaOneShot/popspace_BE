package org.example.popspace.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private String email;
    private String nickname;
    private String memberName;
    private String phoneNumber;
    private String birthDate;
    private String sex;
    private String roadAddress;
    private String detailAddress;
}
