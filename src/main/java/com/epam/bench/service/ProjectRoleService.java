package com.epam.bench.service;

import com.epam.bench.domain.ProjectRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing ProjectRole.
 */
public interface ProjectRoleService {

    /**
     * Save a projectRole.
     *
     * @param projectRole the entity to save
     * @return the persisted entity
     */
    ProjectRole save(ProjectRole projectRole);

    /**
     *  Get all the projectRoles.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ProjectRole> findAll(Pageable pageable);

    /**
     *  Get the "id" projectRole.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ProjectRole findOne(Long id);

    /**
     *  Delete the "id" projectRole.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the projectRole corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ProjectRole> search(String query, Pageable pageable);
}
