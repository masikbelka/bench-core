package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.ProjectCategory;
import com.epam.bench.service.ProjectCategoryService;
import com.epam.bench.web.rest.util.HeaderUtil;
import com.epam.bench.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ProjectCategory.
 */
@RestController
@RequestMapping("/api")
public class ProjectCategoryResource {

    private final Logger log = LoggerFactory.getLogger(ProjectCategoryResource.class);

    private static final String ENTITY_NAME = "projectCategory";

    private final ProjectCategoryService projectCategoryService;

    public ProjectCategoryResource(ProjectCategoryService projectCategoryService) {
        this.projectCategoryService = projectCategoryService;
    }

    /**
     * POST  /project-categories : Create a new projectCategory.
     *
     * @param projectCategory the projectCategory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectCategory, or with status 400 (Bad Request) if the projectCategory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/project-categories")
    @Timed
    public ResponseEntity<ProjectCategory> createProjectCategory(@Valid @RequestBody ProjectCategory projectCategory) throws URISyntaxException {
        log.debug("REST request to save ProjectCategory : {}", projectCategory);
        if (projectCategory.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new projectCategory cannot already have an ID")).body(null);
        }
        ProjectCategory result = projectCategoryService.save(projectCategory);
        return ResponseEntity.created(new URI("/api/project-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /project-categories : Updates an existing projectCategory.
     *
     * @param projectCategory the projectCategory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectCategory,
     * or with status 400 (Bad Request) if the projectCategory is not valid,
     * or with status 500 (Internal Server Error) if the projectCategory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-categories")
    @Timed
    public ResponseEntity<ProjectCategory> updateProjectCategory(@Valid @RequestBody ProjectCategory projectCategory) throws URISyntaxException {
        log.debug("REST request to update ProjectCategory : {}", projectCategory);
        if (projectCategory.getId() == null) {
            return createProjectCategory(projectCategory);
        }
        ProjectCategory result = projectCategoryService.save(projectCategory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, projectCategory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /project-categories : get all the projectCategories.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectCategories in body
     */
    @GetMapping("/project-categories")
    @Timed
    public ResponseEntity<List<ProjectCategory>> getAllProjectCategories(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of ProjectCategories");
        Page<ProjectCategory> page = projectCategoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-categories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project-categories/:id : get the "id" projectCategory.
     *
     * @param id the id of the projectCategory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectCategory, or with status 404 (Not Found)
     */
    @GetMapping("/project-categories/{id}")
    @Timed
    public ResponseEntity<ProjectCategory> getProjectCategory(@PathVariable Long id) {
        log.debug("REST request to get ProjectCategory : {}", id);
        ProjectCategory projectCategory = projectCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(projectCategory));
    }

    /**
     * DELETE  /project-categories/:id : delete the "id" projectCategory.
     *
     * @param id the id of the projectCategory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-categories/{id}")
    @Timed
    public ResponseEntity<Void> deleteProjectCategory(@PathVariable Long id) {
        log.debug("REST request to delete ProjectCategory : {}", id);
        projectCategoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/project-categories?query=:query : search for the projectCategory corresponding
     * to the query.
     *
     * @param query the query of the projectCategory search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/project-categories")
    @Timed
    public ResponseEntity<List<ProjectCategory>> searchProjectCategories(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of ProjectCategories for query {}", query);
        Page<ProjectCategory> page = projectCategoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/project-categories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
