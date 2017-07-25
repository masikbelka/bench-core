package com.epam.bench.repository;

import com.epam.bench.domain.BillingType;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the BillingType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BillingTypeRepository extends JpaRepository<BillingType,Long> {
    
}
