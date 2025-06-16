package org.example.popspace.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void callUpdateEntranceStatus(Long reserveId, String state) {
        jdbcTemplate.update("{call proc_log_entrance_action(?, ?)}", reserveId, state);
    }

}

