package com.epam.bench.repository;

import com.epam.bench.domain.OpportunityPosition;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the OpportunityPosition entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OpportunityPositionRepository extends JpaRepository<OpportunityPosition,Long> {
    
}
