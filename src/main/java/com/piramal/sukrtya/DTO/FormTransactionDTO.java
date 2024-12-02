package com.piramal.sukrtya.DTO;

public class FormTransactionDTO {
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

    public int getFormID()
    {
        return formID;
    }

    public void setFormID(int formID)
    {
        this.formID = formID;
    }

    public String getFromName()
    {
        return fromName;
    }

    public void setFromName(String fromName)
    {
        this.fromName = fromName;
    }

    public int getFacilityTypeID()
    {
        return facilityTypeID;
    }

    public void setFacilityTypeID(int facilityTypeID)
    {
        this.facilityTypeID = facilityTypeID;
    }

    public String getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(String createDate)
    {
        this.createDate = createDate;
    }

    public String getFacilitype()
    {
        return facilitype;
    }

    public void setFacilitype(String facilitype)
    {
        this.facilitype = facilitype;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getUserSubmissionDate()
    {
        return userSubmissionDate;
    }

    public void setUserSubmissionDate(String userSubmissionDate)
    {
        this.userSubmissionDate = userSubmissionDate;
    }

    public String getApprovedBy()
    {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy)
    {
        this.approvedBy = approvedBy;
    }

    public String getApprovedDate()
    {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate)
    {
        this.approvedDate = approvedDate;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public int getApprovalStatus()
    {
        return approvalStatus;
    }

    public void setApprovalStatus(int approvalStatus)
    {
        this.approvalStatus = approvalStatus;
    }
}

