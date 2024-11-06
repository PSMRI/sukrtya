package com.piramal.spring_boot_demo.controller;

import com.piramal.spring_boot_demo.DTO.QuestionResponseDTO;
import com.piramal.spring_boot_demo.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/getQuestionDetails")
    public List<QuestionResponseDTO> getQuestionDetails(
            @RequestParam int formId,
            @RequestParam int RegLId,
            @RequestParam(required = false) String transActionId) {

        return questionService.getQuestionDetails(formId, transActionId);
    }
}