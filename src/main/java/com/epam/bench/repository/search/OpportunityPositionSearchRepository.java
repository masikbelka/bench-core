package com.epam.bench.repository.search;

import com.epam.bench.domain.OpportunityPosition;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the OpportunityPosition entity.
 */
public interface OpportunityPositionSearchRepository extends ElasticsearchRepository<OpportunityPosition, Long> {
}
