package com.epam.bench.repository;

import com.epam.bench.domain.JobExecution;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the JobExecution entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecution,Long> {
    
}
