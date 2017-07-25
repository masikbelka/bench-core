package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.JobFunction;
import com.epam.bench.service.JobFunctionService;
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
 * REST controller for managing JobFunction.
 */
@RestController
@RequestMapping("/api")
public class JobFunctionResource {

    private final Logger log = LoggerFactory.getLogger(JobFunctionResource.class);

    private static final String ENTITY_NAME = "jobFunction";

    private final JobFunctionService jobFunctionService;

    public JobFunctionResource(JobFunctionService jobFunctionService) {
        this.jobFunctionService = jobFunctionService;
    }

    /**
     * POST  /job-functions : Create a new jobFunction.
     *
     * @param jobFunction the jobFunction to create
     * @return the ResponseEntity with status 201 (Created) and with body the new jobFunction, or with status 400 (Bad Request) if the jobFunction has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/job-functions")
    @Timed
    public ResponseEntity<JobFunction> createJobFunction(@Valid @RequestBody JobFunction jobFunction) throws URISyntaxException {
        log.debug("REST request to save JobFunction : {}", jobFunction);
        if (jobFunction.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new jobFunction cannot already have an ID")).body(null);
        }
        JobFunction result = jobFunctionService.save(jobFunction);
        return ResponseEntity.created(new URI("/api/job-functions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /job-functions : Updates an existing jobFunction.
     *
     * @param jobFunction the jobFunction to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated jobFunction,
     * or with status 400 (Bad Request) if the jobFunction is not valid,
     * or with status 500 (Internal Server Error) if the jobFunction couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/job-functions")
    @Timed
    public ResponseEntity<JobFunction> updateJobFunction(@Valid @RequestBody JobFunction jobFunction) throws URISyntaxException {
        log.debug("REST request to update JobFunction : {}", jobFunction);
        if (jobFunction.getId() == null) {
            return createJobFunction(jobFunction);
        }
        JobFunction result = jobFunctionService.save(jobFunction);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, jobFunction.getId().toString()))
            .body(result);
    }

    /**
     * GET  /job-functions : get all the jobFunctions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of jobFunctions in body
     */
    @GetMapping("/job-functions")
    @Timed
    public ResponseEntity<List<JobFunction>> getAllJobFunctions(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of JobFunctions");
        Page<JobFunction> page = jobFunctionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/job-functions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /job-functions/:id : get the "id" jobFunction.
     *
     * @param id the id of the jobFunction to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the jobFunction, or with status 404 (Not Found)
     */
    @GetMapping("/job-functions/{id}")
    @Timed
    public ResponseEntity<JobFunction> getJobFunction(@PathVariable Long id) {
        log.debug("REST request to get JobFunction : {}", id);
        JobFunction jobFunction = jobFunctionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(jobFunction));
    }

    /**
     * DELETE  /job-functions/:id : delete the "id" jobFunction.
     *
     * @param id the id of the jobFunction to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/job-functions/{id}")
    @Timed
    public ResponseEntity<Void> deleteJobFunction(@PathVariable Long id) {
        log.debug("REST request to delete JobFunction : {}", id);
        jobFunctionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/job-functions?query=:query : search for the jobFunction corresponding
     * to the query.
     *
     * @param query the query of the jobFunction search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/job-functions")
    @Timed
    public ResponseEntity<List<JobFunction>> searchJobFunctions(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of JobFunctions for query {}", query);
        Page<JobFunction> page = jobFunctionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/job-functions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
