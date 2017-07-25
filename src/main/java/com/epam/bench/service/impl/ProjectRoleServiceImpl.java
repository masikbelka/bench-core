package com.epam.bench.service.impl;

import com.epam.bench.service.ProjectRoleService;
import com.epam.bench.domain.ProjectRole;
import com.epam.bench.repository.ProjectRoleRepository;
import com.epam.bench.repository.search.ProjectRoleSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ProjectRole.
 */
@Service
@Transactional
public class ProjectRoleServiceImpl implements ProjectRoleService{

    private final Logger log = LoggerFactory.getLogger(ProjectRoleServiceImpl.class);

    private final ProjectRoleRepository projectRoleRepository;

    private final ProjectRoleSearchRepository projectRoleSearchRepository;

    public ProjectRoleServiceImpl(ProjectRoleRepository projectRoleRepository, ProjectRoleSearchRepository projectRoleSearchRepository) {
        this.projectRoleRepository = projectRoleRepository;
        this.projectRoleSearchRepository = projectRoleSearchRepository;
    }

    /**
     * Save a projectRole.
     *
     * @param projectRole the entity to save
     * @return the persisted entity
     */
    @Override
    public ProjectRole save(ProjectRole projectRole) {
        log.debug("Request to save ProjectRole : {}", projectRole);
        ProjectRole result = projectRoleRepository.save(projectRole);
        projectRoleSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the projectRoles.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectRole> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectRoles");
        return projectRoleRepository.findAll(pageable);
    }

    /**
     *  Get one projectRole by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ProjectRole findOne(Long id) {
        log.debug("Request to get ProjectRole : {}", id);
        return projectRoleRepository.findOne(id);
    }

    /**
     *  Delete the  projectRole by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProjectRole : {}", id);
        projectRoleRepository.delete(id);
        projectRoleSearchRepository.delete(id);
    }

    /**
     * Search for the projectRole corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectRole> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectRoles for query {}", query);
        Page<ProjectRole> result = projectRoleSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
