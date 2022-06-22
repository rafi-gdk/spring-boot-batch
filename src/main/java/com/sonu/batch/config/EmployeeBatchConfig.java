package com.sonu.batch.config;

import com.sonu.batch.listener.EmployeeBatchJobListener;
import com.sonu.batch.listener.EmployeeBatchStepListener;
import com.sonu.batch.model.Employee;
import com.sonu.batch.repository.EmployeeRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
public class EmployeeBatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Bean
    public Job runEmployeeBatchJob() {

        return jobBuilderFactory.get("importEmployees")
                .listener(new EmployeeBatchJobListener())
                .flow(employeeBatchStep())
                .end()
                .build();
    }

    @Bean
    public Step employeeBatchStep() {

        return stepBuilderFactory.get("csv-step")
                .listener(new EmployeeBatchStepListener())
                .<Employee, Employee>chunk(10)
                .reader(employeeBatchFileReader())
                .processor(employeeBatchFileProcessor())
                .writer(employeeBatchFileWriter())
                .taskExecutor(taskExecuter())
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

        return new RepositoryItemWriterBuilder<Employee>()
                .repository(new RepositoryItemWriterBuilder.RepositoryMethodReference<Employee>(employeeRepository))
                .methodName("save")
                .build();
    }

    private TaskExecutor taskExecuter() {

        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(100);
        return asyncTaskExecutor;
    }

}
