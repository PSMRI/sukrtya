package com.piramal.sukrtya.DTO;

public class PostAnswer {
    private Integer questionId;
    private String questionType;
    private String Answer;
    private Integer AnswerID;

    // Getter and Setter for questionId
    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    // Getter and Setter for questionType
    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    // Getter and Setter for Answer
    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        this.Answer = answer;
    }

    // Getter and Setter for AnswerID
    public Integer getAnswerID() {
        return AnswerID;
    }

    public void setAnswerID(Integer answerID) {
        this.AnswerID = answerID;
    }
}

