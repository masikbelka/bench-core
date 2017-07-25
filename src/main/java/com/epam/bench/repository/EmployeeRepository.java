package com.epam.bench.repository;

import com.epam.bench.domain.Employee;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    Employee findByUpsaId(String upsaId);
}
