package com.piramal.spring_boot_demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Getter@Setter
public class FormTranslDTO {
    private int formID;
    private String fromName;
    private int facilityTypeID;
    private String createDate;
    private String facilitype;
    private String user;
    private String userSubmissionDate;
    private String approvedBy;
    private String approvedDate;
    private String transactionId;
    private int approvalStatus;

    // Getters and Setters
}

