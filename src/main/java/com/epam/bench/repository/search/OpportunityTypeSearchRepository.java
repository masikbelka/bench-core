package com.epam.bench.repository.search;

import com.epam.bench.domain.OpportunityType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the OpportunityType entity.
 */
public interface OpportunityTypeSearchRepository extends ElasticsearchRepository<OpportunityType, Long> {
}
