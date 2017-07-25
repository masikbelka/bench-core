package com.epam.bench.facade;

import com.epam.bench.domain.Employee;
import com.epam.bench.web.rest.dto.EmployeeDto;
import com.epam.bench.web.rest.dto.UpdateEmployeeFormDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BenchFacade {
    boolean isEmployeeOnBench(String upsaId);

    EmployeeDto addToBench(String upsaId);

    EmployeeDto updateEmployee(UpdateEmployeeFormDto employee);

    Page<EmployeeDto> search(String query, Pageable pageable);

    void removeFromBench(String upsaId);

    EmployeeDto findOne(String upsaId);

    Page<EmployeeDto> findAll(Pageable pageable);
}
