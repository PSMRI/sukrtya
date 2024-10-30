package com.piramal.spring_boot_demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
@Table(name = "facility")
public class Facility {

    @Id
    @Column(name = "facilityid")
    private String FacilityID;
    @Column(name = "facilityname_en")
    private String FacilityNameEn;
    @Column(name = "facilityname_hi")
    private String FacilityNameHn;
    @Column(name = "facility_type_id")
    private String facilityTypeID;

    @Column(name = "facility_nin")
    private String facilityNIN;

    @Column(name = "facilty_address")
    private String faciltyAddress;

    @Column(name = "facility_pincode")
    private String facilityPincode;

    @Column(name = "facilty_lattitude")
    private String faciltyLattitude;

    @Column(name = "facility_longitude")
    private String facilityLongitude;

    @Column(name = "facility_photo")
    private String facilityPhoto;

    @Column(name = "facilty_ownership_type_id")
    private String faciltyOwnershipTypeID;

    @Column(name = "facilty_timings")
    private String faciltyTimings;

    @Column(name = "facility_state_id")
    private String facilityStateID;

    @Column(name = "facility_district_id")
    private String facilityDistrictID;

    @Column(name = "facility_block_id")
    private String facilityBlockID;

    @Column(name = "facility_typeName_en")
    private String facilityTypeNameEN;

    @Column(name = "facility_typename_reg")
    private String facilityTypeNameReg;

    @Column(name = "facility_ownershipname_en")
    private String facilityOwnershipNameEN;
    @Column(name = "facility_ownershipname_reg")
    private String facilityOwnershipNameReg;

    @Column(name = "facility_base_location")
    private String facilityBaseLocation;

    @Column(name = "facility_state")
    private String FacilityState;

    @Column(name = "facility_district")
    private String FacilityDistrict;

    @Column(name = "facility_block")
    private String FacilityBlock;

    @Column(name = "regl_id")
    private String RegLId;

    public String getFacilityID() {
        return FacilityID;
    }

    public void setFacilityID(String facilityID) {
        FacilityID = facilityID;
    }

    public String getFacilityNameEn() {
        return FacilityNameEn;
    }

    public void setFacilityNameEn(String facilityNameEn) {
        FacilityNameEn = facilityNameEn;
    }

    public String getFacilityNameHn() {
        return FacilityNameHn;
    }

    public void setFacilityNameHn(String facilityNameHn) {
        FacilityNameHn = facilityNameHn;
    }

    public String isFacilityTypeID() {
        return facilityTypeID;
    }

    public void setFacilityTypeID(String facilityTypeID) {
        this.facilityTypeID = facilityTypeID;
    }

    public String getFacilityNIN() {
        return facilityNIN;
    }

    public void setFacilityNIN(String facilityNIN) {
        this.facilityNIN = facilityNIN;
    }

    public String getFaciltyAddress() {
        return faciltyAddress;
    }

    public void setFaciltyAddress(String faciltyAddress) {
        this.faciltyAddress = faciltyAddress;
    }

    public String getFacilityPincode() {
        return facilityPincode;
    }

    public void setFacilityPincode(String facilityPincode) {
        this.facilityPincode = facilityPincode;
    }

    public String getFaciltyLattitude() {
        return faciltyLattitude;
    }

    public void setFaciltyLattitude(String faciltyLattitude) {
        this.faciltyLattitude = faciltyLattitude;
    }

    public String getFacilityLongitude() {
        return facilityLongitude;
    }

    public void setFacilityLongitude(String facilityLongitude) {
        this.facilityLongitude=facilityLongitude;
    }

    public String getFacilityPhoto() {
        return facilityPhoto;
    }

    public void setFacilityPhoto(String facilityPhoto) {
        this.facilityPhoto = facilityPhoto;
    }

    public String getFaciltyOwnershipTypeID() {
        return faciltyOwnershipTypeID;
    }

    public void setFaciltyOwnershipTypeID(String faciltyOwnershipTypeID) {
        this.faciltyOwnershipTypeID = faciltyOwnershipTypeID;
    }

    public String getFaciltyTimings() {
        return faciltyTimings;
    }

    public void setFaciltyTimings(String faciltyTimings) {
        this.faciltyTimings = faciltyTimings;
    }

    public String getFacilityStateID() {
        return facilityStateID;
    }

    public void setFacilityStateID(String facilityStateID) {
        this.facilityStateID = facilityStateID;
    }

    public String getFacilityDistrictID() {
        return facilityDistrictID;
    }

    public void setFacilityDistrictID(String facilityDistrictID) {
        this.facilityDistrictID = facilityDistrictID;
    }

    public String getFacilityBlockID() {
        return facilityBlockID;
    }

    public void setFacilityBlockID(String facilityBlockID) {
        this.facilityBlockID = facilityBlockID;
    }

    public String getFacilityTypeNameEN() {
        return facilityTypeNameEN;
    }

    public void setFacilityTypeNameEN(String facilityTypeNameEN) {
        this.facilityTypeNameEN = facilityTypeNameEN;
    }

    public String getFacilityTypeNameReg() {
        return facilityTypeNameReg;
    }

    public void setFacilityTypeNameReg(String facilityTypeNameReg) {
        this.facilityTypeNameReg = facilityTypeNameReg;
    }

    public String getFacilityOwnershipNameEN() {
        return facilityOwnershipNameEN;
    }

    public void setFacilityOwnershipNameEN(String facilityOwnershipNameEN) {
        this.facilityOwnershipNameEN = facilityOwnershipNameEN;
    }

    public String getFacilityOwnershipNameReg() {
        return facilityOwnershipNameReg;
    }

    public void setFacilityOwnershipNameReg(String facilityOwnershipNameReg) {
        this.facilityOwnershipNameReg = facilityOwnershipNameReg;
    }

    public String getRegLId() {
        return RegLId;
    }

    public void setRegLId(String regLId) {
        RegLId = regLId;
    }


    public String getFacilityBaseLocation() {
        return facilityBaseLocation;
    }

    public void setFacilityBaseLocation(String facilityBaseLocation) {
        this.facilityBaseLocation = facilityBaseLocation;
    }

    public String getFacilityState() {
        return FacilityState;
    }

    public void setFacilityState(String facilityState) {
        FacilityState = facilityState;
    }

    public String getFacilityDistrict() {
        return FacilityDistrict;
    }

    public void setFacilityDistrict(String facilityDistrict) {
        FacilityDistrict = facilityDistrict;
    }

    public String getFacilityBlock() {
        return FacilityBlock;
    }

    public void setFacilityBlock(String facilityBlock) {
        FacilityBlock = facilityBlock;
    }

}
