package com.wch.job.controller;

import com.wch.job.model.JobDataFile;
import com.wch.job.model.JobDataResponse;
import com.wch.job.service.JobDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wch.job.constant.Constant.ASCENDING;

@RestController
public class JobController {

    @Autowired
    private JobDataService jobDataService;

    @GetMapping("/job_data")
    public JobDataResponse getJobData(
            @RequestParam(value = "job_title", required = false) String jobTitle,
            @RequestParam(value = "salary", required = false) Double salary,
            @RequestParam(value = "salaryComparison", required = false) String salaryComparison,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "sort", required = false) String sortField,
            @RequestParam(value = "sort_type", required = false, defaultValue = ASCENDING) String sortType
    ) {
        return jobDataService.getJobData(fields, sortField, sortType, jobTitle, salary, salaryComparison, gender);
    }
}