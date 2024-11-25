package com.piramal.sukrtya.services;

import com.piramal.sukrtya.DTO.FormTransactionDTO;
import com.piramal.sukrtya.repository.FormTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormTransactionService {


    private final FormTransactionRepository repository;

    public FormTransactionService(FormTransactionRepository repository) {
        this.repository = repository;
    }

    public List<FormTransactionDTO> getFormTranslList(int facilityType, int facilityId, int rgLId) {
        return repository.getFormTranslList(facilityType, facilityId, rgLId);
    }
}

