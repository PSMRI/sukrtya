package com.piramal.spring_boot_demo.controller;

import com.piramal.spring_boot_demo.models.Facility;
import com.piramal.spring_boot_demo.services.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/facility")
public class FacilityController {

@Autowired
private FacilityService facilityService;

    @GetMapping("GetFacilityList")
   public List<Facility> getFacilityList() {
       return facilityService.getAllFacilities();
   }

    @GetMapping("GetFacilityList/{id}")
    public Optional<Facility> getFacilityById(@PathVariable String id) {
        return facilityService.getFacilityById(id);
    }
}
