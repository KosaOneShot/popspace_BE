package org.example.popspace.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TimeSlotDTO {
    private String time;          // "10:00"
    private boolean isAvailable;  // true or false
}