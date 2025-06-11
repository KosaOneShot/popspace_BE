package org.example.popspace.dto.popup;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PopupSearchDto {
    private String searchKeyword;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate searchDate;
    private String sortKey;
}
