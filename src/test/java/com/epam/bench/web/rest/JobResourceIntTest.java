package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.Job;
import com.epam.bench.repository.JobRepository;
import com.epam.bench.service.JobService;
import com.epam.bench.repository.search.JobSearchRepository;
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
 * Test class for the JobResource REST controller.
 *
 * @see JobResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class JobResourceIntTest {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final Boolean DEFAULT_SEND_EMAIL = false;
    private static final Boolean UPDATED_SEND_EMAIL = true;

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_LAST_START_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_START_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_LAST_END_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_END_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final JobResultStatus DEFAULT_LAST_RESULT = JobResultStatus.SUCCESS;
    private static final JobResultStatus UPDATED_LAST_RESULT = JobResultStatus.ERROR;

    private static final String DEFAULT_LAST_USER = "AAAAAAAAAA";
    private static final String UPDATED_LAST_USER = "BBBBBBBBBB";

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobSearchRepository jobSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restJobMockMvc;

    private Job job;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JobResource jobResource = new JobResource(jobService);
        this.restJobMockMvc = MockMvcBuilders.standaloneSetup(jobResource)
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
    public static Job createEntity(EntityManager em) {
        Job job = new Job()
            .code(DEFAULT_CODE)
            .enabled(DEFAULT_ENABLED)
            .sendEmail(DEFAULT_SEND_EMAIL)
            .email(DEFAULT_EMAIL)
            .lastStartTime(DEFAULT_LAST_START_TIME)
            .lastEndTime(DEFAULT_LAST_END_TIME)
            .lastResult(DEFAULT_LAST_RESULT)
            .lastUser(DEFAULT_LAST_USER);
        return job;
    }

    @Before
    public void initTest() {
        jobSearchRepository.deleteAll();
        job = createEntity(em);
    }

    @Test
    @Transactional
    public void createJob() throws Exception {
        int databaseSizeBeforeCreate = jobRepository.findAll().size();

        // Create the Job
        restJobMockMvc.perform(post("/api/jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(job)))
            .andExpect(status().isCreated());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeCreate + 1);
        Job testJob = jobList.get(jobList.size() - 1);
        assertThat(testJob.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testJob.isEnabled()).isEqualTo(DEFAULT_ENABLED);
        assertThat(testJob.isSendEmail()).isEqualTo(DEFAULT_SEND_EMAIL);
        assertThat(testJob.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testJob.getLastStartTime()).isEqualTo(DEFAULT_LAST_START_TIME);
        assertThat(testJob.getLastEndTime()).isEqualTo(DEFAULT_LAST_END_TIME);
        assertThat(testJob.getLastResult()).isEqualTo(DEFAULT_LAST_RESULT);
        assertThat(testJob.getLastUser()).isEqualTo(DEFAULT_LAST_USER);

        // Validate the Job in Elasticsearch
        Job jobEs = jobSearchRepository.findOne(testJob.getId());
        assertThat(jobEs).isEqualToComparingFieldByField(testJob);
    }

    @Test
    @Transactional
    public void createJobWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jobRepository.findAll().size();

        // Create the Job with an existing ID
        job.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobMockMvc.perform(post("/api/jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(job)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllJobs() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList
        restJobMockMvc.perform(get("/api/jobs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(job.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].sendEmail").value(hasItem(DEFAULT_SEND_EMAIL.booleanValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].lastStartTime").value(hasItem(DEFAULT_LAST_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].lastEndTime").value(hasItem(DEFAULT_LAST_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].lastResult").value(hasItem(DEFAULT_LAST_RESULT.toString())))
            .andExpect(jsonPath("$.[*].lastUser").value(hasItem(DEFAULT_LAST_USER.toString())));
    }

    @Test
    @Transactional
    public void getJob() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get the job
        restJobMockMvc.perform(get("/api/jobs/{id}", job.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(job.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED.booleanValue()))
            .andExpect(jsonPath("$.sendEmail").value(DEFAULT_SEND_EMAIL.booleanValue()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.lastStartTime").value(DEFAULT_LAST_START_TIME.toString()))
            .andExpect(jsonPath("$.lastEndTime").value(DEFAULT_LAST_END_TIME.toString()))
            .andExpect(jsonPath("$.lastResult").value(DEFAULT_LAST_RESULT.toString()))
            .andExpect(jsonPath("$.lastUser").value(DEFAULT_LAST_USER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingJob() throws Exception {
        // Get the job
        restJobMockMvc.perform(get("/api/jobs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJob() throws Exception {
        // Initialize the database
        jobService.save(job);

        int databaseSizeBeforeUpdate = jobRepository.findAll().size();

        // Update the job
        Job updatedJob = jobRepository.findOne(job.getId());
        updatedJob
            .code(UPDATED_CODE)
            .enabled(UPDATED_ENABLED)
            .sendEmail(UPDATED_SEND_EMAIL)
            .email(UPDATED_EMAIL)
            .lastStartTime(UPDATED_LAST_START_TIME)
            .lastEndTime(UPDATED_LAST_END_TIME)
            .lastResult(UPDATED_LAST_RESULT)
            .lastUser(UPDATED_LAST_USER);

        restJobMockMvc.perform(put("/api/jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedJob)))
            .andExpect(status().isOk());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
        Job testJob = jobList.get(jobList.size() - 1);
        assertThat(testJob.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testJob.isEnabled()).isEqualTo(UPDATED_ENABLED);
        assertThat(testJob.isSendEmail()).isEqualTo(UPDATED_SEND_EMAIL);
        assertThat(testJob.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testJob.getLastStartTime()).isEqualTo(UPDATED_LAST_START_TIME);
        assertThat(testJob.getLastEndTime()).isEqualTo(UPDATED_LAST_END_TIME);
        assertThat(testJob.getLastResult()).isEqualTo(UPDATED_LAST_RESULT);
        assertThat(testJob.getLastUser()).isEqualTo(UPDATED_LAST_USER);

        // Validate the Job in Elasticsearch
        Job jobEs = jobSearchRepository.findOne(testJob.getId());
        assertThat(jobEs).isEqualToComparingFieldByField(testJob);
    }

    @Test
    @Transactional
    public void updateNonExistingJob() throws Exception {
        int databaseSizeBeforeUpdate = jobRepository.findAll().size();

        // Create the Job

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restJobMockMvc.perform(put("/api/jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(job)))
            .andExpect(status().isCreated());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteJob() throws Exception {
        // Initialize the database
        jobService.save(job);

        int databaseSizeBeforeDelete = jobRepository.findAll().size();

        // Get the job
        restJobMockMvc.perform(delete("/api/jobs/{id}", job.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean jobExistsInEs = jobSearchRepository.exists(job.getId());
        assertThat(jobExistsInEs).isFalse();

        // Validate the database is empty
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchJob() throws Exception {
        // Initialize the database
        jobService.save(job);

        // Search the job
        restJobMockMvc.perform(get("/api/_search/jobs?query=id:" + job.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(job.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED.booleanValue())))
            .andExpect(jsonPath("$.[*].sendEmail").value(hasItem(DEFAULT_SEND_EMAIL.booleanValue())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].lastStartTime").value(hasItem(DEFAULT_LAST_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].lastEndTime").value(hasItem(DEFAULT_LAST_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].lastResult").value(hasItem(DEFAULT_LAST_RESULT.toString())))
            .andExpect(jsonPath("$.[*].lastUser").value(hasItem(DEFAULT_LAST_USER.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Job.class);
        Job job1 = new Job();
        job1.setId(1L);
        Job job2 = new Job();
        job2.setId(job1.getId());
        assertThat(job1).isEqualTo(job2);
        job2.setId(2L);
        assertThat(job1).isNotEqualTo(job2);
        job1.setId(null);
        assertThat(job1).isNotEqualTo(job2);
    }
}
