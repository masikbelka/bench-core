package com.epam.bench.repository.search;

import com.epam.bench.domain.BenchCommentHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the BenchCommentHistory entity.
 */
public interface BenchCommentHistorySearchRepository extends ElasticsearchRepository<BenchCommentHistory, Long> {
}
