package com.epam.bench.service;

import com.epam.bench.domain.BenchCommentHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing BenchCommentHistory.
 */
public interface BenchCommentHistoryService {

    /**
     * Save a benchCommentHistory.
     *
     * @param benchCommentHistory the entity to save
     * @return the persisted entity
     */
    BenchCommentHistory save(BenchCommentHistory benchCommentHistory);

    /**
     *  Get all the benchCommentHistories.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<BenchCommentHistory> findAll(Pageable pageable);

    /**
     *  Get the "id" benchCommentHistory.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    BenchCommentHistory findOne(Long id);

    /**
     *  Delete the "id" benchCommentHistory.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the benchCommentHistory corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<BenchCommentHistory> search(String query, Pageable pageable);
}
