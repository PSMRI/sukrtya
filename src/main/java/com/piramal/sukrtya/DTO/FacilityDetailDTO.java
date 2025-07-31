package com.piramal.sukrtya.DTO;

import java.util.List;

public class FacilityDetailDTO {
    private int facilityId;
    private String facilityName;
    private String facilityNin;
    private String facilityLongitude;
    private String facilityPhoto;
    private String facilityPincode;
    private int facilityStateId;
    private int facilityDistrictId;
    private int facilityBlockId;
    private int facilityTypeId;
    private String facilityAddress;
    private String facilityLatitude;
    private int facilityOwnershipTypeId;
    private String facilityTimings;
    private String facilityTypeCode;
    private String facilityType;
    private String facilityOwnershipNameEn;
    private String facilityOwnershipNameReg;
    private String facilityBaseLocation;
    private String state;
    private String districtName;
    private String blockName;
    private int facilityStateIdDuplicated;
    private int isSelected;
    private int assessmentId;
    private int usermappedid;

    // hierarchical mapped facilities
    private List<DistrictDTO> mappedFacilities;

    // Getters and Setters

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityNin() {
        return facilityNin;
    }

    public void setFacilityNin(String facilityNin) {
        this.facilityNin = facilityNin;
    }

    public String getFacilityLongitude() {
        return facilityLongitude;
    }

    public void setFacilityLongitude(String facilityLongitude) {
        this.facilityLongitude = facilityLongitude;
    }

    public String getFacilityPhoto() {
        return facilityPhoto;
    }

    public void setFacilityPhoto(String facilityPhoto) {
        this.facilityPhoto = facilityPhoto;
    }

    public String getFacilityPincode() {
        return facilityPincode;
    }

    public void setFacilityPincode(String facilityPincode) {
        this.facilityPincode = facilityPincode;
    }

    public int getFacilityStateId() {
        return facilityStateId;
    }

    public void setFacilityStateId(int facilityStateId) {
        this.facilityStateId = facilityStateId;
    }

    public int getFacilityDistrictId() {
        return facilityDistrictId;
    }

    public void setFacilityDistrictId(int facilityDistrictId) {
        this.facilityDistrictId = facilityDistrictId;
    }

    public int getFacilityBlockId() {
        return facilityBlockId;
    }

    public void setFacilityBlockId(int facilityBlockId) {
        this.facilityBlockId = facilityBlockId;
    }

    public int getFacilityTypeId() {
        return facilityTypeId;
    }

    public void setFacilityTypeId(int facilityTypeId) {
        this.facilityTypeId = facilityTypeId;
    }

    public String getFacilityAddress() {
        return facilityAddress;
    }

    public void setFacilityAddress(String facilityAddress) {
        this.facilityAddress = facilityAddress;
    }

    public String getFacilityLatitude() {
        return facilityLatitude;
    }

    public void setFacilityLatitude(String facilityLatitude) {
        this.facilityLatitude = facilityLatitude;
    }

    public int getFacilityOwnershipTypeId() {
        return facilityOwnershipTypeId;
    }

    public void setFacilityOwnershipTypeId(int facilityOwnershipTypeId) {
        this.facilityOwnershipTypeId = facilityOwnershipTypeId;
    }

    public String getFacilityTimings() {
        return facilityTimings;
    }

    public void setFacilityTimings(String facilityTimings) {
        this.facilityTimings = facilityTimings;
    }

    public String getFacilityTypeCode() {
        return facilityTypeCode;
    }

    public void setFacilityTypeCode(String facilityTypeCode) {
        this.facilityTypeCode = facilityTypeCode;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public String getFacilityOwnershipNameEn() {
        return facilityOwnershipNameEn;
    }

    public void setFacilityOwnershipNameEn(String facilityOwnershipNameEn) {
        this.facilityOwnershipNameEn = facilityOwnershipNameEn;
    }

    public String getFacilityOwnershipNameReg() {
        return facilityOwnershipNameReg;
    }

    public void setFacilityOwnershipNameReg(String facilityOwnershipNameReg) {
        this.facilityOwnershipNameReg = facilityOwnershipNameReg;
    }

    public String getFacilityBaseLocation() {
        return facilityBaseLocation;
    }

    public void setFacilityBaseLocation(String facilityBaseLocation) {
        this.facilityBaseLocation = facilityBaseLocation;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public int getFacilityStateIdDuplicated() {
        return facilityStateIdDuplicated;
    }

    public void setFacilityStateIdDuplicated(int facilityStateIdDuplicated) {
        this.facilityStateIdDuplicated = facilityStateIdDuplicated;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public int getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(int assessmentId) {
        this.assessmentId = assessmentId;
    }

    public int getUsermappedid() {
        return usermappedid;
    }

    public void setUsermappedid(int usermappedid) {
        this.usermappedid = usermappedid;
    }

    public List<DistrictDTO> getMappedFacilities() {
        return mappedFacilities;
    }

    public void setMappedFacilities(List<DistrictDTO> mappedFacilities) {
        this.mappedFacilities = mappedFacilities;
    }
}
