package org.example.popspace.service.mypage;

import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.mypage.FavoritePopupResponseDto;
import org.example.popspace.global.error.CustomException;
import org.example.popspace.global.error.ErrorCode;
import org.example.popspace.mapper.FavoriteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService{

    private final FavoriteMapper favoriteMapper;

    public List<FavoritePopupResponseDto> getFavorites(Long memberId) {
        return favoriteMapper.findFavoritesByMemberId(memberId);
    }

    @Transactional
    public void toggleFavorite(Long memberId, Long popupId) {
        int exists = favoriteMapper.existsFavorite(memberId, popupId);
        if (exists > 0) {
            //찜 데이터가 존재하면 상태만 업데이트
            favoriteMapper.toggleFavoriteState(memberId, popupId);
        } else {
            //찜 데이터가 없으면 새로 삽입
            favoriteMapper.insertFavorite(memberId, popupId);
        }
    }
}
