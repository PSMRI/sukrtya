package com.piramal.sukrtya.services;

import com.piramal.sukrtya.DTO.UpdateFormApprovalDTO;
import com.piramal.sukrtya.repository.FormApprovalRepository;
import org.springframework.stereotype.Service;

@Service
public class FormApprovalService {

    private final FormApprovalRepository formApprovalRepository;

    public FormApprovalService(FormApprovalRepository formApprovalRepository) {
        this.formApprovalRepository = formApprovalRepository;
    }

    public boolean updateFormApproval(UpdateFormApprovalDTO updateFormApprovalDTO) {
        return formApprovalRepository.updateFormApproval(
                updateFormApprovalDTO.getUserId(),
                updateFormApprovalDTO.getApprovalStatus(),
                updateFormApprovalDTO.getFormId(),
                updateFormApprovalDTO.getTransactionId()
        ) > 0;
    }
}

