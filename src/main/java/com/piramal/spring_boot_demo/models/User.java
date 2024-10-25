package com.piramal.spring_boot_demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
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

    public Long getUserID()
    {
        return userID;
    }

    public void setUserID(Long userID)
    {
        this.userID = userID;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Integer getUserTypeID()
    {
        return userTypeID;
    }

    public void setUserTypeID(Integer userTypeID)
    {
        this.userTypeID = userTypeID;
    }

    public Long getProfileID()
    {
        return profileID;
    }

    public void setProfileID(Long profileID)
    {
        this.profileID = profileID;
    }

    public String getProfileName()
    {
        return profileName;
    }

    public void setProfileName(String profileName)
    {
        this.profileName = profileName;
    }

    public String getProfilePhoto()
    {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto)
    {
        this.profilePhoto = profilePhoto;
    }

    public Integer getApprovalStatus()
    {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus)
    {
        this.approvalStatus = approvalStatus;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }
}
