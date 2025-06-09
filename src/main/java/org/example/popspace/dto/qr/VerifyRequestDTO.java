package org.example.popspace.dto.qr;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VerifyRequestDTO {
    @NotNull(message = "reservationId 값이 없습니다.")
    private Long reservationId;

    @NotNull(message = "sig 값이 없습니다.")
    private String sig;

}
