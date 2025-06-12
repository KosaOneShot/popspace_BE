package org.example.popspace.dto.popup;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PopupDetailForAdminResponse {
    private Long popupId;
    private String popupName;
    private String memberName;
}
