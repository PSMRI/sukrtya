package com.piramal.sukrtya.DTO;

public class MappedFacilityUserDTO {
    private String profile;
    private String profileMobile;
    private Long profileId;
    private Boolean isGovt;
    private String userType;

    public MappedFacilityUserDTO(String profile, String profileMobile, Long profileId, Boolean isGovt, String userType) {
        this.profile = profile;
        this.profileMobile = profileMobile;
        this.profileId = profileId;
        this.isGovt = isGovt;
        this.userType = userType;
    }

    // ✅ Getters & Setters
    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }

    public String getProfileMobile() { return profileMobile; }
    public void setProfileMobile(String profileMobile) { this.profileMobile = profileMobile; }

    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }

    public Boolean getIsGovt() { return isGovt; }
    public void setIsGovt(Boolean isGovt) { this.isGovt = isGovt; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}
