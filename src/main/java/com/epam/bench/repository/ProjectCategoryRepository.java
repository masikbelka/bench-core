package com.epam.bench.repository;

import com.epam.bench.domain.ProjectCategory;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ProjectCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory,Long> {
    
}
