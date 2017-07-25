package com.epam.bench.repository;

import com.epam.bench.domain.BenchCommentHistory;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the BenchCommentHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BenchCommentHistoryRepository extends JpaRepository<BenchCommentHistory,Long> {
    
}
