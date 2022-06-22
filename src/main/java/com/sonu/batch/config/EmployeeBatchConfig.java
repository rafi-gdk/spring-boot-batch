package com.sonu.batch.config;

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
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@AllArgsConstructor
@EnableBatchProcessing
public class EmployeeBatchConfig {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private EmployeeRepository employeeRepository;

    @Bean
    public FlatFileItemReader<Employee> reader() {

        FlatFileItemReader<Employee> fileItemReader = new FlatFileItemReader<>();
        fileItemReader.setResource(new FileSystemResource("src/main/resources/100000_Sales_Records.csv"));
        fileItemReader.setName("csv-reader");
        fileItemReader.setLineMapper(getLineMapper());
        fileItemReader.setLinesToSkip(1);
        return fileItemReader;
    }


    @Bean
    public EmployeeBatchProcessor processor() {
        return new EmployeeBatchProcessor();
    }

    @Bean
    public RepositoryItemWriter<Employee> writer() {

        RepositoryItemWriter<Employee> employeeRepositoryItemWriter = new RepositoryItemWriter<>();
        employeeRepositoryItemWriter.setRepository(employeeRepository);
        employeeRepositoryItemWriter.setMethodName("save");
        return employeeRepositoryItemWriter;
    }

    @Bean
    public Step step1() {

        return stepBuilderFactory.get("csv-step").<Employee, Employee>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecuter())
                .build();
    }

    @Bean
    public Job runJob() {

        return jobBuilderFactory.get("importEmployees")
                .flow(step1()).end()
                .build();
    }

    private TaskExecutor taskExecuter() {

        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(100);
        return asyncTaskExecutor;
    }

    private LineMapper<Employee> getLineMapper() {

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setStrict(false);
        delimitedLineTokenizer.setNames("Region", "Country", "ItemType", "SalesChannel", "OrderPriority", "OrderDate", "OrderID", "ShipDate", "UnitsSold", "UnitPrice", "UnitCost", "TotalRevenue", "TotalCost", "TotalProfit");

        BeanWrapperFieldSetMapper<Employee> employeeBeanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        employeeBeanWrapperFieldSetMapper.setTargetType(Employee.class);

        DefaultLineMapper<Employee> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(delimitedLineTokenizer);
        mapper.setFieldSetMapper(employeeBeanWrapperFieldSetMapper);
        return mapper;
    }

}
