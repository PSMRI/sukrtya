package com.piramal.spring_boot_demo.services;

import com.piramal.spring_boot_demo.DTO.FormTranslDTO;
import com.piramal.spring_boot_demo.repository.FormTranslRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FormTranslService {

    @Autowired
    private FormTranslRepository repository;

    public List<FormTranslDTO> getFormTranslList(int facilityType, int facilityId, int rgLId) {
        return repository.getFormTranslList(facilityType, facilityId, rgLId);
    }
}

