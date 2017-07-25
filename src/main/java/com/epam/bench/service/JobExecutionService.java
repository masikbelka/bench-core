package com.epam.bench.service;

import com.epam.bench.domain.JobExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing JobExecution.
 */
public interface JobExecutionService {

    /**
     * Save a jobExecution.
     *
     * @param jobExecution the entity to save
     * @return the persisted entity
     */
    JobExecution save(JobExecution jobExecution);

    /**
     *  Get all the jobExecutions.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<JobExecution> findAll(Pageable pageable);

    /**
     *  Get the "id" jobExecution.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    JobExecution findOne(Long id);

    /**
     *  Delete the "id" jobExecution.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the jobExecution corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<JobExecution> search(String query, Pageable pageable);
}
