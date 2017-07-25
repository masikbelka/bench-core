package com.epam.bench.repository.search;

import com.epam.bench.domain.ProjectRole;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ProjectRole entity.
 */
public interface ProjectRoleSearchRepository extends ElasticsearchRepository<ProjectRole, Long> {
}
