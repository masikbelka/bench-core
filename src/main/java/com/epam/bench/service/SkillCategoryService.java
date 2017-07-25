package com.epam.bench.service;

import com.epam.bench.domain.SkillCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing SkillCategory.
 */
public interface SkillCategoryService {

    /**
     * Save a skillCategory.
     *
     * @param skillCategory the entity to save
     * @return the persisted entity
     */
    SkillCategory save(SkillCategory skillCategory);

    /**
     *  Get all the skillCategories.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<SkillCategory> findAll(Pageable pageable);

    /**
     *  Get the "id" skillCategory.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    SkillCategory findOne(Long id);

    /**
     *  Delete the "id" skillCategory.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the skillCategory corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<SkillCategory> search(String query, Pageable pageable);
}
