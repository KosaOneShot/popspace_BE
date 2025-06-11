package org.example.popspace.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateRequest {
    private String nickname;
    private String roadAddress;
    private String detailAddress;
    private String birthDate;
    private String sex;
}
