package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.Unit;
import com.epam.bench.service.UnitService;
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
 * REST controller for managing Unit.
 */
@RestController
@RequestMapping("/api")
public class UnitResource {

    private final Logger log = LoggerFactory.getLogger(UnitResource.class);

    private static final String ENTITY_NAME = "unit";

    private final UnitService unitService;

    public UnitResource(UnitService unitService) {
        this.unitService = unitService;
    }

    /**
     * POST  /units : Create a new unit.
     *
     * @param unit the unit to create
     * @return the ResponseEntity with status 201 (Created) and with body the new unit, or with status 400 (Bad Request) if the unit has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/units")
    @Timed
    public ResponseEntity<Unit> createUnit(@Valid @RequestBody Unit unit) throws URISyntaxException {
        log.debug("REST request to save Unit : {}", unit);
        if (unit.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new unit cannot already have an ID")).body(null);
        }
        Unit result = unitService.save(unit);
        return ResponseEntity.created(new URI("/api/units/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /units : Updates an existing unit.
     *
     * @param unit the unit to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated unit,
     * or with status 400 (Bad Request) if the unit is not valid,
     * or with status 500 (Internal Server Error) if the unit couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/units")
    @Timed
    public ResponseEntity<Unit> updateUnit(@Valid @RequestBody Unit unit) throws URISyntaxException {
        log.debug("REST request to update Unit : {}", unit);
        if (unit.getId() == null) {
            return createUnit(unit);
        }
        Unit result = unitService.save(unit);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, unit.getId().toString()))
            .body(result);
    }

    /**
     * GET  /units : get all the units.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of units in body
     */
    @GetMapping("/units")
    @Timed
    public ResponseEntity<List<Unit>> getAllUnits(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Units");
        Page<Unit> page = unitService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/units");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /units/:id : get the "id" unit.
     *
     * @param id the id of the unit to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the unit, or with status 404 (Not Found)
     */
    @GetMapping("/units/{id}")
    @Timed
    public ResponseEntity<Unit> getUnit(@PathVariable Long id) {
        log.debug("REST request to get Unit : {}", id);
        Unit unit = unitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(unit));
    }

    /**
     * DELETE  /units/:id : delete the "id" unit.
     *
     * @param id the id of the unit to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/units/{id}")
    @Timed
    public ResponseEntity<Void> deleteUnit(@PathVariable Long id) {
        log.debug("REST request to delete Unit : {}", id);
        unitService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/units?query=:query : search for the unit corresponding
     * to the query.
     *
     * @param query the query of the unit search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/units")
    @Timed
    public ResponseEntity<List<Unit>> searchUnits(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Units for query {}", query);
        Page<Unit> page = unitService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/units");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
