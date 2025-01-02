package com.piramal.sukrtya.services;

import com.piramal.sukrtya.repository.LanguageLabelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LanguageLabelsService {


    private final LanguageLabelsRepository languageLabelsRepository;

    public LanguageLabelsService(LanguageLabelsRepository languageLabelsRepository) {
        this.languageLabelsRepository = languageLabelsRepository;
    }

    public List<Map<String, String>> getLabelsInCustomFormat(int formId, int regLId) {
        List<Object[]> rawLabels = languageLabelsRepository.findLabelIdAndLabelByFormIdAndRegLId(formId, regLId);

        // Create a map for the response
        Map<String, String> labelMap = new LinkedHashMap<>();
        for (Object[] row : rawLabels) {
            Integer labelId = (Integer) row[0];
            String label = (String) row[1];
            labelMap.put(labelId.toString(), label);
        }

        // Wrap the map in a list to match the desired JSON structure
        return List.of(labelMap);
    }
}


