package org.example.popspace.service.EntryEmail;

import lombok.RequiredArgsConstructor;
import org.example.popspace.dto.EntryEmail.Reservation;
import org.example.popspace.mapper.EntryEmailMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntranceStateUpdateService {

    private final EntryEmailMapper entryEmailMapper;

    @Transactional
    public void updateReservations(List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            entryEmailMapper.updateReservationState(reservation.getReserveId(), "EMAIL_SEND");
        }
    }
}