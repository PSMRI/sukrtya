package com.piramal.spring_boot_demo.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long formID;

    private String formName;
    private Integer facilityTypeID;
    private LocalDateTime createDate;
    private String facilityType;
    private String userName;
    private LocalDateTime userSubmissionDate;
    private String approvedBy;
    private LocalDateTime approvedDate;
    private String transactionId;
    private Integer approvalStatus;

//    private Long profileid;

    // Many-to-One relationship with UserProfile
    @ManyToOne
    @JoinColumn(name = "profileid", nullable = false)
    @JsonIgnore // Prevent recursive serialization
    private UserProfile userProfile;
}
