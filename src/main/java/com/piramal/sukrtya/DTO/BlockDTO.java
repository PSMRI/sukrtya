package com.piramal.sukrtya.DTO;

import java.util.List;

public class BlockDTO {
    private String blockCode;
    private String blockName;
    private List<ClusterDTO> clusters;
    
    // Getter and Setter for blockCode
    public String getBlockCode() {
        return blockCode;
    }
    
    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }
    
    // Getter and Setter for blockName
    public String getBlockName() {
        return blockName;
    }
    
    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }
    
    // Getter and Setter for clusters
    public List<ClusterDTO> getClusters() {
        return clusters;
    }
    
    public void setClusters(List<ClusterDTO> clusters) {
        this.clusters = clusters;
    }
}