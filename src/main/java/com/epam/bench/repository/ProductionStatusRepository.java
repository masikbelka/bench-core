package com.epam.bench.repository;

import com.epam.bench.domain.ProductionStatus;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ProductionStatus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductionStatusRepository extends JpaRepository<ProductionStatus,Long> {
    
}
