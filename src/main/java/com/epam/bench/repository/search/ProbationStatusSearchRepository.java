package com.epam.bench.repository.search;

import com.epam.bench.domain.ProbationStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ProbationStatus entity.
 */
public interface ProbationStatusSearchRepository extends ElasticsearchRepository<ProbationStatus, Long> {
}
