package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.FacilityDetailDTO;
import com.piramal.sukrtya.services.FacilityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class FacilityController {
    private static final Logger logger = LogManager.getLogger(FacilityController.class);
    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping("/GetFacilityList")
    @PreAuthorize("hasRole('ADMIN')")
    public List<FacilityDetailDTO> getFacilityList(@RequestParam Integer UserId,
                                                   @RequestParam Integer RegLid,
                                                   @RequestParam Integer MappingUserId) {
        logger.info("Fetching facility list for UserId={}, RegLid={}, MappingUserId={}", UserId, RegLid, MappingUserId);

        List<FacilityDetailDTO> facilityList = facilityService.getFacilityList(UserId, RegLid, MappingUserId);

        logger.info("Successfully retrieved {} facilities for UserId={}, RegLid={}, MappingUserId={}",
                facilityList.size(), UserId, RegLid, MappingUserId);
        return facilityList;
    }


}