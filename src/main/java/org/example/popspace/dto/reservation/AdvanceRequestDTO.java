package org.example.popspace.dto.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdvanceRequestDTO {
    @NotNull
    private Long popupId;

    @NotNull
    private String reserveDate;
    @NotNull
    private String reserveTime;

    public LocalDate getReserveDateAsLocalDate() {
        return LocalDate.parse(reserveDate);
    }

}

