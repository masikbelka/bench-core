package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.BenchHistory;
import com.epam.bench.repository.BenchHistoryRepository;
import com.epam.bench.service.BenchHistoryService;
import com.epam.bench.repository.search.BenchHistorySearchRepository;
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
 * Test class for the BenchHistoryResource REST controller.
 *
 * @see BenchHistoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class BenchHistoryResourceIntTest {

    private static final ZonedDateTime DEFAULT_CREATED_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Boolean DEFAULT_BENCH = false;
    private static final Boolean UPDATED_BENCH = true;

    private static final String DEFAULT_MANAGER_ID = "AAAAAAAAAA";
    private static final String UPDATED_MANAGER_ID = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_VALID_TO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_VALID_TO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_CREATED_BY_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_CHANGED_BY_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_CHANGED_BY_UPSA_ID = "BBBBBBBBBB";

    @Autowired
    private BenchHistoryRepository benchHistoryRepository;

    @Autowired
    private BenchHistoryService benchHistoryService;

    @Autowired
    private BenchHistorySearchRepository benchHistorySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBenchHistoryMockMvc;

    private BenchHistory benchHistory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BenchHistoryResource benchHistoryResource = new BenchHistoryResource(benchHistoryService);
        this.restBenchHistoryMockMvc = MockMvcBuilders.standaloneSetup(benchHistoryResource)
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
    public static BenchHistory createEntity(EntityManager em) {
        BenchHistory benchHistory = new BenchHistory()
            .createdTime(DEFAULT_CREATED_TIME)
            .bench(DEFAULT_BENCH)
            .managerId(DEFAULT_MANAGER_ID)
            .validTo(DEFAULT_VALID_TO)
            .createdByUpsaId(DEFAULT_CREATED_BY_UPSA_ID)
            .changedByUpsaId(DEFAULT_CHANGED_BY_UPSA_ID);
        return benchHistory;
    }

    @Before
    public void initTest() {
        benchHistorySearchRepository.deleteAll();
        benchHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createBenchHistory() throws Exception {
        int databaseSizeBeforeCreate = benchHistoryRepository.findAll().size();

        // Create the BenchHistory
        restBenchHistoryMockMvc.perform(post("/api/bench-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchHistory)))
            .andExpect(status().isCreated());

        // Validate the BenchHistory in the database
        List<BenchHistory> benchHistoryList = benchHistoryRepository.findAll();
        assertThat(benchHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        BenchHistory testBenchHistory = benchHistoryList.get(benchHistoryList.size() - 1);
        assertThat(testBenchHistory.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testBenchHistory.isBench()).isEqualTo(DEFAULT_BENCH);
        assertThat(testBenchHistory.getManagerId()).isEqualTo(DEFAULT_MANAGER_ID);
        assertThat(testBenchHistory.getValidTo()).isEqualTo(DEFAULT_VALID_TO);
        assertThat(testBenchHistory.getCreatedByUpsaId()).isEqualTo(DEFAULT_CREATED_BY_UPSA_ID);
        assertThat(testBenchHistory.getChangedByUpsaId()).isEqualTo(DEFAULT_CHANGED_BY_UPSA_ID);

        // Validate the BenchHistory in Elasticsearch
        BenchHistory benchHistoryEs = benchHistorySearchRepository.findOne(testBenchHistory.getId());
        assertThat(benchHistoryEs).isEqualToComparingFieldByField(testBenchHistory);
    }

    @Test
    @Transactional
    public void createBenchHistoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = benchHistoryRepository.findAll().size();

        // Create the BenchHistory with an existing ID
        benchHistory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBenchHistoryMockMvc.perform(post("/api/bench-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchHistory)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<BenchHistory> benchHistoryList = benchHistoryRepository.findAll();
        assertThat(benchHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCreatedTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = benchHistoryRepository.findAll().size();
        // set the field null
        benchHistory.setCreatedTime(null);

        // Create the BenchHistory, which fails.

        restBenchHistoryMockMvc.perform(post("/api/bench-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchHistory)))
            .andExpect(status().isBadRequest());

        List<BenchHistory> benchHistoryList = benchHistoryRepository.findAll();
        assertThat(benchHistoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBenchIsRequired() throws Exception {
        int databaseSizeBeforeTest = benchHistoryRepository.findAll().size();
        // set the field null
        benchHistory.setBench(null);

        // Create the BenchHistory, which fails.

        restBenchHistoryMockMvc.perform(post("/api/bench-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchHistory)))
            .andExpect(status().isBadRequest());

        List<BenchHistory> benchHistoryList = benchHistoryRepository.findAll();
        assertThat(benchHistoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBenchHistories() throws Exception {
        // Initialize the database
        benchHistoryRepository.saveAndFlush(benchHistory);

        // Get all the benchHistoryList
        restBenchHistoryMockMvc.perform(get("/api/bench-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(benchHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(sameInstant(DEFAULT_CREATED_TIME))))
            .andExpect(jsonPath("$.[*].bench").value(hasItem(DEFAULT_BENCH.booleanValue())))
            .andExpect(jsonPath("$.[*].managerId").value(hasItem(DEFAULT_MANAGER_ID.toString())))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(sameInstant(DEFAULT_VALID_TO))))
            .andExpect(jsonPath("$.[*].createdByUpsaId").value(hasItem(DEFAULT_CREATED_BY_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].changedByUpsaId").value(hasItem(DEFAULT_CHANGED_BY_UPSA_ID.toString())));
    }

    @Test
    @Transactional
    public void getBenchHistory() throws Exception {
        // Initialize the database
        benchHistoryRepository.saveAndFlush(benchHistory);

        // Get the benchHistory
        restBenchHistoryMockMvc.perform(get("/api/bench-histories/{id}", benchHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(benchHistory.getId().intValue()))
            .andExpect(jsonPath("$.createdTime").value(sameInstant(DEFAULT_CREATED_TIME)))
            .andExpect(jsonPath("$.bench").value(DEFAULT_BENCH.booleanValue()))
            .andExpect(jsonPath("$.managerId").value(DEFAULT_MANAGER_ID.toString()))
            .andExpect(jsonPath("$.validTo").value(sameInstant(DEFAULT_VALID_TO)))
            .andExpect(jsonPath("$.createdByUpsaId").value(DEFAULT_CREATED_BY_UPSA_ID.toString()))
            .andExpect(jsonPath("$.changedByUpsaId").value(DEFAULT_CHANGED_BY_UPSA_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBenchHistory() throws Exception {
        // Get the benchHistory
        restBenchHistoryMockMvc.perform(get("/api/bench-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBenchHistory() throws Exception {
        // Initialize the database
        benchHistoryService.save(benchHistory);

        int databaseSizeBeforeUpdate = benchHistoryRepository.findAll().size();

        // Update the benchHistory
        BenchHistory updatedBenchHistory = benchHistoryRepository.findOne(benchHistory.getId());
        updatedBenchHistory
            .createdTime(UPDATED_CREATED_TIME)
            .bench(UPDATED_BENCH)
            .managerId(UPDATED_MANAGER_ID)
            .validTo(UPDATED_VALID_TO)
            .createdByUpsaId(UPDATED_CREATED_BY_UPSA_ID)
            .changedByUpsaId(UPDATED_CHANGED_BY_UPSA_ID);

        restBenchHistoryMockMvc.perform(put("/api/bench-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBenchHistory)))
            .andExpect(status().isOk());

        // Validate the BenchHistory in the database
        List<BenchHistory> benchHistoryList = benchHistoryRepository.findAll();
        assertThat(benchHistoryList).hasSize(databaseSizeBeforeUpdate);
        BenchHistory testBenchHistory = benchHistoryList.get(benchHistoryList.size() - 1);
        assertThat(testBenchHistory.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testBenchHistory.isBench()).isEqualTo(UPDATED_BENCH);
        assertThat(testBenchHistory.getManagerId()).isEqualTo(UPDATED_MANAGER_ID);
        assertThat(testBenchHistory.getValidTo()).isEqualTo(UPDATED_VALID_TO);
        assertThat(testBenchHistory.getCreatedByUpsaId()).isEqualTo(UPDATED_CREATED_BY_UPSA_ID);
        assertThat(testBenchHistory.getChangedByUpsaId()).isEqualTo(UPDATED_CHANGED_BY_UPSA_ID);

        // Validate the BenchHistory in Elasticsearch
        BenchHistory benchHistoryEs = benchHistorySearchRepository.findOne(testBenchHistory.getId());
        assertThat(benchHistoryEs).isEqualToComparingFieldByField(testBenchHistory);
    }

    @Test
    @Transactional
    public void updateNonExistingBenchHistory() throws Exception {
        int databaseSizeBeforeUpdate = benchHistoryRepository.findAll().size();

        // Create the BenchHistory

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBenchHistoryMockMvc.perform(put("/api/bench-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchHistory)))
            .andExpect(status().isCreated());

        // Validate the BenchHistory in the database
        List<BenchHistory> benchHistoryList = benchHistoryRepository.findAll();
        assertThat(benchHistoryList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBenchHistory() throws Exception {
        // Initialize the database
        benchHistoryService.save(benchHistory);

        int databaseSizeBeforeDelete = benchHistoryRepository.findAll().size();

        // Get the benchHistory
        restBenchHistoryMockMvc.perform(delete("/api/bench-histories/{id}", benchHistory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean benchHistoryExistsInEs = benchHistorySearchRepository.exists(benchHistory.getId());
        assertThat(benchHistoryExistsInEs).isFalse();

        // Validate the database is empty
        List<BenchHistory> benchHistoryList = benchHistoryRepository.findAll();
        assertThat(benchHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBenchHistory() throws Exception {
        // Initialize the database
        benchHistoryService.save(benchHistory);

        // Search the benchHistory
        restBenchHistoryMockMvc.perform(get("/api/_search/bench-histories?query=id:" + benchHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(benchHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(sameInstant(DEFAULT_CREATED_TIME))))
            .andExpect(jsonPath("$.[*].bench").value(hasItem(DEFAULT_BENCH.booleanValue())))
            .andExpect(jsonPath("$.[*].managerId").value(hasItem(DEFAULT_MANAGER_ID.toString())))
            .andExpect(jsonPath("$.[*].validTo").value(hasItem(sameInstant(DEFAULT_VALID_TO))))
            .andExpect(jsonPath("$.[*].createdByUpsaId").value(hasItem(DEFAULT_CREATED_BY_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].changedByUpsaId").value(hasItem(DEFAULT_CHANGED_BY_UPSA_ID.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BenchHistory.class);
        BenchHistory benchHistory1 = new BenchHistory();
        benchHistory1.setId(1L);
        BenchHistory benchHistory2 = new BenchHistory();
        benchHistory2.setId(benchHistory1.getId());
        assertThat(benchHistory1).isEqualTo(benchHistory2);
        benchHistory2.setId(2L);
        assertThat(benchHistory1).isNotEqualTo(benchHistory2);
        benchHistory1.setId(null);
        assertThat(benchHistory1).isNotEqualTo(benchHistory2);
    }
}
