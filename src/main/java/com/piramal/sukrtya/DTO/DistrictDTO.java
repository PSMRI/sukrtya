package com.piramal.sukrtya.DTO;

import java.util.List;

public class DistrictDTO {
    private String districtCode;
    private String districtName;
    private List<BlockDTO> blocks;
    
    // Getter and Setter for districtCode
    public String getDistrictCode() {
        return districtCode;
    }
    
    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }
    
    // Getter and Setter for districtName
    public String getDistrictName() {
        return districtName;
    }
    
    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }
    
    // Getter and Setter for blocks
    public List<BlockDTO> getBlocks() {
        return blocks;
    }
    
    public void setBlocks(List<BlockDTO> blocks) {
        this.blocks = blocks;
    }
}