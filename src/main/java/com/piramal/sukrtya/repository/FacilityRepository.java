package com.piramal.sukrtya.repository;


import com.piramal.sukrtya.DTO.FacilityDetailDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FacilityRepository {
    private final JdbcTemplate jdbcTemplate;

    public FacilityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FacilityDetailDTO> getFacilityDetails(Integer userId, Integer regLid, Integer mappingUserId) {
        String sql = "SELECT * FROM get_facility_details(?, ?)";
        return jdbcTemplate.query(sql, new Object[]{regLid, mappingUserId}, (rs, rowNum) -> {
            FacilityDetailDTO facility = new FacilityDetailDTO();
            facility.setFacilityBlockId(rs.getInt("facilityblockid"));
            facility.setFacilityDistrictId(rs.getInt("facilitydistrictid"));
            facility.setFacilityId(rs.getInt("facilityid"));
            facility.setFacilityLongitude(rs.getString("facilitylongitude"));
            facility.setFacilityName(rs.getString("facilityname"));
            facility.setFacilityNin(rs.getString("facilitynin"));
            facility.setFacilityPhoto(rs.getString("facilityphoto"));
            facility.setFacilityPincode(rs.getString("facilitypincode"));
            facility.setFacilityStateId(rs.getInt("facilitystateid"));
            facility.setFacilityTypeId(rs.getInt("facilitytypeid"));
            facility.setFacilityAddress(rs.getString("faciltyaddress"));
            facility.setFacilityLatitude(rs.getString("faciltylattitude"));
            facility.setFacilityOwnershipTypeId(rs.getInt("faciltyownershiptypeid"));
            facility.setFacilityTimings(rs.getString("faciltytimings"));
            facility.setFacilityTypeCode(rs.getString("facilitytypecode"));
            facility.setFacilityType(rs.getString("facilitytype"));
            facility.setFacilityOwnershipNameEn(rs.getString("facilityownershipnameen"));
            facility.setFacilityOwnershipNameReg(rs.getString("facilityownershipnamereg"));
            facility.setFacilityBaseLocation(rs.getString("facilitybaselocation"));
            facility.setState(rs.getString("state"));
            facility.setDistrictName(rs.getString("district_name"));
            facility.setBlockName(rs.getString("blockname"));
            facility.setFacilityStateIdDuplicated(rs.getInt("facilitystateid_duplicated"));
            facility.setIsSelected(rs.getInt("isselected"));
            return facility;
        });
    }
}

