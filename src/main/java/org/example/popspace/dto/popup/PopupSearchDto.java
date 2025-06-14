package org.example.popspace.dto.popup;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PopupSearchDto {
    private String searchKeyword;
    private LocalDate searchDate;
    private String sortKey;
    // 페이지네이션
    private LocalDate lastEndDate;
    private Long lastPopupId;
    private Long lastLikeCnt;
}
