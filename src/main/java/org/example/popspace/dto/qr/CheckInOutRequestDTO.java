package org.example.popspace.dto.qr;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CheckInOutRequestDTO {
    @NotNull
    private Long reserveId;
}
