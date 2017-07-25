package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.PredictionDetails;
import com.epam.bench.service.PredictionDetailsService;
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
 * REST controller for managing PredictionDetails.
 */
@RestController
@RequestMapping("/api")
public class PredictionDetailsResource {

    private final Logger log = LoggerFactory.getLogger(PredictionDetailsResource.class);

    private static final String ENTITY_NAME = "predictionDetails";

    private final PredictionDetailsService predictionDetailsService;

    public PredictionDetailsResource(PredictionDetailsService predictionDetailsService) {
        this.predictionDetailsService = predictionDetailsService;
    }

    /**
     * POST  /prediction-details : Create a new predictionDetails.
     *
     * @param predictionDetails the predictionDetails to create
     * @return the ResponseEntity with status 201 (Created) and with body the new predictionDetails, or with status 400 (Bad Request) if the predictionDetails has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/prediction-details")
    @Timed
    public ResponseEntity<PredictionDetails> createPredictionDetails(@Valid @RequestBody PredictionDetails predictionDetails) throws URISyntaxException {
        log.debug("REST request to save PredictionDetails : {}", predictionDetails);
        if (predictionDetails.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new predictionDetails cannot already have an ID")).body(null);
        }
        PredictionDetails result = predictionDetailsService.save(predictionDetails);
        return ResponseEntity.created(new URI("/api/prediction-details/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /prediction-details : Updates an existing predictionDetails.
     *
     * @param predictionDetails the predictionDetails to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated predictionDetails,
     * or with status 400 (Bad Request) if the predictionDetails is not valid,
     * or with status 500 (Internal Server Error) if the predictionDetails couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/prediction-details")
    @Timed
    public ResponseEntity<PredictionDetails> updatePredictionDetails(@Valid @RequestBody PredictionDetails predictionDetails) throws URISyntaxException {
        log.debug("REST request to update PredictionDetails : {}", predictionDetails);
        if (predictionDetails.getId() == null) {
            return createPredictionDetails(predictionDetails);
        }
        PredictionDetails result = predictionDetailsService.save(predictionDetails);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, predictionDetails.getId().toString()))
            .body(result);
    }

    /**
     * GET  /prediction-details : get all the predictionDetails.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of predictionDetails in body
     */
    @GetMapping("/prediction-details")
    @Timed
    public ResponseEntity<List<PredictionDetails>> getAllPredictionDetails(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of PredictionDetails");
        Page<PredictionDetails> page = predictionDetailsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/prediction-details");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /prediction-details/:id : get the "id" predictionDetails.
     *
     * @param id the id of the predictionDetails to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the predictionDetails, or with status 404 (Not Found)
     */
    @GetMapping("/prediction-details/{id}")
    @Timed
    public ResponseEntity<PredictionDetails> getPredictionDetails(@PathVariable Long id) {
        log.debug("REST request to get PredictionDetails : {}", id);
        PredictionDetails predictionDetails = predictionDetailsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(predictionDetails));
    }

    /**
     * DELETE  /prediction-details/:id : delete the "id" predictionDetails.
     *
     * @param id the id of the predictionDetails to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/prediction-details/{id}")
    @Timed
    public ResponseEntity<Void> deletePredictionDetails(@PathVariable Long id) {
        log.debug("REST request to delete PredictionDetails : {}", id);
        predictionDetailsService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/prediction-details?query=:query : search for the predictionDetails corresponding
     * to the query.
     *
     * @param query the query of the predictionDetails search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/prediction-details")
    @Timed
    public ResponseEntity<List<PredictionDetails>> searchPredictionDetails(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of PredictionDetails for query {}", query);
        Page<PredictionDetails> page = predictionDetailsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/prediction-details");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
