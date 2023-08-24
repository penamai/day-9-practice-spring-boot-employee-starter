package com.afs.restapi.serviceTest;

import com.afs.restapi.entity.Company;
import com.afs.restapi.entity.Employee;
import com.afs.restapi.exception.CompanyNotFoundException;
import com.afs.restapi.repository.CompanyJpaRepository;
import com.afs.restapi.repository.EmployeeJpaRepository;
import com.afs.restapi.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class CompanyServiceTest {
    @Autowired
    private CompanyService companyService;
    private CompanyJpaRepository mockedCompanyRepository;
    private EmployeeJpaRepository mockedEmployeeRepository;

    @BeforeEach
    void setup() {
        mockedCompanyRepository = mock(CompanyJpaRepository.class);
        mockedEmployeeRepository = mock(EmployeeJpaRepository.class);
        companyService = new CompanyService(mockedCompanyRepository, mockedEmployeeRepository);
    }

    @Test
    void should_return_created_company_when_create_given_company() {
        Company company = new Company(null, "Company name");
        Company savedCompany = new Company(1L, "Company name");
        when(mockedCompanyRepository.save(company)).thenReturn(savedCompany);

        Company createdCompany = companyService.create(company);

        assertEquals(1L, createdCompany.getId());
        assertEquals("Company name", createdCompany.getName());
    }

    @Test
    void should_update_company_name_when_update_given_company() {
        Company company = new Company(1L, "Brandname");
        Company updatedCompanyInfo = new Company (null, "BrandNameNew");
        Company updatedCompany = new Company(company.getId(), updatedCompanyInfo.getName());

        when(mockedCompanyRepository.findById(company.getId())).thenReturn(Optional.of(company));
        when(mockedCompanyRepository.save(updatedCompany)).thenReturn(updatedCompany);

        companyService.update(company.getId(), updatedCompanyInfo);

        verify(mockedCompanyRepository).save(argThat(tempCompany -> {
                assertEquals(updatedCompany.getId(), tempCompany.getId());
                assertEquals(updatedCompany.getName(), tempCompany.getName());
                return true;
        }));
    }

    @Test
    void should_return_all_companies_when_findAll() {
        List<Company> companies = new ArrayList<>();
        companies.add(new Company(1L, "JAJAJA"));
        companies.add(new Company(2L, "stuq"));
        companies.add(new Company(3L, "woooo"));
        when(mockedCompanyRepository.findAll()).thenReturn(companies);

        List<Company> retrievedCompanies = companyService.findAll();

        assertThat(companies).hasSameElementsAs(retrievedCompanies);
    }

    @Test
    void should_return_company_when_findById_given_company_id() {
        Company company = new Company(1L, "Harmony");
        when(mockedCompanyRepository.findById(company.getId())).thenReturn(Optional.of(company));

        Company retrievedCompany = companyService.findById(company.getId());

        assertEquals(company.getId(), retrievedCompany.getId());
        assertEquals(company.getName(), retrievedCompany.getName());
    }

    @Test
    void should_return_companyNotFoundException_when_findById_given_nonexistent_company_id() {
        long nonexistentId = 100L;
        when(mockedCompanyRepository.findById(nonexistentId)).thenThrow(CompanyNotFoundException.class);
        assertThatThrownBy(() -> companyService.findById(nonexistentId))
                                  .isInstanceOf(CompanyNotFoundException.class);
    }

    @Test
    void should_return_list_of_companies_when_findByPage_given_pageSize_and_pageNumber() {
        List<Company> companies = new ArrayList<>();
        companies.add(new Company(1L, "JAJAJA"));
        companies.add(new Company(2L, "stuq"));
        companies.add(new Company(3L, "woooo"));

        Page<Company> companyPage = new PageImpl<>(companies, PageRequest.of(0, 3), companies.size());

        when(mockedCompanyRepository.findAll(PageRequest.of(0,3))).thenReturn(companyPage);

        List<Company> retrievedCompanies = companyService.findByPage(1,3);

        assertThat(companies).hasSameElementsAs(retrievedCompanies);
    }

    @Test
    void should_return_list_of_employees_when_findEmployeesByCompanyId_given_company_id() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1L, "Ababa", 20, "Female", 10000));
        employees.add(new Employee(1L, "Brrr", 54, "Male", 2000));
        employees.add(new Employee(1L, "Cheess", 35, "Male", 18000));
        when(mockedEmployeeRepository.findAllByCompanyId(1L)).thenReturn(employees);

        List<Employee> retrievedEmployees = companyService.findEmployeesByCompanyId(1L);

        assertThat(employees).hasSameElementsAs(retrievedEmployees);
    }
}
