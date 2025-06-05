package org.example.popspace.service.auth;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.auth.MemberLoginInfo;
import org.example.popspace.dto.auth.MemberRegisterRequest;
import org.example.popspace.dto.auth.ResetPasswordRequest;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.MemberMapper;
import org.example.popspace.mapper.redis.AuthRedisRepository;
import org.example.popspace.service.email.EmailService;
import org.example.popspace.util.auth.RandomStringUtil;
import org.example.popspace.util.auth.SendTokenUtil;
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
    private final AuthRedisRepository authRedisRepository;
    private final EmailService emailService;

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
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
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

    public void logout(HttpServletRequest request, HttpServletResponse response) {

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("accessToken") || cookie.getName().equals("refreshToken")) {
                authRedisRepository.setTokenBlacklist(cookie.getValue());

            }
        }

        SendTokenUtil.clearTokens(response);
    }

    @Transactional
    public void existsEmailAndSendEmail(String email) {

        memberMapper.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        String code = RandomStringUtil.generateRandomCode();

        authRedisRepository.setEmailCodeValues(email, code);

        emailService.sendPinNumberToEmail(email, code);
    }

    @Transactional
    public void validResetPasswordRequestAndSendEmail(ResetPasswordRequest resetPasswordRequest) {

        String savedCode = authRedisRepository.getEmailCodeValue(resetPasswordRequest.getEmail());
        if (!savedCode.equals(resetPasswordRequest.getCode())) {
            throw new CustomException(ErrorCode.INVALID_RESET_CODE);
        }
        String newPassword = RandomStringUtil.generateRandomPassword();

        memberMapper.updatePassword(passwordEncoder.encode(newPassword), resetPasswordRequest.getEmail());

        emailService.temporaryPasswordEmail(resetPasswordRequest.getEmail(), newPassword);
    }

}
