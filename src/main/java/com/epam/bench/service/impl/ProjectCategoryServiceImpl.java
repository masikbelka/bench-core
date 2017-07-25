package com.epam.bench.service.impl;

import com.epam.bench.service.ProjectCategoryService;
import com.epam.bench.domain.ProjectCategory;
import com.epam.bench.repository.ProjectCategoryRepository;
import com.epam.bench.repository.search.ProjectCategorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ProjectCategory.
 */
@Service
@Transactional
public class ProjectCategoryServiceImpl implements ProjectCategoryService{

    private final Logger log = LoggerFactory.getLogger(ProjectCategoryServiceImpl.class);

    private final ProjectCategoryRepository projectCategoryRepository;

    private final ProjectCategorySearchRepository projectCategorySearchRepository;

    public ProjectCategoryServiceImpl(ProjectCategoryRepository projectCategoryRepository, ProjectCategorySearchRepository projectCategorySearchRepository) {
        this.projectCategoryRepository = projectCategoryRepository;
        this.projectCategorySearchRepository = projectCategorySearchRepository;
    }

    /**
     * Save a projectCategory.
     *
     * @param projectCategory the entity to save
     * @return the persisted entity
     */
    @Override
    public ProjectCategory save(ProjectCategory projectCategory) {
        log.debug("Request to save ProjectCategory : {}", projectCategory);
        ProjectCategory result = projectCategoryRepository.save(projectCategory);
        projectCategorySearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the projectCategories.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectCategory> findAll(Pageable pageable) {
        log.debug("Request to get all ProjectCategories");
        return projectCategoryRepository.findAll(pageable);
    }

    /**
     *  Get one projectCategory by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ProjectCategory findOne(Long id) {
        log.debug("Request to get ProjectCategory : {}", id);
        return projectCategoryRepository.findOne(id);
    }

    /**
     *  Delete the  projectCategory by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete ProjectCategory : {}", id);
        projectCategoryRepository.delete(id);
        projectCategorySearchRepository.delete(id);
    }

    /**
     * Search for the projectCategory corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProjectCategory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ProjectCategories for query {}", query);
        Page<ProjectCategory> result = projectCategorySearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
