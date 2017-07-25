package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.JobFunction;
import com.epam.bench.repository.JobFunctionRepository;
import com.epam.bench.service.JobFunctionService;
import com.epam.bench.repository.search.JobFunctionSearchRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the JobFunctionResource REST controller.
 *
 * @see JobFunctionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class JobFunctionResourceIntTest {

    private static final String DEFAULT_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PREFIX = "AAAAAAAAAA";
    private static final String UPDATED_PREFIX = "BBBBBBBBBB";

    @Autowired
    private JobFunctionRepository jobFunctionRepository;

    @Autowired
    private JobFunctionService jobFunctionService;

    @Autowired
    private JobFunctionSearchRepository jobFunctionSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restJobFunctionMockMvc;

    private JobFunction jobFunction;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JobFunctionResource jobFunctionResource = new JobFunctionResource(jobFunctionService);
        this.restJobFunctionMockMvc = MockMvcBuilders.standaloneSetup(jobFunctionResource)
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
    public static JobFunction createEntity(EntityManager em) {
        JobFunction jobFunction = new JobFunction()
            .upsaId(DEFAULT_UPSA_ID)
            .name(DEFAULT_NAME)
            .prefix(DEFAULT_PREFIX);
        return jobFunction;
    }

    @Before
    public void initTest() {
        jobFunctionSearchRepository.deleteAll();
        jobFunction = createEntity(em);
    }

    @Test
    @Transactional
    public void createJobFunction() throws Exception {
        int databaseSizeBeforeCreate = jobFunctionRepository.findAll().size();

        // Create the JobFunction
        restJobFunctionMockMvc.perform(post("/api/job-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobFunction)))
            .andExpect(status().isCreated());

        // Validate the JobFunction in the database
        List<JobFunction> jobFunctionList = jobFunctionRepository.findAll();
        assertThat(jobFunctionList).hasSize(databaseSizeBeforeCreate + 1);
        JobFunction testJobFunction = jobFunctionList.get(jobFunctionList.size() - 1);
        assertThat(testJobFunction.getUpsaId()).isEqualTo(DEFAULT_UPSA_ID);
        assertThat(testJobFunction.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testJobFunction.getPrefix()).isEqualTo(DEFAULT_PREFIX);

        // Validate the JobFunction in Elasticsearch
        JobFunction jobFunctionEs = jobFunctionSearchRepository.findOne(testJobFunction.getId());
        assertThat(jobFunctionEs).isEqualToComparingFieldByField(testJobFunction);
    }

    @Test
    @Transactional
    public void createJobFunctionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jobFunctionRepository.findAll().size();

        // Create the JobFunction with an existing ID
        jobFunction.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobFunctionMockMvc.perform(post("/api/job-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobFunction)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<JobFunction> jobFunctionList = jobFunctionRepository.findAll();
        assertThat(jobFunctionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUpsaIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobFunctionRepository.findAll().size();
        // set the field null
        jobFunction.setUpsaId(null);

        // Create the JobFunction, which fails.

        restJobFunctionMockMvc.perform(post("/api/job-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobFunction)))
            .andExpect(status().isBadRequest());

        List<JobFunction> jobFunctionList = jobFunctionRepository.findAll();
        assertThat(jobFunctionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobFunctionRepository.findAll().size();
        // set the field null
        jobFunction.setName(null);

        // Create the JobFunction, which fails.

        restJobFunctionMockMvc.perform(post("/api/job-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobFunction)))
            .andExpect(status().isBadRequest());

        List<JobFunction> jobFunctionList = jobFunctionRepository.findAll();
        assertThat(jobFunctionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllJobFunctions() throws Exception {
        // Initialize the database
        jobFunctionRepository.saveAndFlush(jobFunction);

        // Get all the jobFunctionList
        restJobFunctionMockMvc.perform(get("/api/job-functions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobFunction.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].prefix").value(hasItem(DEFAULT_PREFIX.toString())));
    }

    @Test
    @Transactional
    public void getJobFunction() throws Exception {
        // Initialize the database
        jobFunctionRepository.saveAndFlush(jobFunction);

        // Get the jobFunction
        restJobFunctionMockMvc.perform(get("/api/job-functions/{id}", jobFunction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jobFunction.getId().intValue()))
            .andExpect(jsonPath("$.upsaId").value(DEFAULT_UPSA_ID.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.prefix").value(DEFAULT_PREFIX.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingJobFunction() throws Exception {
        // Get the jobFunction
        restJobFunctionMockMvc.perform(get("/api/job-functions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJobFunction() throws Exception {
        // Initialize the database
        jobFunctionService.save(jobFunction);

        int databaseSizeBeforeUpdate = jobFunctionRepository.findAll().size();

        // Update the jobFunction
        JobFunction updatedJobFunction = jobFunctionRepository.findOne(jobFunction.getId());
        updatedJobFunction
            .upsaId(UPDATED_UPSA_ID)
            .name(UPDATED_NAME)
            .prefix(UPDATED_PREFIX);

        restJobFunctionMockMvc.perform(put("/api/job-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedJobFunction)))
            .andExpect(status().isOk());

        // Validate the JobFunction in the database
        List<JobFunction> jobFunctionList = jobFunctionRepository.findAll();
        assertThat(jobFunctionList).hasSize(databaseSizeBeforeUpdate);
        JobFunction testJobFunction = jobFunctionList.get(jobFunctionList.size() - 1);
        assertThat(testJobFunction.getUpsaId()).isEqualTo(UPDATED_UPSA_ID);
        assertThat(testJobFunction.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testJobFunction.getPrefix()).isEqualTo(UPDATED_PREFIX);

        // Validate the JobFunction in Elasticsearch
        JobFunction jobFunctionEs = jobFunctionSearchRepository.findOne(testJobFunction.getId());
        assertThat(jobFunctionEs).isEqualToComparingFieldByField(testJobFunction);
    }

    @Test
    @Transactional
    public void updateNonExistingJobFunction() throws Exception {
        int databaseSizeBeforeUpdate = jobFunctionRepository.findAll().size();

        // Create the JobFunction

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restJobFunctionMockMvc.perform(put("/api/job-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobFunction)))
            .andExpect(status().isCreated());

        // Validate the JobFunction in the database
        List<JobFunction> jobFunctionList = jobFunctionRepository.findAll();
        assertThat(jobFunctionList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteJobFunction() throws Exception {
        // Initialize the database
        jobFunctionService.save(jobFunction);

        int databaseSizeBeforeDelete = jobFunctionRepository.findAll().size();

        // Get the jobFunction
        restJobFunctionMockMvc.perform(delete("/api/job-functions/{id}", jobFunction.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean jobFunctionExistsInEs = jobFunctionSearchRepository.exists(jobFunction.getId());
        assertThat(jobFunctionExistsInEs).isFalse();

        // Validate the database is empty
        List<JobFunction> jobFunctionList = jobFunctionRepository.findAll();
        assertThat(jobFunctionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchJobFunction() throws Exception {
        // Initialize the database
        jobFunctionService.save(jobFunction);

        // Search the jobFunction
        restJobFunctionMockMvc.perform(get("/api/_search/job-functions?query=id:" + jobFunction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobFunction.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].prefix").value(hasItem(DEFAULT_PREFIX.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JobFunction.class);
        JobFunction jobFunction1 = new JobFunction();
        jobFunction1.setId(1L);
        JobFunction jobFunction2 = new JobFunction();
        jobFunction2.setId(jobFunction1.getId());
        assertThat(jobFunction1).isEqualTo(jobFunction2);
        jobFunction2.setId(2L);
        assertThat(jobFunction1).isNotEqualTo(jobFunction2);
        jobFunction1.setId(null);
        assertThat(jobFunction1).isNotEqualTo(jobFunction2);
    }
}
