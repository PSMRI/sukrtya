package com.piramal.sukrtya.DTO;

import java.util.List;

// DTO for main Question response
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

    public int getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(int questionId)
    {
        this.questionId = questionId;
    }

    public String getQuestionName()
    {
        return questionName;
    }

    public void setQuestionName(String questionName)
    {
        this.questionName = questionName;
    }

    public String getQuestionType()
    {
        return questionType;
    }

    public void setQuestionType(String questionType)
    {
        this.questionType = questionType;
    }

    public String getFaAnswers()
    {
        return faAnswers;
    }

    public void setFaAnswers(String faAnswers)
    {
        this.faAnswers = faAnswers;
    }

    public List<QuestionOptionDTO> getQuestionOptions()
    {
        return QuestionOptions;
    }

    public void setQuestionOptions(
        List<QuestionOptionDTO> questionOptions)
    {
        QuestionOptions = questionOptions;
    }

    public String getSkipanswer()
    {
        return skipanswer;
    }

    public void setSkipanswer(String skipanswer)
    {
        this.skipanswer = skipanswer;
    }

    public int getSkipQuestionId()
    {
        return skipQuestionId;
    }

    public void setSkipQuestionId(int skipQuestionId)
    {
        this.skipQuestionId = skipQuestionId;
    }

    public String getAnswer()
    {
        return Answer;
    }

    public void setAnswer(String answer)
    {
        Answer = answer;
    }

    public int getAnswerID()
    {
        return AnswerID;
    }

    public void setAnswerID(int answerID)
    {
        AnswerID = answerID;
    }

    public String getIsMandate()
    {
        return isMandate;
    }

    public void setIsMandate(String isMandate)
    {
        this.isMandate = isMandate;
    }

    public long getMaxvalue()
    {
        return maxvalue;
    }

    public void setMaxvalue(long maxvalue)
    {
        this.maxvalue = maxvalue;
    }

    public long getMinvalue()
    {
        return minvalue;
    }

    public void setMinvalue(long minvalue)
    {
        this.minvalue = minvalue;
    }
}