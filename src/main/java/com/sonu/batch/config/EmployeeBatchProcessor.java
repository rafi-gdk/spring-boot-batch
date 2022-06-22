package com.sonu.batch.config;

import com.sonu.batch.model.Employee;
import org.springframework.batch.item.ItemProcessor;

public class EmployeeBatchProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee employee) {
        return employee;
    }
}
