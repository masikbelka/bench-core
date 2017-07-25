package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.ProjectHistory;
import com.epam.bench.service.ProjectHistoryService;
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
 * REST controller for managing ProjectHistory.
 */
@RestController
@RequestMapping("/api")
public class ProjectHistoryResource {

    private final Logger log = LoggerFactory.getLogger(ProjectHistoryResource.class);

    private static final String ENTITY_NAME = "projectHistory";

    private final ProjectHistoryService projectHistoryService;

    public ProjectHistoryResource(ProjectHistoryService projectHistoryService) {
        this.projectHistoryService = projectHistoryService;
    }

    /**
     * POST  /project-histories : Create a new projectHistory.
     *
     * @param projectHistory the projectHistory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectHistory, or with status 400 (Bad Request) if the projectHistory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/project-histories")
    @Timed
    public ResponseEntity<ProjectHistory> createProjectHistory(@Valid @RequestBody ProjectHistory projectHistory) throws URISyntaxException {
        log.debug("REST request to save ProjectHistory : {}", projectHistory);
        if (projectHistory.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new projectHistory cannot already have an ID")).body(null);
        }
        ProjectHistory result = projectHistoryService.save(projectHistory);
        return ResponseEntity.created(new URI("/api/project-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /project-histories : Updates an existing projectHistory.
     *
     * @param projectHistory the projectHistory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectHistory,
     * or with status 400 (Bad Request) if the projectHistory is not valid,
     * or with status 500 (Internal Server Error) if the projectHistory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-histories")
    @Timed
    public ResponseEntity<ProjectHistory> updateProjectHistory(@Valid @RequestBody ProjectHistory projectHistory) throws URISyntaxException {
        log.debug("REST request to update ProjectHistory : {}", projectHistory);
        if (projectHistory.getId() == null) {
            return createProjectHistory(projectHistory);
        }
        ProjectHistory result = projectHistoryService.save(projectHistory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, projectHistory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /project-histories : get all the projectHistories.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectHistories in body
     */
    @GetMapping("/project-histories")
    @Timed
    public ResponseEntity<List<ProjectHistory>> getAllProjectHistories(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of ProjectHistories");
        Page<ProjectHistory> page = projectHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project-histories/:id : get the "id" projectHistory.
     *
     * @param id the id of the projectHistory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectHistory, or with status 404 (Not Found)
     */
    @GetMapping("/project-histories/{id}")
    @Timed
    public ResponseEntity<ProjectHistory> getProjectHistory(@PathVariable Long id) {
        log.debug("REST request to get ProjectHistory : {}", id);
        ProjectHistory projectHistory = projectHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(projectHistory));
    }

    /**
     * DELETE  /project-histories/:id : delete the "id" projectHistory.
     *
     * @param id the id of the projectHistory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-histories/{id}")
    @Timed
    public ResponseEntity<Void> deleteProjectHistory(@PathVariable Long id) {
        log.debug("REST request to delete ProjectHistory : {}", id);
        projectHistoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/project-histories?query=:query : search for the projectHistory corresponding
     * to the query.
     *
     * @param query the query of the projectHistory search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/project-histories")
    @Timed
    public ResponseEntity<List<ProjectHistory>> searchProjectHistories(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of ProjectHistories for query {}", query);
        Page<ProjectHistory> page = projectHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/project-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
