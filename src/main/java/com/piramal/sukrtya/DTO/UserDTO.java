package com.piramal.sukrtya.DTO;

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

    public int getUserTypeID()
    {
        return userTypeID;
    }

    public void setUserTypeID(int userTypeID)
    {
        this.userTypeID = userTypeID;
    }

    public int getProfileID()
    {
        return profileID;
    }

    public void setProfileID(int profileID)
    {
        this.profileID = profileID;
    }

    public long getUserID()
    {
        return userID;
    }

    public void setUserID(long userID)
    {
        this.userID = userID;
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

    public int getApprovalStatus()
    {
        return approvalStatus;
    }

    public void setApprovalStatus(int approvalStatus)
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
