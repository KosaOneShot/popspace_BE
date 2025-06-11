package org.example.popspace.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.member.MemberResponse;
import org.example.popspace.dto.member.MemberUpdateRequest;
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
        String oldRaw = passwordChangeRequest.getOldPassword();
        String newRaw = passwordChangeRequest.getNewPassword();
        String currentEncodedPassword = memberMapper.findPasswordByMemberId(memberId)
                .orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(oldRaw, currentEncodedPassword)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호 암호화 후 저장
        String newEncoded = passwordEncoder.encode(newRaw);
        int result = memberMapper.changePassword(memberId, newEncoded);

        if (result == 0) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // 혹은 다른 에러 코드
        }
    }

    public void updateMemberInfo(Long memberId, MemberUpdateRequest dto) {
        memberMapper.updateMemberInfo(memberId, dto);
    }

    public MemberResponse getMemberResponse(Long memberId) {
        return memberMapper.findFullMemberById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
    }
}
