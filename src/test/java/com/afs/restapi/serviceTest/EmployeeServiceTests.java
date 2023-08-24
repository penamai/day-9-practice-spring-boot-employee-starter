package com.afs.restapi.serviceTest;

import com.afs.restapi.entity.Employee;
import com.afs.restapi.repository.EmployeeJpaRepository;
import com.afs.restapi.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class EmployeeServiceTests {
    @Autowired
    private EmployeeService employeeService;

    private EmployeeJpaRepository mockedEmployeeRepository;

    @BeforeEach
    void setup() {
        mockedEmployeeRepository = mock(EmployeeJpaRepository.class);
        employeeService = new EmployeeService(mockedEmployeeRepository);
    }

    @Test
    void should_return_created_employee_when_create_given_employee() {
        Employee employee = new Employee(null, "Lucy", 20, "Female", 3000);
        Employee savedEmployee = new Employee(1L, "Lucy", 20, "Female", 3000);
        when(mockedEmployeeRepository.save(employee)).thenReturn(savedEmployee);

        Employee createdEmployee = employeeService.create(employee);

        assertEquals(savedEmployee.getId(), createdEmployee.getId());
        assertEquals(savedEmployee.getName(), createdEmployee.getName());
        assertEquals(savedEmployee.getAge(), createdEmployee.getAge());
        assertEquals(savedEmployee.getGender(), createdEmployee.getGender());
        assertEquals(savedEmployee.getSalary(), createdEmployee.getSalary());
        assertEquals(savedEmployee.getCompanyId(), createdEmployee.getCompanyId());
    }

}
