package com.epam.bench.repository.search;

import com.epam.bench.domain.ProjectHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ProjectHistory entity.
 */
public interface ProjectHistorySearchRepository extends ElasticsearchRepository<ProjectHistory, Long> {
}
