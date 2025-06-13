package org.example.popspace.dto.popup;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PopupDetailResponse {
    private Long popupId;
    private String popupName;
}
