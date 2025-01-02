package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.QuestionResponseDTO;
import com.piramal.sukrtya.services.QuestionService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("sukrtya/api")
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/questions")
    public List<QuestionResponseDTO> getQuestionDetails(
            @RequestParam int formId,
            @RequestParam int RegLId,
            @RequestParam(required = false) String transActionId) {
        logger.info("Received request to get question details with parameters: formId={}, RegLId={}, transActionId={}",
                formId, RegLId, transActionId);

        List<QuestionResponseDTO> questionDetails = questionService.getQuestionDetails(formId,RegLId, transActionId);

        logger.info("Successfully retrieved question details for formId={}, RegLId={}, transActionId={}",
                formId, RegLId, transActionId);

        return questionDetails;
    }
}