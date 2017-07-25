package com.epam.bench.service;

import com.epam.bench.domain.PrimarySkill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing PrimarySkill.
 */
public interface PrimarySkillService {

    /**
     * Save a primarySkill.
     *
     * @param primarySkill the entity to save
     * @return the persisted entity
     */
    PrimarySkill save(PrimarySkill primarySkill);

    /**
     *  Get all the primarySkills.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<PrimarySkill> findAll(Pageable pageable);

    /**
     *  Get the "id" primarySkill.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    PrimarySkill findOne(Long id);

    /**
     *  Delete the "id" primarySkill.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the primarySkill corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<PrimarySkill> search(String query, Pageable pageable);
}
