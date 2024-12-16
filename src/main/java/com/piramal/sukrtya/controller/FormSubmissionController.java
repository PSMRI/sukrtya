package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.FormSubmissionRequest;
import com.piramal.sukrtya.exceptions.handler.ApiResponse;
import com.piramal.sukrtya.services.FormAnswerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessment")
public class    FormSubmissionController {
    private static final Logger logger = LogManager.getLogger(FormSubmissionController.class);

    private final FormAnswerService formAnswerService;

    public FormSubmissionController(FormAnswerService formAnswerService) {
        this.formAnswerService = formAnswerService;
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> saveFormSubmission(@RequestBody FormSubmissionRequest request) {
        logger.info("Received form submission request: {}", request);

        formAnswerService.saveOrUpdateFormSubmission(request);
        logger.info("Form submission processed successfully for request: {}", request);

        ApiResponse<String> response = new ApiResponse<>(
                "success",
                "Form submission saved successfully.",
                null
        );
        return ResponseEntity.ok(response);
    }
}

