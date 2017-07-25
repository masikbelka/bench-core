package com.epam.bench.repository;

import com.epam.bench.domain.ProbationStatus;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ProbationStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProbationStatusRepository extends JpaRepository<ProbationStatus,Long> {
    
}
