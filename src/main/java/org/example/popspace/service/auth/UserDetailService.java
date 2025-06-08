package org.example.popspace.service.auth;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.auth.MemberLoginInfo;
import org.example.popspace.dto.auth.MemberRegisterRequest;
import org.example.popspace.mapper.MemberMapper;
import org.example.popspace.util.auth.SendTokenUtil;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

    //주입
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CustomUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {

        CustomUserDetail customUserDetail = memberMapper.findByEmail(username)
                .map(CustomUserDetail::from) // 내부에서 role → authorities 변환
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find email"));

        log.info(String.valueOf(customUserDetail));
        return customUserDetail;
    }

    public MemberLoginInfo findByMemberId(Long memberId) {

        return memberMapper.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    @Transactional
    public void registerUser(MemberRegisterRequest memberRegisterRequest) {

        memberRegisterRequest.encodePassword(passwordEncoder.encode(memberRegisterRequest.getPassword()));
        memberMapper.save(memberRegisterRequest);

    }

    public void existsUserCheck(MemberRegisterRequest memberRegisterRequest) {

        int result = memberMapper.existsEmailOrNickname(memberRegisterRequest.getEmail(), memberRegisterRequest.getNickname());
        log.info("result: {}", result);
        if (result != 0) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);

        }
    }

    public void logout(HttpServletResponse response) {
        SendTokenUtil.clearTokens(response);
    }

    public void existsEmail(String email) {

        if (memberMapper.existsEmail(email).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    public void existNickname(String nickname) {

        if (memberMapper.existsNickname(nickname).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
