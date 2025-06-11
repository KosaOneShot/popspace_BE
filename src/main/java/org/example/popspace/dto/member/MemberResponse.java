package org.example.popspace.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
