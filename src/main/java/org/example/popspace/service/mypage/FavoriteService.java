package org.example.popspace.service.mypage;

import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.mypage.FavoritePopupResponseDto;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.FavoriteMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService{

    private final FavoriteMapper favoriteMapper;

    public List<FavoritePopupResponseDto> getFavorites(Long memberId) {
        return favoriteMapper.findFavoritesByMemberId(memberId);
    }

    public void toggleFavorite(Long memberId, Long popupId) {
        int exists = favoriteMapper.existsFavorite(memberId, popupId);
        if (exists > 0) {
            favoriteMapper.toggleFavoriteState(memberId, popupId);
        } else {
            int inserted = favoriteMapper.insertFavorite(memberId, popupId);
            if (inserted != 1) {
                throw new CustomException(ErrorCode.FAVORITE_INSERT_FAILED);
            }
        }
    }
}
