package com.epam.bench.service.impl;

import com.epam.bench.service.UnitService;
import com.epam.bench.domain.Unit;
import com.epam.bench.repository.UnitRepository;
import com.epam.bench.repository.search.UnitSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Unit.
 */
@Service
@Transactional
public class UnitServiceImpl implements UnitService{

    private final Logger log = LoggerFactory.getLogger(UnitServiceImpl.class);

    private final UnitRepository unitRepository;

    private final UnitSearchRepository unitSearchRepository;

    public UnitServiceImpl(UnitRepository unitRepository, UnitSearchRepository unitSearchRepository) {
        this.unitRepository = unitRepository;
        this.unitSearchRepository = unitSearchRepository;
    }

    /**
     * Save a unit.
     *
     * @param unit the entity to save
     * @return the persisted entity
     */
    @Override
    public Unit save(Unit unit) {
        log.debug("Request to save Unit : {}", unit);
        Unit result = unitRepository.save(unit);
        unitSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the units.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Unit> findAll(Pageable pageable) {
        log.debug("Request to get all Units");
        return unitRepository.findAll(pageable);
    }

    /**
     *  Get one unit by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Unit findOne(Long id) {
        log.debug("Request to get Unit : {}", id);
        return unitRepository.findOne(id);
    }

    /**
     *  Delete the  unit by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Unit : {}", id);
        unitRepository.delete(id);
        unitSearchRepository.delete(id);
    }

    /**
     * Search for the unit corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Unit> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Units for query {}", query);
        Page<Unit> result = unitSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
