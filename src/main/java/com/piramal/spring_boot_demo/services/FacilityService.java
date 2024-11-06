package com.piramal.spring_boot_demo.services;

import com.piramal.spring_boot_demo.DTO.FacilityDetailDTO;
import com.piramal.spring_boot_demo.repository.FacilityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityService {
    private final FacilityRepository facilityRepository;

    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    public List<FacilityDetailDTO> getFacilityList(Integer userId, Integer regLid, Integer mappingUserId) {
        return facilityRepository.getFacilityDetails(userId, regLid, mappingUserId);
    }
}