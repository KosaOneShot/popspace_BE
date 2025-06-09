package org.example.popspace.service.popup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.popup.PopupListDto;
import org.example.popspace.mapper.PopupMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopupListService {
    private final PopupMapper popupMapper;

    public List<PopupListDto> getPopupList(Long memberId, String searchKeyword, String searchDateStr) throws ParseException {
        // TODO : 최신순, 찜수 어떤 쿼리를 날릴지 결정
        Date searchDate = new SimpleDateFormat("yyyy-MM-dd").parse(searchDateStr);
        return popupMapper.findPopupListBySearchKeywordAndDate(memberId, searchKeyword, searchDate);
    }
}
