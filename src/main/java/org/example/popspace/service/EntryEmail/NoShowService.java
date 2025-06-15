package org.example.popspace.service.EntryEmail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.popspace.mapper.EntryEmailMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoShowService {

    private final EntryEmailMapper entryEmailMapper;

    /**
     * 노쇼처리 (입장 안한 EMAIL_SEND 인원 → NO_SHOW 변경)
     */
    public int processNoShow(Long popupId, LocalDate date, String reserveTime) {
        List<Long> noShowCandidates = entryEmailMapper.selectNoShowCandidates(popupId, date, reserveTime);
        noShowCandidates.forEach(reserveId -> entryEmailMapper.updateReservationState(reserveId, "NO_SHOW"));
        return noShowCandidates.size();
    }
}
