package com.wch.job.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobData {
    private String timestamp;
    private String employer;
    private String location;
    private String jobTitle;
    private String yearsAtEmployer;
    private String yearsOfExperience;
    private String salary;
    private String signingBonus;
    private String annualBonus;
    private String annualStockValueBonus;
    private String gender;
    private String additionalComments;

    public JobData(JobDataFile jobDataFile) {
        this.timestamp = jobDataFile.getTimestamp();
        this.employer = jobDataFile.getEmployer();
        this.location = jobDataFile.getLocation();
        this.jobTitle = jobDataFile.getJobTitle();
        this.yearsAtEmployer = jobDataFile.getYearsAtEmployer();
        this.yearsOfExperience = jobDataFile.getYearsOfExperience();
        this.salary = jobDataFile.getSalary();
        this.signingBonus = jobDataFile.getSigningBonus();
        this.annualBonus = jobDataFile.getAnnualBonus();
        this.annualStockValueBonus = jobDataFile.getAnnualStockValueBonus();
        this.gender = jobDataFile.getGender();
        this.additionalComments = jobDataFile.getAdditionalComments();
    }
}
