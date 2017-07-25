package com.epam.bench.service.impl;

import com.epam.bench.service.BenchHistoryService;
import com.epam.bench.domain.BenchHistory;
import com.epam.bench.repository.BenchHistoryRepository;
import com.epam.bench.repository.search.BenchHistorySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing BenchHistory.
 */
@Service
@Transactional
public class BenchHistoryServiceImpl implements BenchHistoryService{

    private final Logger log = LoggerFactory.getLogger(BenchHistoryServiceImpl.class);

    private final BenchHistoryRepository benchHistoryRepository;

    private final BenchHistorySearchRepository benchHistorySearchRepository;

    public BenchHistoryServiceImpl(BenchHistoryRepository benchHistoryRepository, BenchHistorySearchRepository benchHistorySearchRepository) {
        this.benchHistoryRepository = benchHistoryRepository;
        this.benchHistorySearchRepository = benchHistorySearchRepository;
    }

    /**
     * Save a benchHistory.
     *
     * @param benchHistory the entity to save
     * @return the persisted entity
     */
    @Override
    public BenchHistory save(BenchHistory benchHistory) {
        log.debug("Request to save BenchHistory : {}", benchHistory);
        BenchHistory result = benchHistoryRepository.save(benchHistory);
        benchHistorySearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the benchHistories.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BenchHistory> findAll(Pageable pageable) {
        log.debug("Request to get all BenchHistories");
        return benchHistoryRepository.findAll(pageable);
    }

    /**
     *  Get one benchHistory by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public BenchHistory findOne(Long id) {
        log.debug("Request to get BenchHistory : {}", id);
        return benchHistoryRepository.findOne(id);
    }

    /**
     *  Delete the  benchHistory by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete BenchHistory : {}", id);
        benchHistoryRepository.delete(id);
        benchHistorySearchRepository.delete(id);
    }

    /**
     * Search for the benchHistory corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BenchHistory> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of BenchHistories for query {}", query);
        Page<BenchHistory> result = benchHistorySearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
