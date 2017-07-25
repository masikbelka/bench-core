package com.epam.bench.repository;

import com.epam.bench.domain.BenchPredictions;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the BenchPredictions entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BenchPredictionsRepository extends JpaRepository<BenchPredictions,Long> {
    
}
