package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.DTO.QuestionResponseDTO;
import com.piramal.sukrtya.services.QuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/getQuestionDetails")
    public List<QuestionResponseDTO> getQuestionDetails(
            @RequestParam int formId,
            @RequestParam int RegLId,
            @RequestParam(required = false) String transActionId) {

        return questionService.getQuestionDetails(formId, transActionId);
    }
}