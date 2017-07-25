package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.OpportunityType;
import com.epam.bench.service.OpportunityTypeService;
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
 * REST controller for managing OpportunityType.
 */
@RestController
@RequestMapping("/api")
public class OpportunityTypeResource {

    private final Logger log = LoggerFactory.getLogger(OpportunityTypeResource.class);

    private static final String ENTITY_NAME = "opportunityType";

    private final OpportunityTypeService opportunityTypeService;

    public OpportunityTypeResource(OpportunityTypeService opportunityTypeService) {
        this.opportunityTypeService = opportunityTypeService;
    }

    /**
     * POST  /opportunity-types : Create a new opportunityType.
     *
     * @param opportunityType the opportunityType to create
     * @return the ResponseEntity with status 201 (Created) and with body the new opportunityType, or with status 400 (Bad Request) if the opportunityType has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/opportunity-types")
    @Timed
    public ResponseEntity<OpportunityType> createOpportunityType(@Valid @RequestBody OpportunityType opportunityType) throws URISyntaxException {
        log.debug("REST request to save OpportunityType : {}", opportunityType);
        if (opportunityType.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new opportunityType cannot already have an ID")).body(null);
        }
        OpportunityType result = opportunityTypeService.save(opportunityType);
        return ResponseEntity.created(new URI("/api/opportunity-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /opportunity-types : Updates an existing opportunityType.
     *
     * @param opportunityType the opportunityType to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated opportunityType,
     * or with status 400 (Bad Request) if the opportunityType is not valid,
     * or with status 500 (Internal Server Error) if the opportunityType couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/opportunity-types")
    @Timed
    public ResponseEntity<OpportunityType> updateOpportunityType(@Valid @RequestBody OpportunityType opportunityType) throws URISyntaxException {
        log.debug("REST request to update OpportunityType : {}", opportunityType);
        if (opportunityType.getId() == null) {
            return createOpportunityType(opportunityType);
        }
        OpportunityType result = opportunityTypeService.save(opportunityType);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, opportunityType.getId().toString()))
            .body(result);
    }

    /**
     * GET  /opportunity-types : get all the opportunityTypes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of opportunityTypes in body
     */
    @GetMapping("/opportunity-types")
    @Timed
    public ResponseEntity<List<OpportunityType>> getAllOpportunityTypes(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of OpportunityTypes");
        Page<OpportunityType> page = opportunityTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/opportunity-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /opportunity-types/:id : get the "id" opportunityType.
     *
     * @param id the id of the opportunityType to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the opportunityType, or with status 404 (Not Found)
     */
    @GetMapping("/opportunity-types/{id}")
    @Timed
    public ResponseEntity<OpportunityType> getOpportunityType(@PathVariable Long id) {
        log.debug("REST request to get OpportunityType : {}", id);
        OpportunityType opportunityType = opportunityTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(opportunityType));
    }

    /**
     * DELETE  /opportunity-types/:id : delete the "id" opportunityType.
     *
     * @param id the id of the opportunityType to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/opportunity-types/{id}")
    @Timed
    public ResponseEntity<Void> deleteOpportunityType(@PathVariable Long id) {
        log.debug("REST request to delete OpportunityType : {}", id);
        opportunityTypeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/opportunity-types?query=:query : search for the opportunityType corresponding
     * to the query.
     *
     * @param query the query of the opportunityType search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/opportunity-types")
    @Timed
    public ResponseEntity<List<OpportunityType>> searchOpportunityTypes(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of OpportunityTypes for query {}", query);
        Page<OpportunityType> page = opportunityTypeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/opportunity-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
