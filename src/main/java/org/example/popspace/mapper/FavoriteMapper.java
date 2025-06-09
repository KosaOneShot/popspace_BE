package org.example.popspace.mapper;

import org.apache.ibatis.annotations.*;
import org.example.popspace.dto.mypage.FavoritePopupResponseDto;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    @Select("""
        SELECT
            P.popup_id AS popupId,
            P.popup_name AS title,
            TO_CHAR(P.start_date, 'YYYY.MM.DD') || ' - ' || TO_CHAR(P.end_date, 'YYYY.MM.DD') AS dateRange,
            P.location,
            P.image_url AS imageUrl
        FROM POPUP_LIKE L
        JOIN POPUP P ON L.popup_id = P.popup_id
        WHERE L.member_id = #{memberId}
        AND L.like_state = 'ACTIVE'
    """)
    List<FavoritePopupResponseDto> findFavoritesByMemberId(@Param("memberId") Long memberId);

    @Select("""
        SELECT COUNT(*)
        FROM POPUP_LIKE
        WHERE member_id = #{memberId}
        AND popup_id = #{popupId}
    """)
    int existsFavorite(@Param("memberId") Long memberId, @Param("popupId") Long popupId);

    @Update("""
        UPDATE POPUP_LIKE
        SET like_state = CASE
            WHEN like_state = 'ACTIVE' THEN 'DELETED'
            ELSE 'ACTIVE'
        END
        WHERE member_id = #{memberId}
        AND popup_id = #{popupId}
    """)
    void toggleFavoriteState(@Param("memberId") Long memberId, @Param("popupId") Long popupId);

    @Insert("""
        INSERT INTO POPUP_LIKE (
            like_id, created_at, like_state, member_id, popup_id
        ) VALUES (
            seq_popup_like_id.NEXTVAL, SYSDATE, 'ACTIVE', #{memberId}, #{popupId}
        )
    """)
    int insertFavorite(@Param("memberId") Long memberId, @Param("popupId") Long popupId);

}
