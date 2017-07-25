package com.epam.bench.repository;

import com.epam.bench.domain.OpportunityType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the OpportunityType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OpportunityTypeRepository extends JpaRepository<OpportunityType,Long> {
    
}
