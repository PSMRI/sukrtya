package com.piramal.spring_boot_demo.controller;

import com.piramal.spring_boot_demo.DTO.FacilityDetailDTO;
import com.piramal.spring_boot_demo.services.FacilityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FacilityController {
    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping("/GetFacilityList")
    public List<FacilityDetailDTO> getFacilityList(@RequestParam Integer UserId,
                                                   @RequestParam Integer RegLid,
                                                   @RequestParam Integer MappingUserId) {
        return facilityService.getFacilityList(UserId, RegLid, MappingUserId);
    }
}