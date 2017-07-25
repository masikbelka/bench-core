package com.epam.bench.repository;

import com.epam.bench.domain.ProjectRole;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the ProjectRole entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRoleRepository extends JpaRepository<ProjectRole,Long> {
    
}
