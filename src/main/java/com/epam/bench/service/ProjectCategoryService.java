package com.epam.bench.service;

import com.epam.bench.domain.ProjectCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing ProjectCategory.
 */
public interface ProjectCategoryService {

    /**
     * Save a projectCategory.
     *
     * @param projectCategory the entity to save
     * @return the persisted entity
     */
    ProjectCategory save(ProjectCategory projectCategory);

    /**
     *  Get all the projectCategories.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ProjectCategory> findAll(Pageable pageable);

    /**
     *  Get the "id" projectCategory.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ProjectCategory findOne(Long id);

    /**
     *  Delete the "id" projectCategory.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the projectCategory corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ProjectCategory> search(String query, Pageable pageable);
}
