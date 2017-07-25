package com.epam.bench.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.epam.bench.domain.LanguageLevel;
import com.epam.bench.service.LanguageLevelService;
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
 * REST controller for managing LanguageLevel.
 */
@RestController
@RequestMapping("/api")
public class LanguageLevelResource {

    private final Logger log = LoggerFactory.getLogger(LanguageLevelResource.class);

    private static final String ENTITY_NAME = "languageLevel";

    private final LanguageLevelService languageLevelService;

    public LanguageLevelResource(LanguageLevelService languageLevelService) {
        this.languageLevelService = languageLevelService;
    }

    /**
     * POST  /language-levels : Create a new languageLevel.
     *
     * @param languageLevel the languageLevel to create
     * @return the ResponseEntity with status 201 (Created) and with body the new languageLevel, or with status 400 (Bad Request) if the languageLevel has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/language-levels")
    @Timed
    public ResponseEntity<LanguageLevel> createLanguageLevel(@Valid @RequestBody LanguageLevel languageLevel) throws URISyntaxException {
        log.debug("REST request to save LanguageLevel : {}", languageLevel);
        if (languageLevel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new languageLevel cannot already have an ID")).body(null);
        }
        LanguageLevel result = languageLevelService.save(languageLevel);
        return ResponseEntity.created(new URI("/api/language-levels/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /language-levels : Updates an existing languageLevel.
     *
     * @param languageLevel the languageLevel to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated languageLevel,
     * or with status 400 (Bad Request) if the languageLevel is not valid,
     * or with status 500 (Internal Server Error) if the languageLevel couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/language-levels")
    @Timed
    public ResponseEntity<LanguageLevel> updateLanguageLevel(@Valid @RequestBody LanguageLevel languageLevel) throws URISyntaxException {
        log.debug("REST request to update LanguageLevel : {}", languageLevel);
        if (languageLevel.getId() == null) {
            return createLanguageLevel(languageLevel);
        }
        LanguageLevel result = languageLevelService.save(languageLevel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, languageLevel.getId().toString()))
            .body(result);
    }

    /**
     * GET  /language-levels : get all the languageLevels.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of languageLevels in body
     */
    @GetMapping("/language-levels")
    @Timed
    public ResponseEntity<List<LanguageLevel>> getAllLanguageLevels(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of LanguageLevels");
        Page<LanguageLevel> page = languageLevelService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/language-levels");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /language-levels/:id : get the "id" languageLevel.
     *
     * @param id the id of the languageLevel to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the languageLevel, or with status 404 (Not Found)
     */
    @GetMapping("/language-levels/{id}")
    @Timed
    public ResponseEntity<LanguageLevel> getLanguageLevel(@PathVariable Long id) {
        log.debug("REST request to get LanguageLevel : {}", id);
        LanguageLevel languageLevel = languageLevelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(languageLevel));
    }

    /**
     * DELETE  /language-levels/:id : delete the "id" languageLevel.
     *
     * @param id the id of the languageLevel to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/language-levels/{id}")
    @Timed
    public ResponseEntity<Void> deleteLanguageLevel(@PathVariable Long id) {
        log.debug("REST request to delete LanguageLevel : {}", id);
        languageLevelService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/language-levels?query=:query : search for the languageLevel corresponding
     * to the query.
     *
     * @param query the query of the languageLevel search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/language-levels")
    @Timed
    public ResponseEntity<List<LanguageLevel>> searchLanguageLevels(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of LanguageLevels for query {}", query);
        Page<LanguageLevel> page = languageLevelService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/language-levels");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
