package com.epam.bench.repository;

import com.epam.bench.domain.BenchHistory;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the BenchHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BenchHistoryRepository extends JpaRepository<BenchHistory,Long> {
    
}
