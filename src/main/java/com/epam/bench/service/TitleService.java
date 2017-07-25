package com.epam.bench.service;

import com.epam.bench.domain.Title;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Title.
 */
public interface TitleService {

    /**
     * Save a title.
     *
     * @param title the entity to save
     * @return the persisted entity
     */
    Title save(Title title);

    /**
     *  Get all the titles.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Title> findAll(Pageable pageable);

    /**
     *  Get the "id" title.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Title findOne(Long id);

    /**
     *  Delete the "id" title.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the title corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Title> search(String query, Pageable pageable);
}
