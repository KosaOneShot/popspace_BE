package org.example.popspace.dto.reservation;

import java.time.LocalDate;

public record ReservationDateKeyInfo(
        Long popupId,
        LocalDate reserveDate
) {}
