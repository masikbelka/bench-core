package com.epam.bench.repository.search;

import com.epam.bench.domain.BillingType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the BillingType entity.
 */
public interface BillingTypeSearchRepository extends ElasticsearchRepository<BillingType, Long> {
}
