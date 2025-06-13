package org.example.popspace.controller.mypage;

import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.auth.CustomUserDetail;
import org.example.popspace.dto.mypage.FavoritePopupResponseDto;
import org.example.popspace.service.mypage.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    //관심 팝업 목록
    @GetMapping
    public ResponseEntity<List<FavoritePopupResponseDto>> getFavorites(@AuthenticationPrincipal CustomUserDetail user) {
        List<FavoritePopupResponseDto> list = favoriteService.getFavorites(user.getId());
        return ResponseEntity.ok(list);
    }

    //관심 팝업 토글
    @PostMapping("/toggle/{popupId}")
    public ResponseEntity<Void> toggleFavorite(@PathVariable Long popupId,
                                               @AuthenticationPrincipal CustomUserDetail user) {
        favoriteService.toggleFavorite(user.getId(), popupId);
        return ResponseEntity.ok().build();
    }
}

