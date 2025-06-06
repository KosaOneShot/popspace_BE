package org.example.popspace.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;
import org.example.popspace.dto.popup.PopupInfoDto;
import org.example.popspace.dto.popup.PopupLikeDto;
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
    Optional<List<ReviewDto>> findReviewByPopupId(Long popupId);


    /* 찜 여부 */
    @Select("""
        select L.MEMBER_ID,
               L.POPUP_ID,
               L.LIKE_ID,
               L.LIKE_STATE
        from MEMBER M
        join POPUP_LIKE L on M.MEMBER_ID = L.MEMBER_ID
        where L.POPUP_ID = #{popupId} AND M.MEMBER_ID = #{memberId}
    """)
    Optional<PopupLikeDto> findPopupLikeByPopupIdMemberId(Long popupId, Long memberId); // 없으면 like 안 한 거

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
            WHERE R.POPUP_ID = #{popupId} AND M.MEMBER_ID = #{memberID}
    """)
    Optional<ReservationDto> findReservationByPopupIdMemberId(Long popupId, Long memberId); // 없으면 예약 안 한거
}
