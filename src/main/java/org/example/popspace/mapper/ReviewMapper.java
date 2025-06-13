package org.example.popspace.mapper;

import org.apache.ibatis.annotations.*;
import org.example.popspace.dto.mypage.PendingReviewDto;
import org.example.popspace.dto.mypage.ReviewRequestDto;
import org.example.popspace.dto.mypage.ReviewResponseDto;
import org.example.popspace.dto.mypage.ReviewUpdateRequestDto;

import java.util.List;

@Mapper
public interface ReviewMapper {

    @Insert("""
        INSERT INTO REVIEW (
            review_id, rating, content, created_at, reserve_id
        ) VALUES (
            seq_review_id.NEXTVAL, #{rating}, #{content}, SYSDATE, #{reserveId}
        )
    """)
    void insertReview(ReviewRequestDto dto);

    @Select("""
        SELECT COUNT(*)
        FROM RESERVATION
        WHERE reserve_id = #{reserveId}
        AND member_id = #{memberId}
    """)
    int isReservationOwnedByMember(@Param("reserveId") Long reserveId, @Param("memberId") Long memberId);


    @Select("""
        SELECT COUNT(*)
        FROM REVIEW R
        JOIN RESERVATION RS ON R.reserve_id = RS.reserve_id AND R.review_id = #{reviewId} AND RS.member_id = #{memberId}
    """)
    int isReviewOwnedByMember(@Param("reviewId") Long reviewId, @Param("memberId") Long memberId);

    @Update("""
        UPDATE REVIEW
        SET rating = #{dto.rating},
            content = #{dto.content},
            updated_at = SYSDATE
        WHERE review_id = #{reviewId}
    """)
    void updateReview(@Param("reviewId") Long reviewId, @Param("dto") ReviewUpdateRequestDto dto);

    @Update("""
        UPDATE REVIEW
        SET review_state = 'DELETED',
            updated_at = SYSDATE
        WHERE review_id = #{reviewId}
    """)
    void deleteReview(@Param("reviewId") Long reviewId);

    @Select("""
        SELECT
            R.review_id AS reviewId,
            P.popup_name AS title,
            R.content,
            R.rating,
            TO_CHAR(RS.reserve_date, 'YYYY-MM-DD') AS visitedDate,
            P.image_url AS imageUrl
        FROM REVIEW R
        JOIN RESERVATION RS ON R.reserve_id = RS.reserve_id
        JOIN POPUP P ON RS.popup_id = P.popup_id AND RS.member_id = #{memberId} 
        WHERE R.review_state = 'ACTIVE'
        ORDER BY R.created_at DESC
    """)
    List<ReviewResponseDto> findReviewsByMemberId(@Param("memberId") Long memberId);

    @Select("""
        SELECT
            R.reserve_id AS reserveId,
            P.popup_name AS title,
            TO_CHAR(R.reserve_date, 'YYYY-MM-DD') AS visitedDate,
            NVL(P.image_url, 'https://placehold.co/80x80.png?text=팝업') AS imageUrl
        FROM RESERVATION R
        JOIN POPUP P ON R.popup_id = P.popup_id AND R.member_id = #{memberId}
        WHERE R.reservation_state = 'CHECKED_OUT'
        AND NOT EXISTS (
            SELECT 1 FROM REVIEW V WHERE V.reserve_id = R.reserve_id
        )
    """)
    List<PendingReviewDto> findPendingReviews(@Param("memberId") Long memberId);

}