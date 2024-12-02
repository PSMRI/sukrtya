package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.FormSubmissionRequest;
import com.piramal.sukrtya.exceptions.handler.ApiResponse;
import com.piramal.sukrtya.services.FormAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment")
public class    FormSubmissionController {
    private final FormAnswerService formAnswerService;

public FormSubmissionController(FormAnswerService formAnswerService) {
    this.formAnswerService = formAnswerService;
}
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> saveFormSubmission(@RequestBody FormSubmissionRequest request) {
        formAnswerService.saveOrUpdateFormSubmission(request);
        ApiResponse<String> response = new ApiResponse<>(
                "success",
                "Form submission saved successfully.",
                null
        );
        return ResponseEntity.ok(response);
    }
}

