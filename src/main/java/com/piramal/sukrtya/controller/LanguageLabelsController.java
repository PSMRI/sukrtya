package com.piramal.sukrtya.controller;

import com.piramal.sukrtya.services.LanguageLabelsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("sukrtya/api/language-labels")
public class LanguageLabelsController {

    private final LanguageLabelsService languageLabelsService;

    public LanguageLabelsController(LanguageLabelsService languageLabelsService) {
        this.languageLabelsService = languageLabelsService;
    }

    @GetMapping("/getLabels")
    public ResponseEntity<List<Map<String, String>>> getLabels(
            @RequestParam int formId,
            @RequestParam int regLId) {
        List<Map<String, String>> labels = languageLabelsService.getLabelsInCustomFormat(formId, regLId);
        return ResponseEntity.ok(labels);
    }
}

