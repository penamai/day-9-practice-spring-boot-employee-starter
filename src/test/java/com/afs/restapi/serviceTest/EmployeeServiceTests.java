package com.afs.restapi.serviceTest;

import com.afs.restapi.entity.Employee;
import com.afs.restapi.exception.EmployeeNotFoundException;
import com.afs.restapi.repository.EmployeeJpaRepository;
import com.afs.restapi.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class EmployeeServiceTests {
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

    @Test
    void should_update_age_and_salary_of_employee_when_update_given_employee_and_updated_employee_info() {
        Employee employee = new Employee(1L, "Delilah", 50, "Female", 10000);
        Employee updatedEmployeeInfo = new Employee(null, "Namee", 51, "Female", 11000);
        Employee updatedEmployee = new Employee(employee.getId(),employee.getName(),updatedEmployeeInfo.getAge(),employee.getGender(), updatedEmployeeInfo.getSalary());

        when(mockedEmployeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        employeeService.update(employee.getId(), updatedEmployeeInfo);

        verify(mockedEmployeeRepository).save(argThat(tempEmployee -> {
                assertEquals(updatedEmployee.getId(), tempEmployee.getId());
                assertEquals(updatedEmployee.getName(), tempEmployee.getName());
                assertEquals(updatedEmployee.getAge(), tempEmployee.getAge());
                assertEquals(updatedEmployee.getGender(), tempEmployee.getGender());
                assertEquals(updatedEmployee.getSalary(), tempEmployee.getSalary());
                return true;
        }));
    }

    @Test
    void should_return_all_employees_when_findAll() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1L, "Ababa", 20, "Female", 10000));
        employees.add(new Employee(2L, "Brrr", 54, "Male", 2000));
        employees.add(new Employee(3L, "Cheess", 35, "Male", 18000));
        when(mockedEmployeeRepository.findAll()).thenReturn(employees);

        List<Employee> retrievedEmployees = employeeService.findAll();

        assertThat(employees).hasSameElementsAs(retrievedEmployees);
    }

    @Test
    void should_return_correct_employee_when_findById_given_employee_id() {
        Employee employee = new Employee(1L, "Ababa", 20, "Female", 10000);
        when(mockedEmployeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        Employee retrievedEmployee = employeeService.findById(employee.getId());

        assertEquals(employee.getId(), retrievedEmployee.getId());
        assertEquals(employee.getName(), retrievedEmployee.getName());
        assertEquals(employee.getAge(), retrievedEmployee.getAge());
        assertEquals(employee.getGender(), retrievedEmployee.getGender());
        assertEquals(employee.getSalary(), retrievedEmployee.getSalary());
        assertEquals(employee.getCompanyId(), retrievedEmployee.getCompanyId());
    }

    @Test
    void should_return_employeeNotFoundException_when_findById_given_nonexistent_id() {
        long nonexistentId = 100L;
        when(mockedEmployeeRepository.findById(nonexistentId)).thenThrow(EmployeeNotFoundException.class);
        assertThatThrownBy(() -> employeeService.findById(nonexistentId))
                                  .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void should_return_female_employees_when_findByGender_given_female_parameter() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1L, "Ababa", 20, "Female", 10000));
        employees.add(new Employee(2L, "Blaire", 54, "Female", 2000));
        employees.add(new Employee(3L, "Claire", 35, "Female", 18000));
        when(mockedEmployeeRepository.findAllByGender("Female")).thenReturn(employees);

        List<Employee> retrievedEmployees = employeeService.findAllByGender("Female");

        assertThat(employees).hasSameElementsAs(retrievedEmployees);
    }

    @Test
    void should_return_list_of_employees_when_findByPage_given_pageSize_and_pageNumber() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1L, "Ababa", 20, "Female", 10000));
        employees.add(new Employee(2L, "Brrr", 54, "Male", 2000));
        employees.add(new Employee(3L, "Cheess", 35, "Male", 18000));

        Page<Employee> employeePage = new PageImpl<>(employees, PageRequest.of(0, 3), employees.size());

        when(mockedEmployeeRepository.findAll(PageRequest.of(0, 3))).thenReturn(employeePage);

        List<Employee> retrievedEmployees = employeeService.findByPage(1, 3);

        assertThat(employees).hasSameElementsAs(retrievedEmployees);
    }

    @Test
    void should_delete_employee_when_delete_given_employee_id() {
        employeeService.delete(1L);
        verify(mockedEmployeeRepository).deleteById(1L);
    }
}
