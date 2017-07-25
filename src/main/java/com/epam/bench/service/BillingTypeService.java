package com.epam.bench.service;

import com.epam.bench.domain.BillingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing BillingType.
 */
public interface BillingTypeService {

    /**
     * Save a billingType.
     *
     * @param billingType the entity to save
     * @return the persisted entity
     */
    BillingType save(BillingType billingType);

    /**
     *  Get all the billingTypes.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<BillingType> findAll(Pageable pageable);

    /**
     *  Get the "id" billingType.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    BillingType findOne(Long id);

    /**
     *  Delete the "id" billingType.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the billingType corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<BillingType> search(String query, Pageable pageable);
}
