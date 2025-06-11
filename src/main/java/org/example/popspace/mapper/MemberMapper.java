package org.example.popspace.mapper;

import org.apache.ibatis.annotations.*;
import org.example.popspace.dto.auth.MemberLoginInfo;
import org.example.popspace.dto.auth.MemberRegisterRequest;
import org.example.popspace.dto.member.MemberResponse;
import org.example.popspace.dto.member.MemberUpdateRequest;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    @Select("""
                SELECT MEMBER_ID, email, nickname, role, password
                FROM member
                WHERE email = #{email}
            """)
    Optional<MemberLoginInfo> findByEmail(String email);

    @Select("""
                SELECT member_id, email, nickname, role, password
                FROM member
                WHERE member_id = #{memberId}
            """)
    Optional<MemberLoginInfo> findByMemberId(Long memberId);

    @Select("""
                SELECT CASE
                    WHEN EXISTS (
                        SELECT 1
                        FROM member
                        WHERE email = #{email}
                        OR nickname = #{nickname}
                    ) THEN 1 ELSE 0
                END
                FROM dual
            """)
    int existsEmailOrNickname(@Param("email") String email, @Param("nickname") String nickname);

    @Insert("""
                INSERT INTO member(
                    member_id, email, password, nickname, member_name, created_at,
                    sex, birth_date, phone_number, road_address, detail_address, agreement
                ) VALUES (
                    SEQ_MEMBER_ID.nextval, #{email}, #{password}, #{nickname}, #{memberName}, sysdate,
                    #{sex}, #{birthDate}, #{phoneNumber}, #{roadAddress}, #{detailAddress}, #{agreement}
                )
            """)
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

    @Update("""
            update MEMBER set PASSWORD= #{encodePassword} where EMAIL= #{email}
            """)
    void updatePassword(String encodePassword, String email);

    @Select("""
            select PASSWORD
            from MEMBER
            where MEMBER_ID=#{memberId}
            """)
    Optional<String> findPasswordByMemberId(Long memberId);

    @Update("""
                UPDATE MEMBER SET PASSWORD = #{newPassword}
                WHERE MEMBER_ID = #{memberId}
            """)
    int changePassword(Long memberId, String newPassword);

    @Update("""
        UPDATE MEMBER
        SET nickname = #{dto.nickname},
            road_address = #{dto.roadAddress},
            detail_address = #{dto.detailAddress},
            birth_date = TO_DATE(#{dto.birthDate}, 'YYYY-MM-DD'),
            sex = #{dto.sex},
            updated_at = SYSDATE
        WHERE member_id = #{memberId}
    """)
    int updateMemberInfo(@Param("memberId") Long memberId, MemberUpdateRequest dto);

    @Select("""
        SELECT
            member_id,
            email,
            nickname,
            member_name,
            phone_number,
            road_address,
            detail_address,
            TO_CHAR(birth_date, 'YYYY-MM-DD') AS birthDate,
            sex
        FROM member
        WHERE member_id = #{memberId}
    """)
    Optional<MemberResponse> findFullMemberById(Long memberId);
}
