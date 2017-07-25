package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.JobExecution;
import com.epam.bench.service.JobExecutionService;
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

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing JobExecution.
 */
@RestController
@RequestMapping("/api")
public class JobExecutionResource {

    private final Logger log = LoggerFactory.getLogger(JobExecutionResource.class);

    private static final String ENTITY_NAME = "jobExecution";

    private final JobExecutionService jobExecutionService;

    public JobExecutionResource(JobExecutionService jobExecutionService) {
        this.jobExecutionService = jobExecutionService;
    }

    /**
     * POST  /job-executions : Create a new jobExecution.
     *
     * @param jobExecution the jobExecution to create
     * @return the ResponseEntity with status 201 (Created) and with body the new jobExecution, or with status 400 (Bad Request) if the jobExecution has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/job-executions")
    @Timed
    public ResponseEntity<JobExecution> createJobExecution(@RequestBody JobExecution jobExecution) throws URISyntaxException {
        log.debug("REST request to save JobExecution : {}", jobExecution);
        if (jobExecution.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new jobExecution cannot already have an ID")).body(null);
        }
        JobExecution result = jobExecutionService.save(jobExecution);
        return ResponseEntity.created(new URI("/api/job-executions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /job-executions : Updates an existing jobExecution.
     *
     * @param jobExecution the jobExecution to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated jobExecution,
     * or with status 400 (Bad Request) if the jobExecution is not valid,
     * or with status 500 (Internal Server Error) if the jobExecution couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/job-executions")
    @Timed
    public ResponseEntity<JobExecution> updateJobExecution(@RequestBody JobExecution jobExecution) throws URISyntaxException {
        log.debug("REST request to update JobExecution : {}", jobExecution);
        if (jobExecution.getId() == null) {
            return createJobExecution(jobExecution);
        }
        JobExecution result = jobExecutionService.save(jobExecution);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, jobExecution.getId().toString()))
            .body(result);
    }

    /**
     * GET  /job-executions : get all the jobExecutions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of jobExecutions in body
     */
    @GetMapping("/job-executions")
    @Timed
    public ResponseEntity<List<JobExecution>> getAllJobExecutions(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of JobExecutions");
        Page<JobExecution> page = jobExecutionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/job-executions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /job-executions/:id : get the "id" jobExecution.
     *
     * @param id the id of the jobExecution to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the jobExecution, or with status 404 (Not Found)
     */
    @GetMapping("/job-executions/{id}")
    @Timed
    public ResponseEntity<JobExecution> getJobExecution(@PathVariable Long id) {
        log.debug("REST request to get JobExecution : {}", id);
        JobExecution jobExecution = jobExecutionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(jobExecution));
    }

    /**
     * DELETE  /job-executions/:id : delete the "id" jobExecution.
     *
     * @param id the id of the jobExecution to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/job-executions/{id}")
    @Timed
    public ResponseEntity<Void> deleteJobExecution(@PathVariable Long id) {
        log.debug("REST request to delete JobExecution : {}", id);
        jobExecutionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/job-executions?query=:query : search for the jobExecution corresponding
     * to the query.
     *
     * @param query the query of the jobExecution search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/job-executions")
    @Timed
    public ResponseEntity<List<JobExecution>> searchJobExecutions(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of JobExecutions for query {}", query);
        Page<JobExecution> page = jobExecutionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/job-executions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
