package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.ProductionStatus;
import com.epam.bench.service.ProductionStatusService;
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
 * REST controller for managing ProductionStatus.
 */
@RestController
@RequestMapping("/api")
public class ProductionStatusResource {

    private final Logger log = LoggerFactory.getLogger(ProductionStatusResource.class);

    private static final String ENTITY_NAME = "productionStatus";

    private final ProductionStatusService productionStatusService;

    public ProductionStatusResource(ProductionStatusService productionStatusService) {
        this.productionStatusService = productionStatusService;
    }

    /**
     * POST  /production-statuses : Create a new productionStatus.
     *
     * @param productionStatus the productionStatus to create
     * @return the ResponseEntity with status 201 (Created) and with body the new productionStatus, or with status 400 (Bad Request) if the productionStatus has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/production-statuses")
    @Timed
    public ResponseEntity<ProductionStatus> createProductionStatus(@Valid @RequestBody ProductionStatus productionStatus) throws URISyntaxException {
        log.debug("REST request to save ProductionStatus : {}", productionStatus);
        if (productionStatus.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new productionStatus cannot already have an ID")).body(null);
        }
        ProductionStatus result = productionStatusService.save(productionStatus);
        return ResponseEntity.created(new URI("/api/production-statuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /production-statuses : Updates an existing productionStatus.
     *
     * @param productionStatus the productionStatus to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated productionStatus,
     * or with status 400 (Bad Request) if the productionStatus is not valid,
     * or with status 500 (Internal Server Error) if the productionStatus couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/production-statuses")
    @Timed
    public ResponseEntity<ProductionStatus> updateProductionStatus(@Valid @RequestBody ProductionStatus productionStatus) throws URISyntaxException {
        log.debug("REST request to update ProductionStatus : {}", productionStatus);
        if (productionStatus.getId() == null) {
            return createProductionStatus(productionStatus);
        }
        ProductionStatus result = productionStatusService.save(productionStatus);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, productionStatus.getId().toString()))
            .body(result);
    }

    /**
     * GET  /production-statuses : get all the productionStatuses.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of productionStatuses in body
     */
    @GetMapping("/production-statuses")
    @Timed
    public ResponseEntity<List<ProductionStatus>> getAllProductionStatuses(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of ProductionStatuses");
        Page<ProductionStatus> page = productionStatusService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/production-statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /production-statuses/:id : get the "id" productionStatus.
     *
     * @param id the id of the productionStatus to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the productionStatus, or with status 404 (Not Found)
     */
    @GetMapping("/production-statuses/{id}")
    @Timed
    public ResponseEntity<ProductionStatus> getProductionStatus(@PathVariable Long id) {
        log.debug("REST request to get ProductionStatus : {}", id);
        ProductionStatus productionStatus = productionStatusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(productionStatus));
    }

    /**
     * DELETE  /production-statuses/:id : delete the "id" productionStatus.
     *
     * @param id the id of the productionStatus to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/production-statuses/{id}")
    @Timed
    public ResponseEntity<Void> deleteProductionStatus(@PathVariable Long id) {
        log.debug("REST request to delete ProductionStatus : {}", id);
        productionStatusService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/production-statuses?query=:query : search for the productionStatus corresponding
     * to the query.
     *
     * @param query the query of the productionStatus search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/production-statuses")
    @Timed
    public ResponseEntity<List<ProductionStatus>> searchProductionStatuses(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of ProductionStatuses for query {}", query);
        Page<ProductionStatus> page = productionStatusService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/production-statuses");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
