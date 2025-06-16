package org.example.popspace.mapper;

import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.popspace.dto.popup.*;
import org.example.popspace.dto.statistics.PopupInfoWithLikeCount;
import org.example.popspace.dto.statistics.ReservationTypeStateCount;
import org.example.popspace.dto.statistics.ReservationMemberData;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Update;

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
        WHERE P.POPUP_ID = #{popupId}
    """)
    PopupInfoDto findPopupInfoAndReviewsByPopupId(Long popupId);

    /* 팝업 리뷰 */
    @Select("""
        SELECT
            P.MEMBER_ID,
            R.REVIEW_ID,
            R.RATING,
            R.CONTENT,
            R.CREATED_AT
        FROM POPUP P
        JOIN RESERVATION RES ON P.POPUP_ID = RES.POPUP_ID
        JOIN REVIEW R ON RES.RESERVE_ID = R.RESERVE_ID
        WHERE P.POPUP_ID = #{popupId}
    """)
    List<PopupReviewDto> findReviewsByPopupId(Long popupId);

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
        UPDATE POPUP_LIKE
        SET LIKE_STATE = #{toBeState}
        WHERE POPUP_ID = #{popupId} AND MEMBER_ID = #{memberId}
    """)
    int updateLikeState(Long popupId, Long memberId, String toBeState);

    @Insert("""
        INSERT INTO POPUP_LIKE (LIKE_ID, POPUP_ID, MEMBER_ID, LIKE_STATE, CREATED_AT)
        VALUES (SEQ_POPUP_LIKE_ID.nextval, #{popupId}, #{memberId}, #{toBeState}, SYSDATE)
    """)
    int insertLikeState(long popupId, long memberId, String toBeState);

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
                <if test="memberId != null">
                  pl.LIKE_STATE,
                </if>
                NVL(lc.LIKE_CNT, 0) AS LIKE_CNT
              FROM POPUP P
              LEFT JOIN (
                SELECT popup_id, COUNT(*) AS LIKE_CNT
                FROM POPUP_LIKE
                GROUP BY popup_id
              ) LC ON P.popup_id = LC.popup_id
              <if test="memberId != null">
                LEFT JOIN POPUP_LIKE PL ON P.popup_id = PL.popup_id AND PL.member_id = #{memberId}
              </if>
              <where>
                  <!-- 검색 (제목) -->
                  <if test="searchKeyword != null and searchKeyword.trim() != ''">
                    AND P.POPUP_NAME LIKE '%' || #{searchKeyword} || '%'
                  </if>
                  <!-- 검색 (날짜) -->
                  <if test="searchDate != null">
                    AND #{searchDate} BETWEEN P.START_DATE AND P.END_DATE
                  </if>
                  <!-- 커서(찜순, 최신순)에 따른 페이지네이션.  &lt; 는 < 임 -->
                  <if test="sortKey == 'mostLiked'">
                    <if test="lastLikeCnt != null">
                        <![CDATA[
                            AND (LC.LIKE_CNT < #{lastLikeCnt}
                            OR (LC.LIKE_CNT = #{lastLikeCnt} AND P.POPUP_ID < #{lastPopupId}))
                        ]]>
                    </if>
                  </if>
                  <if test="sortKey == 'newest'">
                    <if test="lastEndDate != null">
                        <![CDATA[
                            AND (P.END_DATE < #{lastEndDate}
                            OR (P.END_DATE = #{lastEndDate} AND P.POPUP_ID < #{lastPopupId}))
                        ]]>
                    </if>
                  </if>
                </where>
                <!-- ORDER BY (찜순, 최신순) -->
                <choose>
                  <when test="sortKey == 'mostLiked'">
                    ORDER BY LC.LIKE_CNT DESC NULLS LAST, P.POPUP_ID DESC
                  </when>
                  <otherwise>
                    ORDER BY P.END_DATE DESC, P.POPUP_ID DESC
                  </otherwise>
                </choose>
                FETCH FIRST 4 ROWS ONLY
            </script>
            """
    })
    List<PopupCardDto> findPopupListBySearchKeywordAndDate(
            Long memberId, String searchKeyword, LocalDate searchDate, String sortKey, LocalDate lastEndDate, Long lastLikeCnt, Long lastPopupId);

    @Select("""
    SELECT member_id
    FROM popup
    WHERE popup_id = #{popupId}
    """)
    Optional<Long> findPopupOwnerIdByPopupId(Long popupId);

    @Select("""
            SELECT
                m.MEMBER_ID,
                m.BIRTH_DATE,
                m.SEX,
                r.RESERVE_ID,
                r.RESERVATION_TYPE,
                r.RESERVATION_STATE,
                r.RESERVE_DATE,
                r.RESERVE_TIME,
                rv.RATING,
                el.CREATED_AT,
                el.ENTRANCE_STATE
            FROM ENTRANCE_LOG el
            JOIN RESERVATION r ON el.RESERVE_ID = r.RESERVE_ID
            JOIN MEMBER m ON r.MEMBER_ID = m.MEMBER_ID
            LEFT JOIN REVIEW rv ON rv.RESERVE_ID = r.RESERVE_ID
            WHERE el.POPUP_ID = #{popupId} and el.ENTRANCE_STATE='CHECKED_IN'
            """)
    List<ReservationMemberData> findStatisticsDataDtoByPopupId(Long popupId);

    @Select("""
            select p.POPUP_ID,p.POPUP_NAME,p.START_DATE,p.END_DATE,p.OPEN_TIME,p.CLOSE_TIME, count(pl.LIKE_STATE) as like_count
            from POPUP p
            left join POPUP_LIKE pl on p.POPUP_ID=pl.POPUP_ID and pl.LIKE_STATE='ACTIVE'
            where p.POPUP_ID=#{popupId}
            group by p.POPUP_ID, p.START_DATE, p.END_DATE, p.POPUP_NAME,p.OPEN_TIME,p.CLOSE_TIME
            """)
    PopupInfoWithLikeCount findPopupWithLikeCount(Long popupId);

    @Select("""
            SELECT
                RESERVATION_TYPE,
                RESERVATION_STATE,
                COUNT(*) AS CNT
            FROM RESERVATION
            WHERE POPUP_ID = #{popupId}
            GROUP BY RESERVATION_TYPE, RESERVATION_STATE
            """)
    List<ReservationTypeStateCount> findReservationData(Long popupId);

    @Select("""
            select p.POPUP_ID,p.POPUP_NAME
            from POPSPACE.POPUP p
            join member m on m.MEMBER_ID= p.MEMBER_ID and p.MEMBER_ID=#{memberId}
            order by p.POPUP_ID desc
            """)
    List<PopupDetailResponse> findAllPopupDetailByMemberId(Long memberId);

    @Select("""
            select p.POPUP_ID,p.POPUP_NAME,m.MEMBER_NAME
            from POPSPACE.POPUP p
            join member m on m.MEMBER_ID= p.MEMBER_ID
            order by p.POPUP_ID desc
            """)
    List<PopupDetailForAdminResponse> findAllPopupListForAdmin();
}
