package com.piramal.sukrtya.repository;


import com.piramal.sukrtya.DTO.BlockDTO;
import com.piramal.sukrtya.DTO.ClusterDTO;
import com.piramal.sukrtya.DTO.DistrictDTO;
import com.piramal.sukrtya.DTO.FacilityDTO;
import com.piramal.sukrtya.DTO.FacilityDetailDTO;
import com.piramal.sukrtya.DTO.MappedFacilityDTO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            facility.setAssessmentId(rs.getInt("assessmentid"));
            facility.setUsermappedid(rs.getInt("usermappedid"));
            
            // List<MappedFacilityDTO> mappedFacilities = getMappedFacilitiesByUserMapping(facility.getUsermappedid());
            // facility.setMappedFacilities(mappedFacilities);
            List<DistrictDTO> mappedFacilities = getMappedFacilitiesByUserMappingHierarchical(facility.getUsermappedid());
            facility.setMappedFacilities(mappedFacilities);

            return facility;
        });
    }
    // public List<MappedFacilityDTO> getMappedFacilitiesByUserMapping(Integer userMappedId) {
    //     String sql = "SELECT * FROM get_facilities_by_user_mapping(?)";

    //     return jdbcTemplate.query(sql, new Object[]{userMappedId}, (rs, rowNum) -> {
    //         MappedFacilityDTO mapped = new MappedFacilityDTO();
    //         mapped.setFacilityId(rs.getInt("facilityid"));
    //         mapped.setFacilityName(rs.getString("facilitynameen"));
    //         mapped.setFacilityNin(rs.getString("facilitynin"));
    //         mapped.setDistrictName(rs.getString("district_name"));
    //         mapped.setDistrictCode(rs.getInt("district_code"));
    //         mapped.setBlockName(rs.getString("block_name"));
    //         mapped.setBlockCode(rs.getInt("block_code"));
    //         mapped.setClusterId(rs.getInt("clusterid"));
    //         mapped.setClusterName(rs.getString("cluster_name"));
    //         return mapped;
    //     });
    // }
    public List<DistrictDTO> getMappedFacilitiesByUserMappingHierarchical(Integer userMappedId) {
        String sql = "SELECT * FROM get_facilities_by_user_mapping(?)";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userMappedId);

        Map<String, DistrictDTO> districtMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            String districtCode = (String) row.get("district_code");
            String districtName = (String) row.get("district_name");
            String blockCode = (String) row.get("block_code");
            String blockName = (String) row.get("block_name");
            Integer clusterId = (Integer) row.get("clusterid");
            String clusterName = (String) row.get("cluster_name");

            Integer facilityId = (Integer) row.get("facilityid");
            String facilityName = (String) row.get("facilitynameen");
            String facilityNin = (String) row.get("facilitynin");

            // District
            DistrictDTO district = districtMap.computeIfAbsent(districtCode, dc -> {
                DistrictDTO d = new DistrictDTO();
                d.setDistrictCode(dc);
                d.setDistrictName(districtName);
                d.setBlocks(new ArrayList<>());
                return d;
            });

            // Block
            BlockDTO block = district.getBlocks().stream()
                .filter(b -> b.getBlockCode().equals(blockCode))
                .findFirst()
                .orElseGet(() -> {
                    BlockDTO b = new BlockDTO();
                    b.setBlockCode(blockCode);
                    b.setBlockName(blockName);
                    b.setClusters(new ArrayList<>());
                    district.getBlocks().add(b);
                    return b;
                });

            // Cluster
            ClusterDTO cluster = block.getClusters().stream()
                .filter(c -> Objects.equals(c.getClusterId(), clusterId))
                .findFirst()
                .orElseGet(() -> {
                    ClusterDTO c = new ClusterDTO();
                    c.setClusterId(clusterId);
                    c.setClusterName(clusterName);
                    c.setFacilities(new ArrayList<>());
                    block.getClusters().add(c);
                    return c;
                });

            // Facility
            FacilityDTO facility = new FacilityDTO();
            facility.setFacilityId(facilityId);
            facility.setFacilityName(facilityName);
            facility.setFacilityNin(facilityNin);
            cluster.getFacilities().add(facility);
        }

        return new ArrayList<>(districtMap.values());
    }
}

