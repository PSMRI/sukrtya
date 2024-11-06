package com.piramal.spring_boot_demo.repository;

import com.piramal.spring_boot_demo.DTO.FormTranslDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class FormTranslRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<FormTranslDTO> getFormTranslList(int facilityType, int facilityId, int rgLId) {
        String sql = "SELECT * FROM get_form_data(?, ?)";

        return jdbcTemplate.query(sql, new Object[]{facilityType, facilityId}, (rs, rowNum) -> {
            FormTranslDTO form = new FormTranslDTO();
            form.setFormID(rs.getInt("formid"));
            form.setFromName(rs.getString("formname"));
            form.setFacilityTypeID(rs.getInt("facilitytypeid"));
            form.setCreateDate(rs.getString("createddate"));
            form.setFacilitype(rs.getString("facilitytype"));
            form.setUser(rs.getString("userid"));
            form.setUserSubmissionDate(rs.getString("submitdate"));
            form.setApprovedBy(rs.getString("approvedby"));
            form.setApprovedDate(rs.getString("approveddate"));
            form.setTransactionId(rs.getString("transactionid"));
            form.setApprovalStatus(rs.getInt("f7"));
            return form;
        });
    }
}