package com.epam.bench.facade.converter;

import com.epam.bench.domain.Employee;
import com.epam.bench.web.rest.dto.EmployeeDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Employee2DtoConverterImpl implements Converter<Employee, EmployeeDto>{

    @Override
    public EmployeeDto convert(Employee source) {
        final EmployeeDto dto = new EmployeeDto();
        return null;
    }

    @Override
    public List<EmployeeDto> convertAll(List<Employee> source) {
        final List<EmployeeDto> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(source)) {
            for (Employee employee : source) {
                result.add(convert(employee));
            }
        }
        return result;
    }
}
