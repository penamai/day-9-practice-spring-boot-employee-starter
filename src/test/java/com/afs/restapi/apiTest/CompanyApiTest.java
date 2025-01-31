package com.afs.restapi.apiTest;

import com.afs.restapi.entity.Company;
import com.afs.restapi.entity.Employee;
import com.afs.restapi.repository.CompanyJpaRepository;
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
class CompanyApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyJpaRepository companyJpaRepository;

    @Autowired
    private EmployeeJpaRepository employeeJpaRepository;

    @BeforeEach
    void setUp() {
        companyJpaRepository.deleteAll();
        employeeJpaRepository.deleteAll();
    }

    @Test
    void should_update_company_name_when_put_given_id_and_updated_info() throws Exception {
        Company previousCompany = new Company(1L, "abc");
        Company savedCompany = companyJpaRepository.save(previousCompany);

        Company companyUpdateRequest = new Company(savedCompany.getId(), "xyz");
        ObjectMapper objectMapper = new ObjectMapper();
        String updatedEmployeeJson = objectMapper.writeValueAsString(companyUpdateRequest);
        mockMvc.perform(put("/companies/{id}", savedCompany.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedEmployeeJson))
                .andExpect(MockMvcResultMatchers.status().is(204));

        Optional<Company> optionalCompany = companyJpaRepository.findById(savedCompany.getId());
        assertTrue(optionalCompany.isPresent());
        Company updatedCompany = optionalCompany.get();
        Assertions.assertEquals(savedCompany.getId(), updatedCompany.getId());
        Assertions.assertEquals(companyUpdateRequest.getName(), updatedCompany.getName());
    }

    @Test
    void should_delete_company_when_delete_given_company_id() throws Exception {
        Company company = new Company(1L, "abc");
        Company savedCompany = companyJpaRepository.save(company);

        mockMvc.perform(delete("/companies/{id}", savedCompany.getId()))
                .andExpect(MockMvcResultMatchers.status().is(204));

        assertTrue(companyJpaRepository.findById(savedCompany.getId()).isEmpty());
    }

    @Test
    void should_return_created_company_when_post_given_company() throws Exception {
        Company company = getCompany1();

        ObjectMapper objectMapper = new ObjectMapper();
        String companyRequest = objectMapper.writeValueAsString(company);
        mockMvc.perform(post("/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyRequest))
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(company.getName()));
    }

    @Test
    void should_return_companies_when_get_companies() throws Exception {
        Company company = getCompany1();
        Company savedCompany = companyJpaRepository.save(company);

        mockMvc.perform(get("/companies"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(savedCompany.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(company.getName()));
    }

    @Test
    void should_return_list_of_companies_when_get_given_pageNumber_and_pageSize() throws Exception {
        Company company1 = getCompany1();
        Company company2 = getCompany2();
        Company company3 = getCompany3();
        Company savedCompany1 = companyJpaRepository.save(company1);
        Company savedCompany2 = companyJpaRepository.save(company2);
        companyJpaRepository.save(company3);

        mockMvc.perform(get("/companies")
                        .param("pageNumber", "1")
                        .param("pageSize", "2"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(savedCompany1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(savedCompany1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(savedCompany2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(savedCompany2.getName()))
        ;
    }

    @Test
    void should_return_company_when_get_given_company_id() throws Exception {
        Company company = getCompany1();
        Company savedCompany = companyJpaRepository.save(company);
        Employee employee = getEmployee(company);
        Employee savedEmployee = employeeJpaRepository.save(employee);

        mockMvc.perform(get("/companies/{id}", savedCompany.getId()))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedCompany.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(company.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].id").value(savedEmployee.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].name").value(employee.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].age").value(employee.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].gender").value(employee.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].salary").value(employee.getSalary()));
    }

    @Test
    void should_return_employees_when_get_employees_given_company_id() throws Exception {
        Company company = getCompany1();
        Company savedCompany = companyJpaRepository.save(company);
        Employee employee = getEmployee(company);
        Employee savedEmployee = employeeJpaRepository.save(employee);

        mockMvc.perform(get("/companies/{companyId}/employees", savedCompany.getId()))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(savedEmployee.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(employee.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(employee.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gender").value(employee.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].salary").value(employee.getSalary()));
    }

    private static Employee getEmployee(Company company) {
        Employee employee = new Employee();
        employee.setName("Bob");
        employee.setAge(22);
        employee.setGender("Male");
        employee.setSalary(10000);
        employee.setCompanyId(company.getId());
        return employee;
    }


    private static Company getCompany1() {
        Company company = new Company();
        company.setName("ABC");
        return company;
    }

    private static Company getCompany2() {
        Company company = new Company();
        company.setName("DEF");
        return company;
    }

    private static Company getCompany3() {
        Company company = new Company();
        company.setName("XYZ");
        return company;
    }
}