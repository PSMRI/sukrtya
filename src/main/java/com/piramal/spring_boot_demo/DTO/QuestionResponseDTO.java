package com.piramal.spring_boot_demo.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// DTO for main Question response
@Getter@Setter
public class QuestionResponseDTO {
    private int questionId;
    private String questionName;
    private String questionType;
    private String faAnswers;
    private List<QuestionOptionDTO> QuestionOptions;
    private String skipanswer;
    private int skipQuestionId;
    private String Answer;
    private int AnswerID;
    private String isMandate;
    private long maxvalue;
    private long minvalue;
    // Getters and setters
}