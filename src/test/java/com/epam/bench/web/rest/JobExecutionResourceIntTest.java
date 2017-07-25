package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.JobExecution;
import com.epam.bench.repository.JobExecutionRepository;
import com.epam.bench.service.JobExecutionService;
import com.epam.bench.repository.search.JobExecutionSearchRepository;
import com.epam.bench.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.epam.bench.domain.enumeration.JobResultStatus;
/**
 * Test class for the JobExecutionResource REST controller.
 *
 * @see JobExecutionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class JobExecutionResourceIntTest {

    private static final LocalDate DEFAULT_START_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final JobResultStatus DEFAULT_RESULT = JobResultStatus.SUCCESS;
    private static final JobResultStatus UPDATED_RESULT = JobResultStatus.ERROR;

    private static final String DEFAULT_USER = "AAAAAAAAAA";
    private static final String UPDATED_USER = "BBBBBBBBBB";

    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @Autowired
    private JobExecutionService jobExecutionService;

    @Autowired
    private JobExecutionSearchRepository jobExecutionSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restJobExecutionMockMvc;

    private JobExecution jobExecution;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JobExecutionResource jobExecutionResource = new JobExecutionResource(jobExecutionService);
        this.restJobExecutionMockMvc = MockMvcBuilders.standaloneSetup(jobExecutionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobExecution createEntity(EntityManager em) {
        JobExecution jobExecution = new JobExecution()
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .result(DEFAULT_RESULT)
            .user(DEFAULT_USER);
        return jobExecution;
    }

    @Before
    public void initTest() {
        jobExecutionSearchRepository.deleteAll();
        jobExecution = createEntity(em);
    }

    @Test
    @Transactional
    public void createJobExecution() throws Exception {
        int databaseSizeBeforeCreate = jobExecutionRepository.findAll().size();

        // Create the JobExecution
        restJobExecutionMockMvc.perform(post("/api/job-executions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobExecution)))
            .andExpect(status().isCreated());

        // Validate the JobExecution in the database
        List<JobExecution> jobExecutionList = jobExecutionRepository.findAll();
        assertThat(jobExecutionList).hasSize(databaseSizeBeforeCreate + 1);
        JobExecution testJobExecution = jobExecutionList.get(jobExecutionList.size() - 1);
        assertThat(testJobExecution.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testJobExecution.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testJobExecution.getResult()).isEqualTo(DEFAULT_RESULT);
        assertThat(testJobExecution.getUser()).isEqualTo(DEFAULT_USER);

        // Validate the JobExecution in Elasticsearch
        JobExecution jobExecutionEs = jobExecutionSearchRepository.findOne(testJobExecution.getId());
        assertThat(jobExecutionEs).isEqualToComparingFieldByField(testJobExecution);
    }

    @Test
    @Transactional
    public void createJobExecutionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jobExecutionRepository.findAll().size();

        // Create the JobExecution with an existing ID
        jobExecution.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobExecutionMockMvc.perform(post("/api/job-executions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobExecution)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<JobExecution> jobExecutionList = jobExecutionRepository.findAll();
        assertThat(jobExecutionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllJobExecutions() throws Exception {
        // Initialize the database
        jobExecutionRepository.saveAndFlush(jobExecution);

        // Get all the jobExecutionList
        restJobExecutionMockMvc.perform(get("/api/job-executions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobExecution.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].result").value(hasItem(DEFAULT_RESULT.toString())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER.toString())));
    }

    @Test
    @Transactional
    public void getJobExecution() throws Exception {
        // Initialize the database
        jobExecutionRepository.saveAndFlush(jobExecution);

        // Get the jobExecution
        restJobExecutionMockMvc.perform(get("/api/job-executions/{id}", jobExecution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jobExecution.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.result").value(DEFAULT_RESULT.toString()))
            .andExpect(jsonPath("$.user").value(DEFAULT_USER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingJobExecution() throws Exception {
        // Get the jobExecution
        restJobExecutionMockMvc.perform(get("/api/job-executions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJobExecution() throws Exception {
        // Initialize the database
        jobExecutionService.save(jobExecution);

        int databaseSizeBeforeUpdate = jobExecutionRepository.findAll().size();

        // Update the jobExecution
        JobExecution updatedJobExecution = jobExecutionRepository.findOne(jobExecution.getId());
        updatedJobExecution
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .result(UPDATED_RESULT)
            .user(UPDATED_USER);

        restJobExecutionMockMvc.perform(put("/api/job-executions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedJobExecution)))
            .andExpect(status().isOk());

        // Validate the JobExecution in the database
        List<JobExecution> jobExecutionList = jobExecutionRepository.findAll();
        assertThat(jobExecutionList).hasSize(databaseSizeBeforeUpdate);
        JobExecution testJobExecution = jobExecutionList.get(jobExecutionList.size() - 1);
        assertThat(testJobExecution.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testJobExecution.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testJobExecution.getResult()).isEqualTo(UPDATED_RESULT);
        assertThat(testJobExecution.getUser()).isEqualTo(UPDATED_USER);

        // Validate the JobExecution in Elasticsearch
        JobExecution jobExecutionEs = jobExecutionSearchRepository.findOne(testJobExecution.getId());
        assertThat(jobExecutionEs).isEqualToComparingFieldByField(testJobExecution);
    }

    @Test
    @Transactional
    public void updateNonExistingJobExecution() throws Exception {
        int databaseSizeBeforeUpdate = jobExecutionRepository.findAll().size();

        // Create the JobExecution

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restJobExecutionMockMvc.perform(put("/api/job-executions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobExecution)))
            .andExpect(status().isCreated());

        // Validate the JobExecution in the database
        List<JobExecution> jobExecutionList = jobExecutionRepository.findAll();
        assertThat(jobExecutionList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteJobExecution() throws Exception {
        // Initialize the database
        jobExecutionService.save(jobExecution);

        int databaseSizeBeforeDelete = jobExecutionRepository.findAll().size();

        // Get the jobExecution
        restJobExecutionMockMvc.perform(delete("/api/job-executions/{id}", jobExecution.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean jobExecutionExistsInEs = jobExecutionSearchRepository.exists(jobExecution.getId());
        assertThat(jobExecutionExistsInEs).isFalse();

        // Validate the database is empty
        List<JobExecution> jobExecutionList = jobExecutionRepository.findAll();
        assertThat(jobExecutionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchJobExecution() throws Exception {
        // Initialize the database
        jobExecutionService.save(jobExecution);

        // Search the jobExecution
        restJobExecutionMockMvc.perform(get("/api/_search/job-executions?query=id:" + jobExecution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobExecution.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].result").value(hasItem(DEFAULT_RESULT.toString())))
            .andExpect(jsonPath("$.[*].user").value(hasItem(DEFAULT_USER.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JobExecution.class);
        JobExecution jobExecution1 = new JobExecution();
        jobExecution1.setId(1L);
        JobExecution jobExecution2 = new JobExecution();
        jobExecution2.setId(jobExecution1.getId());
        assertThat(jobExecution1).isEqualTo(jobExecution2);
        jobExecution2.setId(2L);
        assertThat(jobExecution1).isNotEqualTo(jobExecution2);
        jobExecution1.setId(null);
        assertThat(jobExecution1).isNotEqualTo(jobExecution2);
    }
}
