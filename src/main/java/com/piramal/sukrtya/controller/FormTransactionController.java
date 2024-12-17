package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.FormTransactionDTO;
import com.piramal.sukrtya.services.FormTransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(FormTransactionController.class);
    private final FormTransactionService formTransactionService;

    public FormTransactionController(FormTransactionService formTransactionService  ) {
        this.formTransactionService = formTransactionService;
    }

    @GetMapping("/forms")
    public List<FormTransactionDTO>  getFormTranslList(
            @RequestParam("facilytyType") int facilityType,
            @RequestParam("FacilityId") int facilityId,
            @RequestParam("RgLId") int rgLId) {
        logger.info("Received request to get form transactions with parameters: facilityType={}, facilityId={}, rgLId={}",
                facilityType, facilityId, rgLId);

        List<FormTransactionDTO> objform = formTransactionService.getFormTranslList(facilityType, facilityId, rgLId);

        logger.info("Successfully retrieved form transactions for facilityType={}, facilityId={}, rgLId={}",
                facilityType, facilityId, rgLId);

        return objform;
    }

}

