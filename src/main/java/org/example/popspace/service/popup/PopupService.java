package org.example.popspace.service.popup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.dto.popup.PopupDetailResponse;
import org.example.popspace.mapper.PopupMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PopupService {
    private final PopupMapper popupMapper;

    public List<PopupDetailResponse> getPopupList(Long memberId) {
        return popupMapper.findAllPopupDetailByMemberId(memberId);
    }
}
