package com.epam.bench.facade.impl;

import com.epam.bench.domain.Employee;
import com.epam.bench.facade.BenchFacade;
import com.epam.bench.facade.converter.Converter;
import com.epam.bench.service.EmployeeService;
import com.epam.bench.web.rest.dto.EmployeeDto;
import com.epam.bench.web.rest.dto.UpdateEmployeeFormDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class BenchFacadeImpl implements BenchFacade {

    private final EmployeeService employeeService;
    private final Converter<Employee, EmployeeDto> employee2DtoConverter;

    public BenchFacadeImpl(EmployeeService employeeService, Converter<Employee, EmployeeDto> employee2DtoConverter) {
        this.employeeService = employeeService;
        this.employee2DtoConverter = employee2DtoConverter;
    }

    @Override
    public boolean isEmployeeOnBench(final String upsaId) {
        return false;
    }

    @Override
    public EmployeeDto addToBench(final String upsaId) {
        return null;
    }

    @Override
    public EmployeeDto updateEmployee(final UpdateEmployeeFormDto employee) {
        return null;
    }

    @Override
    public Page<EmployeeDto> search(final String query, final Pageable pageable) {
        final Page<Employee> employees = employeeService.search(query, pageable);

        return new PageImpl<>(employee2DtoConverter.convertAll(employees.getContent()));
    }

    @Override
    public void removeFromBench(final String upsaId) {

    }

    @Override
    public EmployeeDto findOne(final String upsaId) {
        return employee2DtoConverter.convert(employeeService.findOne(upsaId));
    }

    @Override
    public Page<EmployeeDto> findAll(Pageable pageable) {
        final Page<Employee> employees = employeeService.findAll(pageable);

        return new PageImpl<>(employee2DtoConverter.convertAll(employees.getContent()));
    }
}
