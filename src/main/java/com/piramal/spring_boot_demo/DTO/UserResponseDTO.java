package com.piramal.spring_boot_demo.DTO;

// UserResponseDTO.java
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter @Setter
public class UserResponseDTO {
    private String userName;
    private String password;
    private Integer userTypeID;
    private Integer profileID;
    private Integer userID;
    private String profileName;
    private String profilePhoto;
    private Integer approvalStatus;
    private String userType;
    private List<FacilityDTO> facility;

    // Getters, Setters, Constructors
}

