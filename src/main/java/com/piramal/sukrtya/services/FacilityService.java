package com.piramal.sukrtya.services;

import com.piramal.sukrtya.DTO.FacilityDetailDTO;
import com.piramal.sukrtya.repository.FacilityRepository;
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