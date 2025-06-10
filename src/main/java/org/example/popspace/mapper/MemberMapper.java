package org.example.popspace.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.*;
import org.example.popspace.dto.auth.MemberLoginInfo;
import org.example.popspace.dto.auth.MemberRegisterRequest;

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

    @Update("""
            update MEMBER set PASSWORD= #{encodePassword} where EMAIL= #{email}
            """)
    void updatePassword(String encodePassword,String email);

}
