package org.example.popspace.dto.qr;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VerifyRequestDTO {
    @NotNull(message = "예약 아이디가 없습니다.")
    private Long reserveId;

    @NotNull(message = "서명 값이 없습니다.")
    private String sig;

}
