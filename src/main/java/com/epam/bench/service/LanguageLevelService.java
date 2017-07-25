package com.epam.bench.service;

import com.epam.bench.domain.LanguageLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing LanguageLevel.
 */
public interface LanguageLevelService {

    /**
     * Save a languageLevel.
     *
     * @param languageLevel the entity to save
     * @return the persisted entity
     */
    LanguageLevel save(LanguageLevel languageLevel);

    /**
     *  Get all the languageLevels.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<LanguageLevel> findAll(Pageable pageable);

    /**
     *  Get the "id" languageLevel.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    LanguageLevel findOne(Long id);

    /**
     *  Delete the "id" languageLevel.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the languageLevel corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<LanguageLevel> search(String query, Pageable pageable);
}
