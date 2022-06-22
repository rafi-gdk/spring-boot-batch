package com.sonu.batch.config;

import com.sonu.batch.listener.EmployeeBatchJobListener;
import com.sonu.batch.listener.EmployeeBatchStepListener;
import com.sonu.batch.model.Employee;
import com.sonu.batch.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class EmployeeBatchConfig {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private EmployeeRepository employeeRepository;

    @Bean
    public Job runEmployeeBatchJob() {

        return jobBuilderFactory.get("runEmployeeBatchJob")
                .listener(new EmployeeBatchJobListener())
                .flow(employeeBatchStep())
                .end()
                .build();
    }

    @Bean
    public Step employeeBatchStep() {

        return stepBuilderFactory.get("employeeBatchStep")
                .listener(new EmployeeBatchStepListener())
                .<Employee, Employee>chunk(1000)
                .reader(employeeBatchFileReader())
                .processor(employeeBatchFileProcessor())
                .writer(employeeBatchFileWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public FlatFileItemReader<Employee> employeeBatchFileReader() {

        return new FlatFileItemReaderBuilder<Employee>()
                .name("employeeBatchFileReader")
                .resource(new FileSystemResource("src/main/resources/100000_Sales_Records.csv"))
                .delimited()
                .names("Region", "Country", "ItemType", "SalesChannel", "OrderPriority", "OrderDate", "OrderID", "ShipDate", "UnitsSold", "UnitPrice", "UnitCost", "TotalRevenue", "TotalCost", "TotalProfit")
                .targetType(Employee.class)
                .linesToSkip(1)
                .strict(false)
                .build();
    }

    @Bean
    public EmployeeBatchProcessor employeeBatchFileProcessor() {
        return new EmployeeBatchProcessor();
    }

    @Bean
    public RepositoryItemWriter<Employee> employeeBatchFileWriter() {

        RepositoryItemWriter<Employee> repositoryItemWriter = new RepositoryItemWriter<>();
        repositoryItemWriter.setRepository(employeeRepository);
        repositoryItemWriter.setMethodName("save");
        return repositoryItemWriter;
    }

    private TaskExecutor taskExecutor() {

        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(5);
        return asyncTaskExecutor;
    }

}
