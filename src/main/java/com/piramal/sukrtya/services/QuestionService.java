package com.piramal.sukrtya.services;

import com.piramal.sukrtya.DTO.QuestionOptionDTO;
import com.piramal.sukrtya.DTO.QuestionResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class QuestionService {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    public QuestionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


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

                        Map<String, String> answerData = getAnswerData(tranid, faqid, formid, question.getQuestionType());
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
private Map<String, String> getAnswerData(String tranid, int faqid, int formid, String questionType) {
    String answerQuery = "SELECT faanswers FROM tblfaanswers WHERE fatransactionsid = ? AND faqid = ? AND formid = ?";
    try {
        return jdbcTemplate.queryForObject(
                answerQuery,
                new Object[]{tranid, faqid, formid},
                (rs, rowNum) -> {
                    Map<String, String> result = new HashMap<>();
                    String faAnswers = rs.getString("faanswers");

                    if ("Camera".equals(questionType)) {

                        // Get the current directory
                        String currentDirectory = Paths.get("").toAbsolutePath().toString();
                        // Construct the full image path
                        String imagePath = currentDirectory + File.separator + faAnswers;
                        // Log the image path
                        logger.info("Constructed image path: {}", imagePath);
                        // Convert image URL to Base64
                        String base64Image = convertImageUrlToBase64(imagePath);

                        logger.info("image : {}", base64Image);
                        result.put("Answer",base64Image);

                    } else {
                        result.put("Answer", faAnswers);
                    }

                    result.put("AnswerID", "0"); // Placeholder; adjust as necessary
                    return result;
                }
        );
    } catch (EmptyResultDataAccessException e) {
        Map<String, String> defaultResult = new HashMap<>();
        defaultResult.put("Answer", "");  // Default empty answer
        defaultResult.put("AnswerID", "0"); // Default AnswerID
        return defaultResult;
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Error retrieving answer data", e);
    }
}

    public String convertImageUrlToBase64(String imagePath) {
        try {
            // Read the image file as bytes
            File imageFile = new File(imagePath);
            if (!imageFile.exists() || !imageFile.isFile()) {
                return null;
            }
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

            // Encode the byte array to Base64
            return "data:image/jpeg;base64," +Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error converting image to Base64: " + imagePath, e);
        }
    }
}
