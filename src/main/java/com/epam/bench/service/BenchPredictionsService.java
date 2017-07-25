package com.epam.bench.service;

import com.epam.bench.domain.BenchPredictions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing BenchPredictions.
 */
public interface BenchPredictionsService {

    /**
     * Save a benchPredictions.
     *
     * @param benchPredictions the entity to save
     * @return the persisted entity
     */
    BenchPredictions save(BenchPredictions benchPredictions);

    /**
     *  Get all the benchPredictions.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<BenchPredictions> findAll(Pageable pageable);

    /**
     *  Get the "id" benchPredictions.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    BenchPredictions findOne(Long id);

    /**
     *  Delete the "id" benchPredictions.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the benchPredictions corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<BenchPredictions> search(String query, Pageable pageable);
}
