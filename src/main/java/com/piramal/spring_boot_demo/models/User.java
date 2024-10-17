package com.piramal.spring_boot_demo.models;

 

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;



@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

    private String userName;
    private String password;
    private Integer userTypeID;
    private Long profileID;
    private String profileName;
    private String profilePhoto;
    private Integer approvalStatus;
    private String userType;

    // Getters and setters
    
}
