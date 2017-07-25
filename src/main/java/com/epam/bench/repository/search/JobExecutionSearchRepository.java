package com.epam.bench.repository.search;

import com.epam.bench.domain.JobExecution;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the JobExecution entity.
 */
public interface JobExecutionSearchRepository extends ElasticsearchRepository<JobExecution, Long> {
}
