package com.epam.bench.service;

import com.epam.bench.domain.JobFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing JobFunction.
 */
public interface JobFunctionService {

    /**
     * Save a jobFunction.
     *
     * @param jobFunction the entity to save
     * @return the persisted entity
     */
    JobFunction save(JobFunction jobFunction);

    /**
     *  Get all the jobFunctions.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<JobFunction> findAll(Pageable pageable);

    /**
     *  Get the "id" jobFunction.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    JobFunction findOne(Long id);

    /**
     *  Delete the "id" jobFunction.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the jobFunction corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<JobFunction> search(String query, Pageable pageable);
}
