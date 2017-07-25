package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.ProjectRole;
import com.epam.bench.service.ProjectRoleService;
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
 * REST controller for managing ProjectRole.
 */
@RestController
@RequestMapping("/api")
public class ProjectRoleResource {

    private final Logger log = LoggerFactory.getLogger(ProjectRoleResource.class);

    private static final String ENTITY_NAME = "projectRole";

    private final ProjectRoleService projectRoleService;

    public ProjectRoleResource(ProjectRoleService projectRoleService) {
        this.projectRoleService = projectRoleService;
    }

    /**
     * POST  /project-roles : Create a new projectRole.
     *
     * @param projectRole the projectRole to create
     * @return the ResponseEntity with status 201 (Created) and with body the new projectRole, or with status 400 (Bad Request) if the projectRole has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/project-roles")
    @Timed
    public ResponseEntity<ProjectRole> createProjectRole(@Valid @RequestBody ProjectRole projectRole) throws URISyntaxException {
        log.debug("REST request to save ProjectRole : {}", projectRole);
        if (projectRole.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new projectRole cannot already have an ID")).body(null);
        }
        ProjectRole result = projectRoleService.save(projectRole);
        return ResponseEntity.created(new URI("/api/project-roles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /project-roles : Updates an existing projectRole.
     *
     * @param projectRole the projectRole to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated projectRole,
     * or with status 400 (Bad Request) if the projectRole is not valid,
     * or with status 500 (Internal Server Error) if the projectRole couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/project-roles")
    @Timed
    public ResponseEntity<ProjectRole> updateProjectRole(@Valid @RequestBody ProjectRole projectRole) throws URISyntaxException {
        log.debug("REST request to update ProjectRole : {}", projectRole);
        if (projectRole.getId() == null) {
            return createProjectRole(projectRole);
        }
        ProjectRole result = projectRoleService.save(projectRole);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, projectRole.getId().toString()))
            .body(result);
    }

    /**
     * GET  /project-roles : get all the projectRoles.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of projectRoles in body
     */
    @GetMapping("/project-roles")
    @Timed
    public ResponseEntity<List<ProjectRole>> getAllProjectRoles(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of ProjectRoles");
        Page<ProjectRole> page = projectRoleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/project-roles");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /project-roles/:id : get the "id" projectRole.
     *
     * @param id the id of the projectRole to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the projectRole, or with status 404 (Not Found)
     */
    @GetMapping("/project-roles/{id}")
    @Timed
    public ResponseEntity<ProjectRole> getProjectRole(@PathVariable Long id) {
        log.debug("REST request to get ProjectRole : {}", id);
        ProjectRole projectRole = projectRoleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(projectRole));
    }

    /**
     * DELETE  /project-roles/:id : delete the "id" projectRole.
     *
     * @param id the id of the projectRole to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/project-roles/{id}")
    @Timed
    public ResponseEntity<Void> deleteProjectRole(@PathVariable Long id) {
        log.debug("REST request to delete ProjectRole : {}", id);
        projectRoleService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/project-roles?query=:query : search for the projectRole corresponding
     * to the query.
     *
     * @param query the query of the projectRole search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/project-roles")
    @Timed
    public ResponseEntity<List<ProjectRole>> searchProjectRoles(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of ProjectRoles for query {}", query);
        Page<ProjectRole> page = projectRoleService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/project-roles");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
