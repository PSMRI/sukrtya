package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.AuditRequestDto;
import com.piramal.sukrtya.DTO.UpdateFormApprovalDTO;
import com.piramal.sukrtya.exceptions.handler.ApiResponse;
import com.piramal.sukrtya.services.AuditService;
import com.piramal.sukrtya.services.FormApprovalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sukrtya/api/form-approval")
public class FormApprovalController {

    private final FormApprovalService formApprovalService;
    private final AuditService auditService;
    public FormApprovalController(FormApprovalService formApprovalService, AuditService auditService) {
        this.formApprovalService = formApprovalService;
        this.auditService = auditService;
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateFormApproval(@RequestBody UpdateFormApprovalDTO updateFormApprovalDTO) {
        boolean isUpdated = formApprovalService.updateFormApproval(updateFormApprovalDTO);
        if (isUpdated) {
            return ResponseEntity.ok(new ApiResponse<>("success", "Form approval updated successfully.", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("error", "Form approval not found.", null));
        }
    }
    @PostMapping("/auditTrail")
    public ResponseEntity<ApiResponse<Void>> postAuditTrail(@RequestBody AuditRequestDto auditRequestDto) {
        // Insert into Audit Trail
        auditService.auditTrailInsert(
                auditRequestDto.getActionId(),
                auditRequestDto.getDescription(),
                auditRequestDto.getCreatedBy(),
                auditRequestDto.getTransactionId()
        );

        // Insert into Action Transactions
        auditService.actionTrnsInsert(
                auditRequestDto.getActionId(),
                auditRequestDto.getTransactionId()
        );

        return ResponseEntity.ok(new ApiResponse<>("success", "Audit trail recorded successfully.", null));
    }
}