package org.example.popspace.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.popspace.dto.auth.MemberLoginInfo;
import org.example.popspace.dto.auth.MemberRegisterRequest;

import java.util.Optional;

@Mapper
public interface MemberMapper {
    Optional<MemberLoginInfo> findByEmail(String email);

    Optional<MemberLoginInfo> findByMemberId(Long memberId);

    int existsEmailOrNickname(String email, String nickname);

    void save(MemberRegisterRequest memberRegisterRequest);

    @Select(""" 
            SELECT 1
            FROM member
            WHERE email = #{email}
            FETCH FIRST 1 ROWS ONLY -- email 동일한거 찾으면 바로 탐색 종료
            """)
    Optional<Integer> existsEmail(String email);

    @Select(""" 
            SELECT 1
            FROM member
            WHERE NICKNAME = #{nickname}
            FETCH FIRST 1 ROWS ONLY
            """)
    Optional<Integer> existsNickname(String nickname);
}
