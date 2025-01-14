package com.piramal.sukrtya.entity;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "tblformtransactions")
public class TblFormTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idpk;

    @Column(name = "formid")
    private Integer formId;

    @Column(name = "userid")
    private Integer userId;

    @Column(name = "transactionid", unique = true)
    private String transactionId;

    @Column(name = "submitdate")
    private String submitDate;

    @Column(name = "lattitude")
    private String lattitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "googleaddress")
    private String googleAddress;

    @Column(name = "approvedby")
    private String approvedBy;

    @Column(name = "approveddate")
    private String approvedDate;

    @Column(name = "F7")
    private String approvalStatus;

    public Integer getFormId() {
        return formId;
    }

    public void setFormId(Integer formId) {
        this.formId = formId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getGoogleAddress() {
        return googleAddress;
    }

    public void setGoogleAddress(String googleAddress) {
        this.googleAddress = googleAddress;
    }





    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }
}
