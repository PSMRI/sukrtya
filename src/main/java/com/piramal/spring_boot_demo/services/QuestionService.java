package com.piramal.spring_boot_demo.services;

import com.piramal.spring_boot_demo.DTO.QuestionOptionDTO;
import com.piramal.spring_boot_demo.DTO.QuestionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {

//    @Autowired
//    private JdbcTemplate jdbcTemplate;

//    public List<QuestionResponseDTO> getQuestionDetails(int param) {
//        // Main query to get question details
//        String mainQuery = "SELECT * FROM get_question_details(?)";
//
//        List<QuestionResponseDTO> questions = jdbcTemplate.query(
//                mainQuery,
//                new Object[]{param},
//                (rs, rowNum) -> {
//                    QuestionResponseDTO question = new QuestionResponseDTO();
//                    question.setQuestionId(rs.getInt("questionid"));
//                    question.setQuestionName(rs.getString("questionname"));
//                    question.setQuestionType(rs.getString("questiontype"));
//                    question.setFaAnswers(rs.getString("faanswers"));
//                    question.setSkipanswer(rs.getString("skipanswer"));
//                    question.setSkipQuestionId(rs.getInt("skipquestionid"));
//                    question.setAnswer(rs.getString("answer"));
//                    question.setAnswerID(rs.getInt("answerid"));
//                    question.setIsMandate(rs.getString("ismandate"));
//                    question.setMaxvalue(rs.getLong("maxvalue"));
//                    question.setMinvalue(rs.getLong("minvalue"));
//
//                    // Fetch options if question type is Single Choice
//                    if ("Single Choice".equals(question.getQuestionType())) {
//                        question.setQuestionOptions(getOptionsForFaAnswers(question.getFaAnswers()));
//                    }
//
//                    return question;
//                }
//        );
//
//        return questions;
//    }
//
//    private List<QuestionOptionDTO> getOptionsForFaAnswers(String faAnswers) {
//        List<QuestionOptionDTO> options = new ArrayList<>();
//
//        // Split faAnswers and fetch options for each value
//        for (String answerId : faAnswers.split("/")) {
//            String optionQuery = "SELECT optionid as Value, optionnameen as Text FROM public.tbloptionmaster WHERE optionid = ?";
//            QuestionOptionDTO option = jdbcTemplate.queryForObject(
//                    optionQuery,
//                    new Object[]{Integer.parseInt(answerId)},
//                    (rs, rowNum) -> {
//                        QuestionOptionDTO opt = new QuestionOptionDTO();
//                        opt.setValue(rs.getInt("Value"));
//                        opt.setText(rs.getString("Text"));
//                        return opt;
//                    }
//            );
//            options.add(option);
//        }
//
//        return options;
//    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<QuestionResponseDTO> getQuestionDetails(int param, String tranid) {
        String mainQuery = "SELECT * FROM get_question_details(?)";

        List<QuestionResponseDTO> questions = jdbcTemplate.query(
                mainQuery,
                new Object[]{param},
                (rs, rowNum) -> {
                    QuestionResponseDTO question = new QuestionResponseDTO();
                    question.setQuestionId(rs.getInt("questionid"));
                    question.setQuestionName(rs.getString("questionname"));
                    question.setQuestionType(rs.getString("questiontype"));
                    question.setFaAnswers(rs.getString("faanswers"));
                    question.setSkipanswer(rs.getString("skipanswer"));
                    question.setSkipQuestionId(rs.getInt("skipquestionid"));
                    question.setIsMandate(rs.getString("ismandate"));
                    question.setMaxvalue(rs.getLong("maxvalue"));
                    question.setMinvalue(rs.getLong("minvalue"));

                    // Fetch Answer and AnswerID using tranid parameter if present
                    if (tranid != null) {
                        // Assuming faqid and formid are accessible or can be derived from the question object
                        int faqid = question.getQuestionId(); // Example usage; adjust as needed
                        int formid = param; // Example usage; adjust as needed

                        Map<String, String> answerData = getAnswerData(tranid, faqid, formid);
                        question.setAnswer(answerData.get("Answer"));
                        question.setAnswerID(Integer.parseInt(answerData.get("AnswerID")));
                    } else {
                        question.setAnswer("");
                        question.setAnswerID(0);
                    }

                    if ("Single Choice".equals(question.getQuestionType())) {
                        question.setQuestionOptions(getOptionsForFaAnswers(question.getFaAnswers()));
                    }

                    return question;
                }
        );

        return questions;
    }

    // Helper method to fetch options based on faAnswers values
    private List<QuestionOptionDTO> getOptionsForFaAnswers(String faAnswers) {
        List<QuestionOptionDTO> options = new ArrayList<>();
        for (String answerId : faAnswers.split("/")) {
            String optionQuery = "SELECT optionid as Value, optionnameen as Text FROM public.tbloptionmaster WHERE optionid = ?";
            QuestionOptionDTO option = jdbcTemplate.queryForObject(
                    optionQuery,
                    new Object[]{Integer.parseInt(answerId)},
                    (rs, rowNum) -> {
                        QuestionOptionDTO opt = new QuestionOptionDTO();
                        opt.setValue(rs.getInt("Value"));
                        opt.setText(rs.getString("Text"));
                        return opt;
                    }
            );
            options.add(option);
        }
        return options;
    }

    // Method to retrieve Answer and AnswerID based on parameters
    private Map<String, String> getAnswerData(String tranid, int faqid, int formid) {
        String answerQuery = "SELECT faanswers FROM tblfaanswers WHERE fatransactionsid = ? AND faqid = ? AND formid = ?";
      try{
        return jdbcTemplate.queryForObject(
                answerQuery,
                new Object[]{tranid, faqid, formid},
                (rs, rowNum) -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("Answer", rs.getString("faanswers"));
                    result.put("AnswerID", "0"); // Placeholder; adjust as necessary
                    return result;
                }
        );
      } catch (EmptyResultDataAccessException e) {
          // Handle case where no result is found
          Map<String, String> defaultResult = new HashMap<>();
          defaultResult.put("Answer", "");  // Default empty answer
          defaultResult.put("AnswerID", "0"); // Default AnswerID
          return defaultResult;
      } catch (Exception e) {
          // Handle other exceptions if needed
          e.printStackTrace();
          throw new RuntimeException("Error retrieving answer data", e);
      }
    }
}
