package org.example.popspace.dto.popup;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PopupInfoDto {
    private Long popupId;
    private String popupName;
    private String location;
    private Date startDate;
    private Date endDate;
    private Date openTime;
    private Date closeTime;
    private String description;
    private String category;
    private int maxReservations;
    private String imageUrl;
}
