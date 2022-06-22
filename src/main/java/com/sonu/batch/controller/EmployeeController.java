package com.sonu.batch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class EmployeeController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @GetMapping("/runEmployeeBatchJob")
    public String runEmployeeBatchJob() {

        JobParameters jobParameters = new JobParametersBuilder().addString("DateTime", String.valueOf(LocalDateTime.now())).toJobParameters();
        try {
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
            return "Job Execution  Failed";
        }
        return "Job Executed Successfully";
    }
}
