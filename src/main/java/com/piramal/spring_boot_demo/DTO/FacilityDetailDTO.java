package com.piramal.spring_boot_demo.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class FacilityDetailDTO {
    private Integer facilityBlockId;
    private Integer facilityDistrictId;
    private Integer facilityId;
    private String facilityLongitude;
    private String facilityName;
    private String facilityNin;
    private String facilityPhoto;
    private String facilityPincode;
    private Integer facilityStateId;
    private Integer facilityTypeId;
    private String facilityAddress;
    private String facilityLatitude;
    private Integer facilityOwnershipTypeId;
    private String facilityTimings;
    private String facilityTypeCode;
    private String facilityType;
    private String facilityOwnershipNameEn;
    private String facilityOwnershipNameReg;
    private String facilityBaseLocation;
    private String state;
    private String districtName;
    private String blockName;
    private Integer facilityStateIdDuplicated;
    private Integer isSelected;

    // Getters and setters
}
