package com.epam.bench.repository;

import com.epam.bench.domain.JobFunction;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the JobFunction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JobFunctionRepository extends JpaRepository<JobFunction,Long> {
    
}
