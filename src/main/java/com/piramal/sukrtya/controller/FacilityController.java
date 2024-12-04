package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.FacilityDetailDTO;
import com.piramal.sukrtya.services.FacilityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FacilityController {
    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping("/GetFacilityList")
    @PreAuthorize("hasRole('ADMIN')")
    public List<FacilityDetailDTO> getFacilityList(@RequestParam Integer UserId,
                                                   @RequestParam Integer RegLid,
                                                   @RequestParam Integer MappingUserId) {
        return facilityService.getFacilityList(UserId, RegLid, MappingUserId);
    }
    @GetMapping("/public/data")
    public String publicData() {
        return "This data is accessible to everyone.";
    }
    @GetMapping("/public/secure")
    public Map<String, String> SecureData() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This data is secure to everyone.");
        return response;
    }

}