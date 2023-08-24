package com.afs.restapi.serviceTest;

import com.afs.restapi.entity.Company;
import com.afs.restapi.repository.CompanyJpaRepository;
import com.afs.restapi.repository.EmployeeJpaRepository;
import com.afs.restapi.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
}
