package com.piramal.spring_boot_demo.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class UserDTO {
    private String userName;
    private String password;
    private int userTypeID;
    private int profileID;
    private long userID;
    private String profileName;
    private String profilePhoto;
    private int approvalStatus;
    private String userType;
}
