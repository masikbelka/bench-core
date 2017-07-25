package com.epam.bench.repository;

import com.epam.bench.domain.BillingConcept;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the BillingConcept entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BillingConceptRepository extends JpaRepository<BillingConcept,Long> {
    
}
