package com.epam.bench.service;

import com.epam.bench.domain.BillingConcept;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing BillingConcept.
 */
public interface BillingConceptService {

    /**
     * Save a billingConcept.
     *
     * @param billingConcept the entity to save
     * @return the persisted entity
     */
    BillingConcept save(BillingConcept billingConcept);

    /**
     *  Get all the billingConcepts.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<BillingConcept> findAll(Pageable pageable);

    /**
     *  Get the "id" billingConcept.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    BillingConcept findOne(Long id);

    /**
     *  Delete the "id" billingConcept.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the billingConcept corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<BillingConcept> search(String query, Pageable pageable);
}
