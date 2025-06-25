package org.example.popspace.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterRequest {

    private Long userId;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "이름은 필수입니다.")
    private String memberName;

    private String role;


    @NotBlank(message = "성별은 필수입니다.")
    private String sex;

    private LocalDate birthDate;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;

    @NotBlank(message = "도로명 주소는 필수입니다.")
    private String roadAddress;

    private String detailAddress;

    private String agreement; // 약관 동의 여부

    public void encodePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
