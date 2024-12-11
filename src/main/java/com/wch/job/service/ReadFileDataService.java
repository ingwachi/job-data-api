package com.wch.job.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wch.job.model.JobDataFile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ReadFileDataService {
    public List<JobDataFile> readDataFromFile() {
        String filePath = "src/main/resources/data/salary_survey-3.json";
        try {
            String jsonContent = Files.readString(Path.of(filePath));

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonContent, new TypeReference<List<JobDataFile>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file", e);
        }
    }
}
