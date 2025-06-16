package org.example.popspace.controller.mypage;

import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.mypage.*;
import org.example.popspace.service.mypage.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 작성
    @PostMapping
    public ResponseEntity<Void> createReview(@RequestBody ReviewRequestDto dto,
                                             @AuthenticationPrincipal CustomUserDetail user) {
        reviewService.createReview(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(@PathVariable Long reviewId,
                                             @RequestBody ReviewUpdateRequestDto dto,
                                             @AuthenticationPrincipal CustomUserDetail user) {
        reviewService.updateReview(reviewId, dto, user.getId());
        return ResponseEntity.ok().build();
    }

    //리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId,
                                             @AuthenticationPrincipal CustomUserDetail user) {
        reviewService.deleteReview(reviewId, user.getId());
        return ResponseEntity.noContent().build();
    }

    //작성 리뷰 목록
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getUserReviews(@AuthenticationPrincipal CustomUserDetail user) {
        return ResponseEntity.ok(reviewService.getUserReviews(user.getId()));
    }

    //미작성 리뮤 목록
    @GetMapping("/pending")
    public ResponseEntity<List<PendingReviewDto>> getPendingReviews(
            @AuthenticationPrincipal CustomUserDetail user) {

        return ResponseEntity.ok(reviewService.getPendingReviews(user.getId()));
    }
}