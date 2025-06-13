package org.example.popspace.service.mypage;

import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.mypage.*;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.ReviewMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;

    @Transactional
    public void createReview(ReviewRequestDto dto, Long memberId) {
        Long reserveId = dto.getReserveId();

        // 에약 당사자인지 확인
        if (reviewMapper.isReservationOwnedByMember(reserveId, memberId) == 0) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        reviewMapper.insertReview(dto);
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewUpdateRequestDto dto, Long memberId) {
        //리뷰 작성자가 본인인지 확인
        if (reviewMapper.isReviewOwnedByMember(reviewId, memberId) == 0) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }
        reviewMapper.updateReview(reviewId, dto);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long memberId) {
        //리뷰 작성자가 본인인지 확인
        if (reviewMapper.isReviewOwnedByMember(reviewId, memberId) == 0) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }
        reviewMapper.deleteReview(reviewId);
    }

    public List<ReviewResponseDto> getUserReviews(Long memberId) {
        return reviewMapper.findReviewsByMemberId(memberId);
    }

    public List<PendingReviewDto> getPendingReviews(Long memberId) {
        return reviewMapper.findPendingReviews(memberId);
    }
}