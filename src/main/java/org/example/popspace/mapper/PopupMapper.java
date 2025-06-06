package org.example.popspace.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;
import org.apache.ibatis.annotations.Update;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.dto.popup.ReservationDto;
import org.example.popspace.dto.popup.ReviewDto;

@Mapper
public interface PopupMapper {

    /* 팝업 상세 */
    @Select("""
        SELECT
            P.POPUP_ID,
            P.POPUP_NAME,
            P.LOCATION,
            P.START_DATE,
            P.END_DATE,
            P.OPEN_TIME,
            P.CLOSE_TIME,
            P.DESCRIPTION,
            P.CATEGORY,
            P.MAX_RESERVATIONS,
            P.IMAGE_URL
        FROM POPUP P
        where P.POPUP_ID = #{popupId}
    """)
    Optional<PopupInfoDto> findPopupInfoByPopupId(Long popupId);

    /* 리뷰들 조회 */
    @Select("""
        SELECT
            P.POPUP_ID,
            RES.MEMBER_ID,
            REVIEW.REVIEW_ID,
            REVIEW.RATING,
            REVIEW.CONTENT,
            REVIEW.CREATED_AT
        FROM POPUP P
        JOIN RESERVATION RES ON P.POPUP_ID = RES.POPUP_ID
        JOIN REVIEW ON RES.RESERVE_ID = REVIEW.RESERVE_ID
        WHERE P.POPUP_ID = #{popupId}
    """)
    List<ReviewDto> findReviewByPopupId(Long popupId);


    /* 예약 여부 */
    @Select("""
            SELECT
                M.MEMBER_ID,
                R.POPUP_ID,
                R.RESERVE_ID,
                R.RESERVATION_STATE,
                R.RESERVATION_TYPE
            from MEMBER M
            JOIN RESERVATION R ON M.MEMBER_ID = R.MEMBER_ID
            WHERE R.POPUP_ID = #{popupId} AND M.MEMBER_ID = #{memberId}
            ORDER BY RESERVE_ID DESC
            FETCH FIRST 1 ROWS ONLY
    """)
    Optional<ReservationDto> findReservationByPopupIdMemberId(Long popupId, Long memberId); // 없으면 예약 안 한거

    /* 찜 여부
     * @return : Y, N
     * unique(memberId, popupId) 라는 가정하에
     * */
    @Select("""
        SELECT LIKE_STATE
        FROM POPUP_LIKE
        WHERE POPUP_ID = #{popupId} AND MEMBER_ID = #{memberId}
    """)
    String findPopupLikeByPopupIdMemberId(Long popupId, Long memberId);

    /* 찜 state 업데이트 */
    @Update("""
        MERGE INTO POPUP_LIKE PL
        USING (
          SELECT
            #{popupId}  AS popup_id,
            #{memberId} AS member_id
          FROM dual
        ) SRC
        ON (PL.popup_id = SRC.popup_id AND PL.member_id = SRC.member_id)
        WHEN MATCHED THEN
          UPDATE SET PL.like_state = #{toBeState}
        WHEN NOT MATCHED THEN
          INSERT (LIKE_ID, popup_id, member_id, like_state, created_at)
          VALUES (SEQ_POPUP_LIKE_ID.nextval, SRC.popup_id, SRC.member_id, #{toBeState}, SYSDATE)
    """)
    int upsertPopupLike(long popupId, long memberId, String toBeState);

}
