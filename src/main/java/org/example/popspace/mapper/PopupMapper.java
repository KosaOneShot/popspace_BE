package org.example.popspace.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;
import org.apache.ibatis.annotations.Update;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.dto.popup.PopupListDto;
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
        UPDATE POPSPACE.POPUP_LIKE
        SET LIKE_STATE = #{toBeState}
        WHERE POPUP_ID = #{popupId} AND MEMBER_ID = #{memberId}
    """)
    int updateLikeState(Long popupId, Long memberId, String toBeState);

    @Insert("""
        INSERT INTO POPUP_LIKE (LIKE_ID, POPUP_ID, MEMBER_ID, LIKE_STATE, CREATED_AT)
        VALUES (SEQ_POPUP_LIKE_ID.nextval, #{popupId}, #{memberId}, #{toBeState}, SYSDATE)
    """)
    int insertLikeState(long popupId, long memberId, String toBeState);

//
//    /* 찜 state 업데이트 */
//    @Update("""
//        MERGE INTO POPUP_LIKE PL
//        USING (
//          SELECT
//            #{popupId}  AS popup_id,
//            #{memberId} AS member_id
//          FROM dual
//        ) SRC
//        ON (PL.popup_id = SRC.popup_id AND PL.member_id = SRC.member_id)
//        WHEN MATCHED THEN
//          UPDATE SET PL.like_state = #{toBeState}
//        WHEN NOT MATCHED THEN
//          INSERT (LIKE_ID, popup_id, member_id, like_state, created_at)
//          VALUES (SEQ_POPUP_LIKE_ID.nextval, SRC.popup_id, SRC.member_id, #{toBeState}, SYSDATE)
//    """)
//    void upsertPopupLike(long popupId, long memberId, String toBeState);


    /* 팝업 목록 + 검색 + 정렬(찜) */
    // 에러 있는 것처럼 보이지만 제대로 동작함
    @Select({
            """
            <script>
              SELECT
                p.POPUP_ID,
                p.POPUP_NAME,
                p.LOCATION,
                p.START_DATE,
                p.END_DATE,
                p.IMAGE_URL,
                pl.LIKE_STATE,
                NVL(lc.LIKE_CNT, 0) AS LIKE_CNT
              FROM POPUP P
              LEFT JOIN (
                SELECT popup_id, COUNT(*) AS LIKE_CNT
                FROM POPUP_LIKE
                GROUP BY popup_id
              ) LC ON P.popup_id = LC.popup_id
              LEFT JOIN POPUP_LIKE PL ON P.popup_id = PL.popup_id AND PL.member_id = #{memberId}
              <where>
                <if test="searchKeyword != null and searchKeyword.trim() != ''">
                  AND P.POPUP_NAME LIKE '%' || #{searchKeyword} || '%'
                </if>
                <if test="searchDate != null">
                  AND #{searchDate} BETWEEN P.START_DATE AND P.END_DATE
                </if>
              </where>
              <choose>
                <when test="sortKey == 'mostLiked'">
                  ORDER BY LC.LIKE_CNT DESC NULLS LAST
                </when>
                <otherwise>
                  ORDER BY P.END_DATE DESC
                </otherwise>
              </choose>
            </script>
            """
    })
    List<PopupListDto> findPopupListBySearchKeywordAndDate(
            Long memberId, String searchKeyword, Date searchDate, String sortKey);

    @Select("""
    SELECT member_id
    FROM popup
    WHERE popup_id = #{popupId}
    """)
    Optional<Long> findPopupOwnerIdByPopupId(Long popupId);

}
