package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.FormTransactionDTO;
import com.piramal.sukrtya.services.FormTransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FormTransactionController {
    private final FormTransactionService formTransactionService;

    public FormTransactionController(FormTransactionService formTransactionService  ) {
        this.formTransactionService = formTransactionService;
    }

    @GetMapping("/GetFormTransactionList")
    public List<FormTransactionDTO>  getFormTranslList(
            @RequestParam("facilytyType") int facilityType,
            @RequestParam("FacilityId") int facilityId,
            @RequestParam("RgLId") int rgLId) {

        List<FormTransactionDTO> objform = formTransactionService.getFormTranslList(facilityType, facilityId, rgLId);
        return formTransactionService.getFormTranslList(facilityType, facilityId, rgLId);
    }

}

