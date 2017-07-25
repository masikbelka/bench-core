package com.epam.bench.repository.search;

import com.epam.bench.domain.LanguageLevel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the LanguageLevel entity.
 */
public interface LanguageLevelSearchRepository extends ElasticsearchRepository<LanguageLevel, Long> {
}
