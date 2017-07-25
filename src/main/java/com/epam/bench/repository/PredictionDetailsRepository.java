package com.epam.bench.repository;

import com.epam.bench.domain.PredictionDetails;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the PredictionDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PredictionDetailsRepository extends JpaRepository<PredictionDetails,Long> {
    
}
