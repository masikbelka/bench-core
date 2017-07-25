package com.epam.bench.repository;

import com.epam.bench.domain.PrimarySkill;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the PrimarySkill entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PrimarySkillRepository extends JpaRepository<PrimarySkill,Long> {
    
}
