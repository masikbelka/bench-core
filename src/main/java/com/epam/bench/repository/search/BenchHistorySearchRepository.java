package com.epam.bench.repository.search;

import com.epam.bench.domain.BenchHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the BenchHistory entity.
 */
public interface BenchHistorySearchRepository extends ElasticsearchRepository<BenchHistory, Long> {
}
