package org.example.popspace.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.member.PasswordChangeRequest;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.MemberMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberMapper memberMapper;

    @Transactional
    public void changePassword(Long memberId, PasswordChangeRequest passwordChangeRequest) {
        String encodedOldPassword = passwordEncoder.encode(passwordChangeRequest.getOldPassword());
        String encodedNewPassword = passwordEncoder.encode(passwordChangeRequest.getNewPassword());

        int result =memberMapper.changePassword(memberId,encodedOldPassword,encodedNewPassword);
        if(result ==0){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
