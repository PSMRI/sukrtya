package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.FormTranslDTO;
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
    public Map<String, Object> getFormTranslList(
            @RequestParam("facilytyType") int facilityType,
            @RequestParam("FacilityId") int facilityId,
            @RequestParam("RgLId") int rgLId) {

        List<FormTranslDTO> objform = formTransactionService.getFormTranslList(facilityType, facilityId, rgLId);
        return new HashMap<String, Object>() {{ put("objform", objform); }};
    }
}

