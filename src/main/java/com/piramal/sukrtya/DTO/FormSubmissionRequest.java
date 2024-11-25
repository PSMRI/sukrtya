package com.piramal.sukrtya.DTO;


import java.util.List;



public class FormSubmissionRequest {
    private String TransactionId;
    private Integer userId;
    private Integer FormId;
    private String FacilityNIN;
    private String Lattitude;
    private String Longitude;
    private String GoogleAddress;
    private List<PostAnswer> PostAnswer;

    // Getter and Setter for TransactionId
    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        this.TransactionId = transactionId;
    }

    // Getter and Setter for userId
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // Getter and Setter for FormId
    public Integer getFormId() {
        return FormId;
    }

    public void setFormId(Integer formId) {
        this.FormId = formId;
    }

    // Getter and Setter for FacilityNIN
    public String getFacilityNIN() {
        return FacilityNIN;
    }

    public void setFacilityNIN(String facilityNIN) {
        this.FacilityNIN = facilityNIN;
    }

    // Getter and Setter for Lattitude
    public String getLattitude() {
        return Lattitude;
    }

    public void setLattitude(String lattitude) {
        this.Lattitude = lattitude;
    }

    // Getter and Setter for Longitude
    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        this.Longitude = longitude;
    }

    // Getter and Setter for GoogleAddress
    public String getGoogleAddress() {
        return GoogleAddress;
    }

    public void setGoogleAddress(String googleAddress) {
        this.GoogleAddress = googleAddress;
    }

    // Getter and Setter for PostAnswer
    public List<PostAnswer> getPostAnswer() {
        return PostAnswer;
    }

    public void setPostAnswer(List<PostAnswer> postAnswer) {
        this.PostAnswer = postAnswer;
    }
}

