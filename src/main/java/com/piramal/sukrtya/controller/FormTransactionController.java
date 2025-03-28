package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.FormTransactionDTO;
import com.piramal.sukrtya.DTO.MappedFacilityUserDTO;
import com.piramal.sukrtya.services.FormTransactionService;
import com.piramal.sukrtya.services.MappedFacilityUserService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("sukrtya/api")
public class FormTransactionController {

    private static final Logger logger = LoggerFactory.getLogger(FormTransactionController.class);
    private final FormTransactionService formTransactionService;
    private final MappedFacilityUserService mappedFacilityUserService;
@Autowired
    public FormTransactionController(FormTransactionService formTransactionService, MappedFacilityUserService mappedFacilityUserService ) {
        this.formTransactionService = formTransactionService;
        this.mappedFacilityUserService = mappedFacilityUserService;
    }

    @GetMapping("/forms")
    public List<FormTransactionDTO>  getFormTranslList(
            @RequestParam("facilytyType") int facilityType,
            @RequestParam("FacilityId") int facilityId,
            @RequestParam("RgLId") int rgLId,
            @RequestParam("AssessmentId") int assesmentId) {
        logger.info("Received request to get form transactions with parameters: facilityType={}, facilityId={}, rgLId={}",
                facilityType, facilityId, rgLId);

        List<FormTransactionDTO> objform = formTransactionService.getFormTranslList(facilityType, facilityId, rgLId,assesmentId);

        logger.info("Successfully retrieved form transactions for facilityType={}, facilityId={}, rgLId={}",
                facilityType, facilityId, rgLId);

        return objform;
    }

    @GetMapping("/mapped-facility-users")
    public List<MappedFacilityUserDTO> getMappedFacilityUsers(
            @RequestParam Long facilityId,
            @RequestParam Long regLId) {
        return mappedFacilityUserService.getMappedFacilityUsers(facilityId, regLId);
    }

}

