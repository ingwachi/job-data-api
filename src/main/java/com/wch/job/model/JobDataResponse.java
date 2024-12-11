package com.wch.job.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@Accessors(chain = true)
public class JobDataResponse {
    private int totalItems;
    private List<JobData> jobDataList;
}
