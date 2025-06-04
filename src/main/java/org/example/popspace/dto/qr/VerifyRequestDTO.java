package org.example.popspace.dto.qr;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VerifyRequestDTO {
    private Long reservation_id;
    private String sig;

}
