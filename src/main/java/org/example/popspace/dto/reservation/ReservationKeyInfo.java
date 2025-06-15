package org.example.popspace.dto.reservation;

import java.time.LocalDate;

public record ReservationKeyInfo(
        Long popupId,
        LocalDate reserveDate,
        String reserveTime
) {}

