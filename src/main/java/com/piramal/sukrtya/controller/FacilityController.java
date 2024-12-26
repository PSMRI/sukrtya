package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.FacilityDetailDTO;
import com.piramal.sukrtya.services.FacilityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("sukrtya/api")
public class FacilityController {
    private static final Logger logger = LoggerFactory.getLogger(FacilityController.class);
    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }


    @GetMapping("/facilities")

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