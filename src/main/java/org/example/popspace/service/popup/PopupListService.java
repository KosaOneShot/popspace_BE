package org.example.popspace.service.popup;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.popup.PopupListDto;
import org.example.popspace.mapper.PopupMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopupListService {
    private final PopupMapper popupMapper;

    public List<PopupListDto> getPopupList(Long memberId, String searchKeyword, String searchDateStr, String sortKey) throws ParseException {
        LocalDate searchDate = !"".equals(searchDateStr) ? LocalDate.parse(searchDateStr, DateTimeFormatter.ISO_DATE) : null;
        return popupMapper.findPopupListBySearchKeywordAndDate(memberId, searchKeyword, searchDate, sortKey);
    }
}
