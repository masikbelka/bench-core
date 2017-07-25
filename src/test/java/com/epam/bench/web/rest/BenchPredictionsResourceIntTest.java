package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.BenchPredictions;
import com.epam.bench.repository.BenchPredictionsRepository;
import com.epam.bench.service.BenchPredictionsService;
import com.epam.bench.repository.search.BenchPredictionsSearchRepository;
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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.epam.bench.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BenchPredictionsResource REST controller.
 *
 * @see BenchPredictionsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class BenchPredictionsResourceIntTest {

    private static final ZonedDateTime DEFAULT_CREATED_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Boolean DEFAULT_IGNORED = false;
    private static final Boolean UPDATED_IGNORED = true;

    private static final Boolean DEFAULT_IGNORED_DAYS = false;
    private static final Boolean UPDATED_IGNORED_DAYS = true;

    private static final Boolean DEFAULT_READY_TO_BENCH = false;
    private static final Boolean UPDATED_READY_TO_BENCH = true;

    private static final Boolean DEFAULT_READY_TO_PRODUCTION = false;
    private static final Boolean UPDATED_READY_TO_PRODUCTION = true;

    @Autowired
    private BenchPredictionsRepository benchPredictionsRepository;

    @Autowired
    private BenchPredictionsService benchPredictionsService;

    @Autowired
    private BenchPredictionsSearchRepository benchPredictionsSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBenchPredictionsMockMvc;

    private BenchPredictions benchPredictions;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BenchPredictionsResource benchPredictionsResource = new BenchPredictionsResource(benchPredictionsService);
        this.restBenchPredictionsMockMvc = MockMvcBuilders.standaloneSetup(benchPredictionsResource)
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
    public static BenchPredictions createEntity(EntityManager em) {
        BenchPredictions benchPredictions = new BenchPredictions()
            .createdTime(DEFAULT_CREATED_TIME)
            .ignored(DEFAULT_IGNORED)
            .ignoredDays(DEFAULT_IGNORED_DAYS)
            .readyToBench(DEFAULT_READY_TO_BENCH)
            .readyToProduction(DEFAULT_READY_TO_PRODUCTION);
        return benchPredictions;
    }

    @Before
    public void initTest() {
        benchPredictionsSearchRepository.deleteAll();
        benchPredictions = createEntity(em);
    }

    @Test
    @Transactional
    public void createBenchPredictions() throws Exception {
        int databaseSizeBeforeCreate = benchPredictionsRepository.findAll().size();

        // Create the BenchPredictions
        restBenchPredictionsMockMvc.perform(post("/api/bench-predictions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchPredictions)))
            .andExpect(status().isCreated());

        // Validate the BenchPredictions in the database
        List<BenchPredictions> benchPredictionsList = benchPredictionsRepository.findAll();
        assertThat(benchPredictionsList).hasSize(databaseSizeBeforeCreate + 1);
        BenchPredictions testBenchPredictions = benchPredictionsList.get(benchPredictionsList.size() - 1);
        assertThat(testBenchPredictions.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testBenchPredictions.isIgnored()).isEqualTo(DEFAULT_IGNORED);
        assertThat(testBenchPredictions.isIgnoredDays()).isEqualTo(DEFAULT_IGNORED_DAYS);
        assertThat(testBenchPredictions.isReadyToBench()).isEqualTo(DEFAULT_READY_TO_BENCH);
        assertThat(testBenchPredictions.isReadyToProduction()).isEqualTo(DEFAULT_READY_TO_PRODUCTION);

        // Validate the BenchPredictions in Elasticsearch
        BenchPredictions benchPredictionsEs = benchPredictionsSearchRepository.findOne(testBenchPredictions.getId());
        assertThat(benchPredictionsEs).isEqualToComparingFieldByField(testBenchPredictions);
    }

    @Test
    @Transactional
    public void createBenchPredictionsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = benchPredictionsRepository.findAll().size();

        // Create the BenchPredictions with an existing ID
        benchPredictions.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBenchPredictionsMockMvc.perform(post("/api/bench-predictions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchPredictions)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<BenchPredictions> benchPredictionsList = benchPredictionsRepository.findAll();
        assertThat(benchPredictionsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCreatedTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = benchPredictionsRepository.findAll().size();
        // set the field null
        benchPredictions.setCreatedTime(null);

        // Create the BenchPredictions, which fails.

        restBenchPredictionsMockMvc.perform(post("/api/bench-predictions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchPredictions)))
            .andExpect(status().isBadRequest());

        List<BenchPredictions> benchPredictionsList = benchPredictionsRepository.findAll();
        assertThat(benchPredictionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIgnoredIsRequired() throws Exception {
        int databaseSizeBeforeTest = benchPredictionsRepository.findAll().size();
        // set the field null
        benchPredictions.setIgnored(null);

        // Create the BenchPredictions, which fails.

        restBenchPredictionsMockMvc.perform(post("/api/bench-predictions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchPredictions)))
            .andExpect(status().isBadRequest());

        List<BenchPredictions> benchPredictionsList = benchPredictionsRepository.findAll();
        assertThat(benchPredictionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkReadyToBenchIsRequired() throws Exception {
        int databaseSizeBeforeTest = benchPredictionsRepository.findAll().size();
        // set the field null
        benchPredictions.setReadyToBench(null);

        // Create the BenchPredictions, which fails.

        restBenchPredictionsMockMvc.perform(post("/api/bench-predictions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchPredictions)))
            .andExpect(status().isBadRequest());

        List<BenchPredictions> benchPredictionsList = benchPredictionsRepository.findAll();
        assertThat(benchPredictionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkReadyToProductionIsRequired() throws Exception {
        int databaseSizeBeforeTest = benchPredictionsRepository.findAll().size();
        // set the field null
        benchPredictions.setReadyToProduction(null);

        // Create the BenchPredictions, which fails.

        restBenchPredictionsMockMvc.perform(post("/api/bench-predictions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchPredictions)))
            .andExpect(status().isBadRequest());

        List<BenchPredictions> benchPredictionsList = benchPredictionsRepository.findAll();
        assertThat(benchPredictionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBenchPredictions() throws Exception {
        // Initialize the database
        benchPredictionsRepository.saveAndFlush(benchPredictions);

        // Get all the benchPredictionsList
        restBenchPredictionsMockMvc.perform(get("/api/bench-predictions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(benchPredictions.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(sameInstant(DEFAULT_CREATED_TIME))))
            .andExpect(jsonPath("$.[*].ignored").value(hasItem(DEFAULT_IGNORED.booleanValue())))
            .andExpect(jsonPath("$.[*].ignoredDays").value(hasItem(DEFAULT_IGNORED_DAYS.booleanValue())))
            .andExpect(jsonPath("$.[*].readyToBench").value(hasItem(DEFAULT_READY_TO_BENCH.booleanValue())))
            .andExpect(jsonPath("$.[*].readyToProduction").value(hasItem(DEFAULT_READY_TO_PRODUCTION.booleanValue())));
    }

    @Test
    @Transactional
    public void getBenchPredictions() throws Exception {
        // Initialize the database
        benchPredictionsRepository.saveAndFlush(benchPredictions);

        // Get the benchPredictions
        restBenchPredictionsMockMvc.perform(get("/api/bench-predictions/{id}", benchPredictions.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(benchPredictions.getId().intValue()))
            .andExpect(jsonPath("$.createdTime").value(sameInstant(DEFAULT_CREATED_TIME)))
            .andExpect(jsonPath("$.ignored").value(DEFAULT_IGNORED.booleanValue()))
            .andExpect(jsonPath("$.ignoredDays").value(DEFAULT_IGNORED_DAYS.booleanValue()))
            .andExpect(jsonPath("$.readyToBench").value(DEFAULT_READY_TO_BENCH.booleanValue()))
            .andExpect(jsonPath("$.readyToProduction").value(DEFAULT_READY_TO_PRODUCTION.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingBenchPredictions() throws Exception {
        // Get the benchPredictions
        restBenchPredictionsMockMvc.perform(get("/api/bench-predictions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBenchPredictions() throws Exception {
        // Initialize the database
        benchPredictionsService.save(benchPredictions);

        int databaseSizeBeforeUpdate = benchPredictionsRepository.findAll().size();

        // Update the benchPredictions
        BenchPredictions updatedBenchPredictions = benchPredictionsRepository.findOne(benchPredictions.getId());
        updatedBenchPredictions
            .createdTime(UPDATED_CREATED_TIME)
            .ignored(UPDATED_IGNORED)
            .ignoredDays(UPDATED_IGNORED_DAYS)
            .readyToBench(UPDATED_READY_TO_BENCH)
            .readyToProduction(UPDATED_READY_TO_PRODUCTION);

        restBenchPredictionsMockMvc.perform(put("/api/bench-predictions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBenchPredictions)))
            .andExpect(status().isOk());

        // Validate the BenchPredictions in the database
        List<BenchPredictions> benchPredictionsList = benchPredictionsRepository.findAll();
        assertThat(benchPredictionsList).hasSize(databaseSizeBeforeUpdate);
        BenchPredictions testBenchPredictions = benchPredictionsList.get(benchPredictionsList.size() - 1);
        assertThat(testBenchPredictions.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testBenchPredictions.isIgnored()).isEqualTo(UPDATED_IGNORED);
        assertThat(testBenchPredictions.isIgnoredDays()).isEqualTo(UPDATED_IGNORED_DAYS);
        assertThat(testBenchPredictions.isReadyToBench()).isEqualTo(UPDATED_READY_TO_BENCH);
        assertThat(testBenchPredictions.isReadyToProduction()).isEqualTo(UPDATED_READY_TO_PRODUCTION);

        // Validate the BenchPredictions in Elasticsearch
        BenchPredictions benchPredictionsEs = benchPredictionsSearchRepository.findOne(testBenchPredictions.getId());
        assertThat(benchPredictionsEs).isEqualToComparingFieldByField(testBenchPredictions);
    }

    @Test
    @Transactional
    public void updateNonExistingBenchPredictions() throws Exception {
        int databaseSizeBeforeUpdate = benchPredictionsRepository.findAll().size();

        // Create the BenchPredictions

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBenchPredictionsMockMvc.perform(put("/api/bench-predictions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchPredictions)))
            .andExpect(status().isCreated());

        // Validate the BenchPredictions in the database
        List<BenchPredictions> benchPredictionsList = benchPredictionsRepository.findAll();
        assertThat(benchPredictionsList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBenchPredictions() throws Exception {
        // Initialize the database
        benchPredictionsService.save(benchPredictions);

        int databaseSizeBeforeDelete = benchPredictionsRepository.findAll().size();

        // Get the benchPredictions
        restBenchPredictionsMockMvc.perform(delete("/api/bench-predictions/{id}", benchPredictions.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean benchPredictionsExistsInEs = benchPredictionsSearchRepository.exists(benchPredictions.getId());
        assertThat(benchPredictionsExistsInEs).isFalse();

        // Validate the database is empty
        List<BenchPredictions> benchPredictionsList = benchPredictionsRepository.findAll();
        assertThat(benchPredictionsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBenchPredictions() throws Exception {
        // Initialize the database
        benchPredictionsService.save(benchPredictions);

        // Search the benchPredictions
        restBenchPredictionsMockMvc.perform(get("/api/_search/bench-predictions?query=id:" + benchPredictions.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(benchPredictions.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(sameInstant(DEFAULT_CREATED_TIME))))
            .andExpect(jsonPath("$.[*].ignored").value(hasItem(DEFAULT_IGNORED.booleanValue())))
            .andExpect(jsonPath("$.[*].ignoredDays").value(hasItem(DEFAULT_IGNORED_DAYS.booleanValue())))
            .andExpect(jsonPath("$.[*].readyToBench").value(hasItem(DEFAULT_READY_TO_BENCH.booleanValue())))
            .andExpect(jsonPath("$.[*].readyToProduction").value(hasItem(DEFAULT_READY_TO_PRODUCTION.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BenchPredictions.class);
        BenchPredictions benchPredictions1 = new BenchPredictions();
        benchPredictions1.setId(1L);
        BenchPredictions benchPredictions2 = new BenchPredictions();
        benchPredictions2.setId(benchPredictions1.getId());
        assertThat(benchPredictions1).isEqualTo(benchPredictions2);
        benchPredictions2.setId(2L);
        assertThat(benchPredictions1).isNotEqualTo(benchPredictions2);
        benchPredictions1.setId(null);
        assertThat(benchPredictions1).isNotEqualTo(benchPredictions2);
    }
}
