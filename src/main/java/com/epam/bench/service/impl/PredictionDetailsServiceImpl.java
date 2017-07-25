package com.epam.bench.service.impl;

import com.epam.bench.service.PredictionDetailsService;
import com.epam.bench.domain.PredictionDetails;
import com.epam.bench.repository.PredictionDetailsRepository;
import com.epam.bench.repository.search.PredictionDetailsSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing PredictionDetails.
 */
@Service
@Transactional
public class PredictionDetailsServiceImpl implements PredictionDetailsService{

    private final Logger log = LoggerFactory.getLogger(PredictionDetailsServiceImpl.class);

    private final PredictionDetailsRepository predictionDetailsRepository;

    private final PredictionDetailsSearchRepository predictionDetailsSearchRepository;

    public PredictionDetailsServiceImpl(PredictionDetailsRepository predictionDetailsRepository, PredictionDetailsSearchRepository predictionDetailsSearchRepository) {
        this.predictionDetailsRepository = predictionDetailsRepository;
        this.predictionDetailsSearchRepository = predictionDetailsSearchRepository;
    }

    /**
     * Save a predictionDetails.
     *
     * @param predictionDetails the entity to save
     * @return the persisted entity
     */
    @Override
    public PredictionDetails save(PredictionDetails predictionDetails) {
        log.debug("Request to save PredictionDetails : {}", predictionDetails);
        PredictionDetails result = predictionDetailsRepository.save(predictionDetails);
        predictionDetailsSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the predictionDetails.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PredictionDetails> findAll(Pageable pageable) {
        log.debug("Request to get all PredictionDetails");
        return predictionDetailsRepository.findAll(pageable);
    }

    /**
     *  Get one predictionDetails by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public PredictionDetails findOne(Long id) {
        log.debug("Request to get PredictionDetails : {}", id);
        return predictionDetailsRepository.findOne(id);
    }

    /**
     *  Delete the  predictionDetails by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete PredictionDetails : {}", id);
        predictionDetailsRepository.delete(id);
        predictionDetailsSearchRepository.delete(id);
    }

    /**
     * Search for the predictionDetails corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PredictionDetails> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PredictionDetails for query {}", query);
        Page<PredictionDetails> result = predictionDetailsSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
