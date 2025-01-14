package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.AuditRequestDto;
import com.piramal.sukrtya.DTO.UpdateFormApprovalDTO;
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
    public ResponseEntity<String> updateFormApproval(@RequestBody UpdateFormApprovalDTO updateFormApprovalDTO) {
        try {
            boolean isUpdated = formApprovalService.updateFormApproval(updateFormApprovalDTO);
            if (isUpdated) {
                return ResponseEntity.ok("Form approval updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Form approval not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
    @PostMapping("/AuditTrail")
    public ResponseEntity<SuccessStatus> postAuditTrail(@RequestBody AuditRequestDto auditRequestDto) {

        // Insert into Audit Trail
        auditService.auditTrailInsert(
                auditRequestDto.getActionId(),
                auditRequestDto.getDescription(),
                auditRequestDto.getCreatedBy()
        );

        // Insert into Action Transactions
        auditService.actionTrnsInsert(
                auditRequestDto.getActionId(),
                auditRequestDto.getTransactionId()
        );

        SuccessStatus successStatus = new SuccessStatus("Success");
        return ResponseEntity.ok(successStatus);
    }
    public static class SuccessStatus {
        private String success;

        public SuccessStatus(String success) {
            this.success = success;
        }

        public String getSuccess() {
            return success;
        }

        public void setSuccess(String success) {
            this.success = success;
        }
    }
}