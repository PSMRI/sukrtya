package com.piramal.spring_boot_demo.controller;

import com.piramal.spring_boot_demo.DTO.FormTranslDTO;
import com.piramal.spring_boot_demo.services.FormTranslService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FormTranslController {

    @Autowired
    private FormTranslService service;

    @GetMapping("/GetFormTranslList")
    public Map<String, Object> getFormTranslList(
            @RequestParam("facilytyType") int facilityType,
            @RequestParam("FacilityId") int facilityId,
            @RequestParam("RgLId") int rgLId) {

        List<FormTranslDTO> objform = service.getFormTranslList(facilityType, facilityId, rgLId);

        Map<String, Object> response = new HashMap<>();
        response.put("objform", objform);
        return response;
    }
}

