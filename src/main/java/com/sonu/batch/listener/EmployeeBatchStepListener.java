package com.sonu.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class EmployeeBatchStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("BATCH STEP STARTED SUCCESSFULLY");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("BATCH STEP COMPLETED SUCCESSFULLY");
        return ExitStatus.COMPLETED;
    }
}