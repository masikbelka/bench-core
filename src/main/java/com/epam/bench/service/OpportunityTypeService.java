package com.epam.bench.service;

import com.epam.bench.domain.OpportunityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing OpportunityType.
 */
public interface OpportunityTypeService {

    /**
     * Save a opportunityType.
     *
     * @param opportunityType the entity to save
     * @return the persisted entity
     */
    OpportunityType save(OpportunityType opportunityType);

    /**
     *  Get all the opportunityTypes.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<OpportunityType> findAll(Pageable pageable);

    /**
     *  Get the "id" opportunityType.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    OpportunityType findOne(Long id);

    /**
     *  Delete the "id" opportunityType.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the opportunityType corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<OpportunityType> search(String query, Pageable pageable);
}
