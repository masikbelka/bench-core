package com.epam.bench.repository.search;

import com.epam.bench.domain.BillingConcept;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the BillingConcept entity.
 */
public interface BillingConceptSearchRepository extends ElasticsearchRepository<BillingConcept, Long> {
}
