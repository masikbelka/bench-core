package com.epam.bench.repository;

import com.epam.bench.domain.ProjectHistory;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ProjectHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectHistoryRepository extends JpaRepository<ProjectHistory,Long> {
    
}
