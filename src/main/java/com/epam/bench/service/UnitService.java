package com.epam.bench.service;

import com.epam.bench.domain.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Unit.
 */
public interface UnitService {

    /**
     * Save a unit.
     *
     * @param unit the entity to save
     * @return the persisted entity
     */
    Unit save(Unit unit);

    /**
     *  Get all the units.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Unit> findAll(Pageable pageable);

    /**
     *  Get the "id" unit.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Unit findOne(Long id);

    /**
     *  Delete the "id" unit.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the unit corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Unit> search(String query, Pageable pageable);
}
