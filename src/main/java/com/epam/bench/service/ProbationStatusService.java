package com.epam.bench.service;

import com.epam.bench.domain.ProbationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing ProbationStatus.
 */
public interface ProbationStatusService {

    /**
     * Save a probationStatus.
     *
     * @param probationStatus the entity to save
     * @return the persisted entity
     */
    ProbationStatus save(ProbationStatus probationStatus);

    /**
     *  Get all the probationStatuses.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ProbationStatus> findAll(Pageable pageable);

    /**
     *  Get the "id" probationStatus.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ProbationStatus findOne(Long id);

    /**
     *  Delete the "id" probationStatus.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the probationStatus corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ProbationStatus> search(String query, Pageable pageable);
}
