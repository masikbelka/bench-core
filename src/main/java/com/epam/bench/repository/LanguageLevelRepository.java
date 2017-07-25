package com.epam.bench.repository;

import com.epam.bench.domain.LanguageLevel;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the LanguageLevel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LanguageLevelRepository extends JpaRepository<LanguageLevel,Long> {
    
}
