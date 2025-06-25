package org.example.popspace.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void logEntranceAction(Long reserveId, String state) {
        // 예외 발생 시 전체 트랜잭션 자동 롤백
        jdbcTemplate.update("{call proc_log_entrance_action(?, ?)}", reserveId, state);
    }


}

