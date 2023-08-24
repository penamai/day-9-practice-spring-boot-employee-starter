package com.afs.restapi.apiTest;

import com.afs.restapi.entity.Employee;
import com.afs.restapi.repository.EmployeeJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeJpaRepository employeeJpaRepository;

    @BeforeEach
    void setUp() {
        employeeJpaRepository.deleteAll();
    }

    @Test
    void should_update_employee_age_and_salary_when_put_given_employee_id_and_updated_info() throws Exception {
        Employee previousEmployee = new Employee(1L, "zhangsan", 22, "Male", 1000);
        Employee savedEmployee = employeeJpaRepository.save(previousEmployee);

        Employee employeeUpdateRequest = new Employee(1L, "lisi", 24, "Female", 2000);
        ObjectMapper objectMapper = new ObjectMapper();
        String updatedEmployeeJson = objectMapper.writeValueAsString(employeeUpdateRequest);
        mockMvc.perform(put("/employees/{id}", savedEmployee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedEmployeeJson))
                .andExpect(MockMvcResultMatchers.status().is(204));

        Optional<Employee> optionalEmployee = employeeJpaRepository.findById(savedEmployee.getId());
        assertTrue(optionalEmployee.isPresent());
        Employee updatedEmployee = optionalEmployee.get();
        Assertions.assertEquals(employeeUpdateRequest.getAge(), updatedEmployee.getAge());
        Assertions.assertEquals(employeeUpdateRequest.getSalary(), updatedEmployee.getSalary());
        Assertions.assertEquals(savedEmployee.getId(), updatedEmployee.getId());
        Assertions.assertEquals(previousEmployee.getName(), updatedEmployee.getName());
        Assertions.assertEquals(previousEmployee.getGender(), updatedEmployee.getGender());
    }

    @Test
    void should_return_created_employee_when_post_given_employee() throws Exception {
        Employee employee = getEmployeeBob();

        ObjectMapper objectMapper = new ObjectMapper();
        String employeeRequest = objectMapper.writeValueAsString(employee);
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeRequest))
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(employee.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(employee.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value(employee.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.salary").value(employee.getSalary()));
    }

    @Test
    void should_return_list_of_employees_when_get_employees() throws Exception {
        Employee employee = getEmployeeBob();
        Employee savedEmployee = employeeJpaRepository.save(employee);

        mockMvc.perform(get("/employees"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(savedEmployee.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(employee.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(employee.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value(employee.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary").value(employee.getSalary()));
    }

    @Test
    void should_return_employee_when_get_employee_given_employee_id() throws Exception {
        Employee employee = getEmployeeBob();
        Employee savedEmployee = employeeJpaRepository.save(employee);

        mockMvc.perform(get("/employees/{id}", savedEmployee.getId()))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedEmployee.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(employee.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(employee.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value(employee.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.salary").value(employee.getSalary()));
    }

    @Test
    void should_delete_employee_when_delete_given_employee_id() throws Exception {
        Employee employee = getEmployeeBob();
        Employee savedEmployee = employeeJpaRepository.save(employee);

        mockMvc.perform(delete("/employees/{id}", savedEmployee.getId()))
                .andExpect(MockMvcResultMatchers.status().is(204));

        assertTrue(employeeJpaRepository.findById(1L).isEmpty());
    }

    @Test
    void should_return_list_of_employees_when_get_given_gender() throws Exception {
        Employee employee = getEmployeeBob();
        Employee savedEmployee = employeeJpaRepository.save(employee);

        mockMvc.perform(get("/employees?gender={0}", "Male"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(savedEmployee.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(employee.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(employee.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value(employee.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary").value(employee.getSalary()));
    }

    @Test
    void should_return_list_of_employees_when_get_given_pageNumber_and_pageSize() throws Exception {
        Employee employeeBob = getEmployeeBob();
        Employee employeeSusan = getEmployeeSusan();
        Employee employeeLily = getEmployeeLily();
        Employee savedBob = employeeJpaRepository.save(employeeBob);
        Employee savedSusan = employeeJpaRepository.save(employeeSusan);
        employeeJpaRepository.save(employeeLily);

        mockMvc.perform(get("/employees")
                        .param("pageNumber", "1")
                        .param("pageSize", "2"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(savedBob.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(savedBob.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(savedBob.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value(savedBob.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary").value(savedBob.getSalary()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(savedSusan.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(savedSusan.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].age").value(savedSusan.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].gender").value(savedSusan.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].salary").value(savedSusan.getSalary()));
    }

    private static Employee getEmployeeBob() {
        Employee employee = new Employee();
        employee.setName("Bob");
        employee.setAge(22);
        employee.setGender("Male");
        employee.setSalary(10000);
        return employee;
    }

    private static Employee getEmployeeSusan() {
        Employee employee = new Employee();
        employee.setName("Susan");
        employee.setAge(23);
        employee.setGender("Female");
        employee.setSalary(11000);
        return employee;
    }

    private static Employee getEmployeeLily() {
        Employee employee = new Employee();
        employee.setName("Lily");
        employee.setAge(24);
        employee.setGender("Female");
        employee.setSalary(12000);
        return employee;
    }
}