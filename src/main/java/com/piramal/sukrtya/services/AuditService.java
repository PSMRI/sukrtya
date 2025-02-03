package com.piramal.sukrtya.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuditService {


    private final JdbcTemplate jdbcTemplate;

    public AuditService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void auditTrailInsert(int actionId, String description, String createdBy, String transactionId) {
        String sql = "INSERT INTO tbl_AuditTrail (ActionId, Description, CreatedBy, CreatedDate,tranactionid) VALUES (?, ?, ?, CURRENT_TIMESTAMP,?)";
        jdbcTemplate.update(sql, actionId, description, createdBy,transactionId);
    }

    public void actionTrnsInsert(int actionId, String transactionId) {
        String sql = "INSERT INTO tbl_action_Trns (ActionId, tranactionid) VALUES (?, ?)";
        jdbcTemplate.update(sql, actionId, transactionId);
    }
}
