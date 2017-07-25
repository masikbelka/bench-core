package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.Opportunity;
import com.epam.bench.service.OpportunityService;
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
 * REST controller for managing Opportunity.
 */
@RestController
@RequestMapping("/api")
public class OpportunityResource {

    private final Logger log = LoggerFactory.getLogger(OpportunityResource.class);

    private static final String ENTITY_NAME = "opportunity";

    private final OpportunityService opportunityService;

    public OpportunityResource(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    /**
     * POST  /opportunities : Create a new opportunity.
     *
     * @param opportunity the opportunity to create
     * @return the ResponseEntity with status 201 (Created) and with body the new opportunity, or with status 400 (Bad Request) if the opportunity has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/opportunities")
    @Timed
    public ResponseEntity<Opportunity> createOpportunity(@Valid @RequestBody Opportunity opportunity) throws URISyntaxException {
        log.debug("REST request to save Opportunity : {}", opportunity);
        if (opportunity.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new opportunity cannot already have an ID")).body(null);
        }
        Opportunity result = opportunityService.save(opportunity);
        return ResponseEntity.created(new URI("/api/opportunities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /opportunities : Updates an existing opportunity.
     *
     * @param opportunity the opportunity to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated opportunity,
     * or with status 400 (Bad Request) if the opportunity is not valid,
     * or with status 500 (Internal Server Error) if the opportunity couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/opportunities")
    @Timed
    public ResponseEntity<Opportunity> updateOpportunity(@Valid @RequestBody Opportunity opportunity) throws URISyntaxException {
        log.debug("REST request to update Opportunity : {}", opportunity);
        if (opportunity.getId() == null) {
            return createOpportunity(opportunity);
        }
        Opportunity result = opportunityService.save(opportunity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, opportunity.getId().toString()))
            .body(result);
    }

    /**
     * GET  /opportunities : get all the opportunities.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of opportunities in body
     */
    @GetMapping("/opportunities")
    @Timed
    public ResponseEntity<List<Opportunity>> getAllOpportunities(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Opportunities");
        Page<Opportunity> page = opportunityService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/opportunities");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /opportunities/:id : get the "id" opportunity.
     *
     * @param id the id of the opportunity to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the opportunity, or with status 404 (Not Found)
     */
    @GetMapping("/opportunities/{id}")
    @Timed
    public ResponseEntity<Opportunity> getOpportunity(@PathVariable Long id) {
        log.debug("REST request to get Opportunity : {}", id);
        Opportunity opportunity = opportunityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(opportunity));
    }

    /**
     * DELETE  /opportunities/:id : delete the "id" opportunity.
     *
     * @param id the id of the opportunity to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/opportunities/{id}")
    @Timed
    public ResponseEntity<Void> deleteOpportunity(@PathVariable Long id) {
        log.debug("REST request to delete Opportunity : {}", id);
        opportunityService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/opportunities?query=:query : search for the opportunity corresponding
     * to the query.
     *
     * @param query the query of the opportunity search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/opportunities")
    @Timed
    public ResponseEntity<List<Opportunity>> searchOpportunities(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Opportunities for query {}", query);
        Page<Opportunity> page = opportunityService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/opportunities");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
