package com.piramal.sukrtya.services;

import com.piramal.sukrtya.DTO.FormSubmissionRequest;
import com.piramal.sukrtya.DTO.PostAnswer;
import com.piramal.sukrtya.entity.TblFaAnswers;
import com.piramal.sukrtya.entity.TblFormTransactions;
import com.piramal.sukrtya.repository.FaAnswersRepository;
import com.piramal.sukrtya.repository.FormSubmissionRepository;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class FormAnswerService {

    private final FormSubmissionRepository formSubmissionRepository;

    private final FaAnswersRepository faAnswersRepository;

    public FormAnswerService(FormSubmissionRepository formSubmissionRepository, FaAnswersRepository faAnswersRepository) {
        this.formSubmissionRepository = formSubmissionRepository;
        this.faAnswersRepository = faAnswersRepository;
    }

    public void saveOrUpdateFormSubmission(FormSubmissionRequest request) {
        // Generate Transaction ID if null
        String transactionId = request.getTransactionId();
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = generateTransactionId(request.getFacilityNIN());
            request.setTransactionId(transactionId);
        }

        TblFormTransactions formSubmission = formSubmissionRepository.findByTransactionId(transactionId)
                .orElse(new TblFormTransactions());

        formSubmission.setFormId(request.getFormId());
        formSubmission.setUserId(request.getUserId());
        formSubmission.setTransactionId(transactionId);
        formSubmission.setSubmitDate(LocalDateTime.now().toString());
        formSubmission.setLattitude(request.getLattitude());
        formSubmission.setLongitude(request.getLongitude());
        formSubmission.setGoogleAddress(request.getGoogleAddress());

        formSubmissionRepository.save(formSubmission);

        // Save or update answers in tblfaanswers
        for (PostAnswer answer : request.getPostAnswer()) {
            TblFaAnswers faAnswer = faAnswersRepository.findByFaTransactionsIDAndFaQIDAndFormId(
                    transactionId,
                    answer.getQuestionId(),
                    request.getFormId()
            ).orElse(new TblFaAnswers());

            faAnswer.setFaTransactionsID(transactionId);
            faAnswer.setFaQID(answer.getQuestionId());
            faAnswer.setFormId(request.getFormId());

            if ("Camera".equalsIgnoreCase(answer.getQuestionType()) && isBase64Encoded(answer.getAnswer())){
                String filePath = saveImage(answer.getAnswer(), transactionId, request.getFormId(), answer.getQuestionId());
                faAnswer.setFaAnswers(filePath);
            } else {
                faAnswer.setFaAnswers(answer.getAnswer());
            }

            faAnswersRepository.save(faAnswer);
        }
    }
    private String generateTransactionId(String nin) {
        LocalDateTime now = LocalDateTime.now();
        String month = String.format("%02d", now.getMonthValue());
        String yearLastTwoDigits = String.valueOf(now.getYear()).substring(2);
        return nin + month + yearLastTwoDigits;
    }
    private String saveImage(String base64Image, String transactionId, Integer formId, Integer questionId) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);
            String fileName = formId + questionId + transactionId + ".jpg";
            String filePath = "/SukrtyaImages/SurveyPhoto/" + fileName;

            // Check if the file exists
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
            Files.write(path, decodedBytes);

            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Error saving image", e);
        }
    }
    private boolean isBase64Encoded(String base64String) {
        try {
            String base64Data = base64String.split(",")[1]; // Extract the Base64 data after the comma
            Base64.getDecoder().decode(base64Data);
            return true;
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
}

