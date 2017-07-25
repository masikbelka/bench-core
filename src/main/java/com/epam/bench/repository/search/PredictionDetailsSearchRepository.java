package com.epam.bench.repository.search;

import com.epam.bench.domain.PredictionDetails;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the PredictionDetails entity.
 */
public interface PredictionDetailsSearchRepository extends ElasticsearchRepository<PredictionDetails, Long> {
}
