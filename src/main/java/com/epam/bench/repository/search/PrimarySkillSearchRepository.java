package com.epam.bench.repository.search;

import com.epam.bench.domain.PrimarySkill;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the PrimarySkill entity.
 */
public interface PrimarySkillSearchRepository extends ElasticsearchRepository<PrimarySkill, Long> {
}
