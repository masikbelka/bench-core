package com.epam.bench.service;

import com.epam.bench.domain.ProductionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing ProductionStatus.
 */
public interface ProductionStatusService {

    /**
     * Save a productionStatus.
     *
     * @param productionStatus the entity to save
     * @return the persisted entity
     */
    ProductionStatus save(ProductionStatus productionStatus);

    /**
     *  Get all the productionStatuses.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ProductionStatus> findAll(Pageable pageable);

    /**
     *  Get the "id" productionStatus.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ProductionStatus findOne(Long id);

    /**
     *  Delete the "id" productionStatus.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the productionStatus corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ProductionStatus> search(String query, Pageable pageable);
}
