package com.epam.bench.repository.search;

import com.epam.bench.domain.ProjectCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the ProjectCategory entity.
 */
public interface ProjectCategorySearchRepository extends ElasticsearchRepository<ProjectCategory, Long> {
}
