package com.piramal.spring_boot_demo.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter @Setter
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileID;

    private String profileName;
    private LocalDate profileDoB;
    private String profileGender;
    private String profileMobile;
    private String profileWhatsApp;
    private String profileAddress;
    private String profilePincode;
    private String profilePhoto;
    private String profileEduction;
    private String profileEmail;
    private Integer profileGenderID;
    private Integer profileEductionLevelID;
    private Integer educationId;
    private Integer roleId;
    private Integer profileStateID;
    private Integer profileDistrictID;
    private Integer profileBlockID;
    private String aadharNo;
    private String aadharPhoto;
    private String userType;
    private Long userId;

    // One-to-Many relationship with Form
    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Form> forms;
}
