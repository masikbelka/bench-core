package com.epam.bench.service.impl;

import com.epam.bench.service.OpportunityTypeService;
import com.epam.bench.domain.OpportunityType;
import com.epam.bench.repository.OpportunityTypeRepository;
import com.epam.bench.repository.search.OpportunityTypeSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing OpportunityType.
 */
@Service
@Transactional
public class OpportunityTypeServiceImpl implements OpportunityTypeService{

    private final Logger log = LoggerFactory.getLogger(OpportunityTypeServiceImpl.class);

    private final OpportunityTypeRepository opportunityTypeRepository;

    private final OpportunityTypeSearchRepository opportunityTypeSearchRepository;

    public OpportunityTypeServiceImpl(OpportunityTypeRepository opportunityTypeRepository, OpportunityTypeSearchRepository opportunityTypeSearchRepository) {
        this.opportunityTypeRepository = opportunityTypeRepository;
        this.opportunityTypeSearchRepository = opportunityTypeSearchRepository;
    }

    /**
     * Save a opportunityType.
     *
     * @param opportunityType the entity to save
     * @return the persisted entity
     */
    @Override
    public OpportunityType save(OpportunityType opportunityType) {
        log.debug("Request to save OpportunityType : {}", opportunityType);
        OpportunityType result = opportunityTypeRepository.save(opportunityType);
        opportunityTypeSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the opportunityTypes.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OpportunityType> findAll(Pageable pageable) {
        log.debug("Request to get all OpportunityTypes");
        return opportunityTypeRepository.findAll(pageable);
    }

    /**
     *  Get one opportunityType by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public OpportunityType findOne(Long id) {
        log.debug("Request to get OpportunityType : {}", id);
        return opportunityTypeRepository.findOne(id);
    }

    /**
     *  Delete the  opportunityType by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete OpportunityType : {}", id);
        opportunityTypeRepository.delete(id);
        opportunityTypeSearchRepository.delete(id);
    }

    /**
     * Search for the opportunityType corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OpportunityType> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OpportunityTypes for query {}", query);
        Page<OpportunityType> result = opportunityTypeSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
