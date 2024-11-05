package com.piramal.spring_boot_demo.DTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter@Setter
public class FormDetailDTO {
    private int formID;
    private String fromName;
    private int facilityTypeID;
    private String createdDate;
    private String facilitype; // consider renaming for clarity
    private String user;
    private LocalDate userSubmissionDate; // assuming this could be a LocalDate
    private String approvedBy;
    private String approvedDate;
    private String transactionId;
    private int approvalStatus;

    // Getters and setters
}