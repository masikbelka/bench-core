package com.epam.bench.service;

import com.epam.bench.domain.OpportunityPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing OpportunityPosition.
 */
public interface OpportunityPositionService {

    /**
     * Save a opportunityPosition.
     *
     * @param opportunityPosition the entity to save
     * @return the persisted entity
     */
    OpportunityPosition save(OpportunityPosition opportunityPosition);

    /**
     *  Get all the opportunityPositions.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<OpportunityPosition> findAll(Pageable pageable);

    /**
     *  Get the "id" opportunityPosition.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    OpportunityPosition findOne(Long id);

    /**
     *  Delete the "id" opportunityPosition.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the opportunityPosition corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<OpportunityPosition> search(String query, Pageable pageable);
}
