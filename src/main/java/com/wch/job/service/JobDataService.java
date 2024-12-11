package com.wch.job.service;

import com.wch.job.model.JobData;
import com.wch.job.model.JobDataFile;
import com.wch.job.model.JobDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.wch.job.constant.Constant.*;


@Service
@Slf4j
public class JobDataService {

    @Autowired
    private ReadFileDataService readFileDataService;

    public JobDataResponse getJobData(
            String fields,
            String sort,
            String sortType,
            String jobTitle,
            Double salary,
            String salaryComparison,
            String gender
    ) {
        //Read data from JSON file
        List<JobDataFile> jobDataFileList = readFileDataService.readDataFromFile();

        //Filter by one or more fields/attributes
        List<JobDataFile> filteredData = filterJobDataByField(jobDataFileList, jobTitle, salary, salaryComparison, gender);

        //Sort by one or more fields/attributes
        if (sort != null) {
            String[] sortFields = sort.split(COMMA);  // Split sort fields by comma
            Comparator<JobDataFile> comparator = null;

            //Loop through the sort fields to build a composite comparator
            for (String field : sortFields) {
                Comparator<JobDataFile> fieldComparator = createComparator(field.trim());
                if (comparator == null) {
                    comparator = fieldComparator;
                } else {
                    comparator = comparator.thenComparing(fieldComparator);
                }
            }

            //Reverse the comparator if sort_type is DESC
            if (DESCENDING.equalsIgnoreCase(sortType) && comparator != null) {
                comparator = comparator.reversed();
            }

            // Apply the comparator to sort the filtered data
            filteredData.sort(comparator);
        }

        //Select sparse fields
        if (fields != null) {
            List<String> selectedFields = List.of(fields.split(COMMA));
            filteredData = filteredData.stream()
                    .map(job -> filterFields(job, selectedFields))
                    .collect(Collectors.toList());
        }

        return new JobDataResponse()
                .setJobDataList(filteredData.stream().map(JobData::new).toList())
                .setTotalItems(filteredData.size());
    }

    private Comparator<JobDataFile> createComparator(String field) {
        return switch (field) {
            case FIELD_TIME_STAMP -> Comparator.comparing(
                    JobDataFile::getTimestamp,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case FIELD_EMPLOYER -> Comparator.comparing(
                    JobDataFile::getEmployer,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case FIELD_LOCATION -> Comparator.comparing(
                    JobDataFile::getLocation,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case FIELD_JOB_TITLE -> Comparator.comparing(
                    job -> job.getJobTitle() == null || job.getJobTitle().isEmpty() ? null : job.getJobTitle(),
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case FIELD_YEARS_AT_EMPLOYER -> Comparator.comparing(
                    JobDataFile::getYearsAtEmployer,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case FIELD_YEARS_OF_EXP -> Comparator.comparing(
                    JobDataFile::getYearsOfExperience,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case FIELD_SALARY -> Comparator.comparing(
                    job -> {
                        String cleanedSalary = cleanStringData(job.getSalary());
                        return cleanedSalary.isEmpty() ? null : Double.parseDouble(cleanedSalary);
                    },
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case FIELD_SIGNING_BONUS -> Comparator.comparing(
                    JobDataFile::getSigningBonus,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case FIELD_ANNUAL_BONUS -> Comparator.comparing(
                    JobDataFile::getAnnualBonus,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case FIELD_ANNUAL_STOCK_VALUE_BONUS -> Comparator.comparing(
                    JobDataFile::getAnnualStockValueBonus,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case FIELD_GENDER -> Comparator.comparing(
                    JobDataFile::getGender,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case SORT_FIELD_ADDITIONAL_COMMENTS -> Comparator.comparing(
                    JobDataFile::getAdditionalComments,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            default -> throw new IllegalArgumentException("Invalid sort field: " + field);
        };
    }

    private JobDataFile filterFields(JobDataFile job, List<String> fields) {
        JobDataFile sparseJob = new JobDataFile();
        // Check and set fields if they exist in the 'fields' list
        if (fields.contains(FIELD_TIME_STAMP)) sparseJob.setTimestamp(job.getTimestamp());
        if (fields.contains(FIELD_EMPLOYER)) sparseJob.setEmployer(job.getEmployer());
        if (fields.contains(FIELD_LOCATION)) sparseJob.setLocation(job.getLocation());
        if (fields.contains(FIELD_JOB_TITLE)) sparseJob.setJobTitle(job.getJobTitle());
        if (fields.contains(FIELD_YEARS_AT_EMPLOYER)) sparseJob.setYearsAtEmployer(job.getYearsAtEmployer());
        if (fields.contains(FIELD_YEARS_OF_EXP)) sparseJob.setYearsOfExperience(job.getYearsOfExperience());
        if (fields.contains(FIELD_SALARY)) sparseJob.setSalary(job.getSalary());
        if (fields.contains(FIELD_SIGNING_BONUS)) sparseJob.setSigningBonus(job.getSigningBonus());
        if (fields.contains(FIELD_ANNUAL_BONUS)) sparseJob.setAnnualBonus(job.getAnnualBonus());
        if (fields.contains(FIELD_ANNUAL_STOCK_VALUE_BONUS)) sparseJob.setAnnualStockValueBonus(job.getAnnualStockValueBonus());
        if (fields.contains(FIELD_GENDER)) sparseJob.setGender(job.getGender());
        if (fields.contains(SORT_FIELD_ADDITIONAL_COMMENTS)) sparseJob.setAdditionalComments(job.getAdditionalComments());

        return sparseJob;
    }


    private List<JobDataFile> filterJobDataByField(List<JobDataFile> jobDataFileList, String jobTitle, Double salary, String salaryComparison, String gender) {
        return jobDataFileList.stream()
                .filter(job -> applyJobTitleFilter(jobTitle, job))
                .filter(job -> applySalaryFilter(salary, salaryComparison, job))
                .filter(job -> applyGenderFilter(gender, job))
                .collect(Collectors.toList());
    }

    private boolean applyJobTitleFilter(String jobTitle, JobDataFile job) {
        return jobTitle == null || job.getJobTitle().equalsIgnoreCase(jobTitle);
    }

    private boolean applySalaryFilter(Double salary, String salaryComparison, JobDataFile job) {
        if (salary == null) {
            return true;
        }

        String cleanedSalary = cleanStringData(job.getSalary());
        if (cleanedSalary.isEmpty()) {
            return false;
        }

        double jobSalary = Double.parseDouble(cleanedSalary);

        return switch (salaryComparison != null ? salaryComparison.toLowerCase() : "") {
            case GTE -> jobSalary >= salary;
            case LTE -> jobSalary <= salary;
            default -> false;
        };
    }

    private boolean applyGenderFilter(String gender, JobDataFile job) {
        return gender == null || job.getGender().equalsIgnoreCase(gender);
    }

    private String cleanStringData(String salary) {
        return salary != null ? salary.replaceAll("\\D", EMPTY_STRING) : EMPTY_STRING;
    }
}
