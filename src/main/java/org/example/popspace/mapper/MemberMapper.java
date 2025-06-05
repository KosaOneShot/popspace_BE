package org.example.popspace.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.popspace.dto.auth.MemberLoginInfo;
import org.example.popspace.dto.auth.MemberRegisterRequest;

import java.util.Optional;

@Mapper
public interface MemberMapper {
    Optional<MemberLoginInfo> findByEmail(String email);

    Optional<MemberLoginInfo> findByMemberId(Long memberId);

    int existsEmailOrNickname(String email, String nickname);

    void save(MemberRegisterRequest memberRegisterRequest);

}
