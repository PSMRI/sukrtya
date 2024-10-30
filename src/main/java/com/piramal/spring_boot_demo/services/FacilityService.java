package com.piramal.spring_boot_demo.services;

import com.piramal.spring_boot_demo.models.Facility;
import com.piramal.spring_boot_demo.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacilityService {
    @Autowired
    private FacilityRepository facilityRepository;

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }
    public Optional<Facility>  getFacilityById(String id) {
        return facilityRepository.findById(id);
    }
}

