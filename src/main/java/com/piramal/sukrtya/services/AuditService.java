package com.piramal.sukrtya.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void auditTrailInsert(int actionId, String description, String createdBy) {
        String sql = "INSERT INTO tbl_AuditTrail (ActionId, Description, CreatedBy, CreatedDate) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        jdbcTemplate.update(sql, actionId, description, createdBy);
    }

    public void actionTrnsInsert(int actionId, String transactionId) {
        String sql = "INSERT INTO tbl_action_Trns (ActionId, tranactionid) VALUES (?, ?)";
        jdbcTemplate.update(sql, actionId, transactionId);
    }
}
