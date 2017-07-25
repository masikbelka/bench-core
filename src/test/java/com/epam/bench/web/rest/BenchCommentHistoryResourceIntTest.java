package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.BenchCommentHistory;
import com.epam.bench.repository.BenchCommentHistoryRepository;
import com.epam.bench.service.BenchCommentHistoryService;
import com.epam.bench.repository.search.BenchCommentHistorySearchRepository;
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
 * Test class for the BenchCommentHistoryResource REST controller.
 *
 * @see BenchCommentHistoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class BenchCommentHistoryResourceIntTest {

    private static final ZonedDateTime DEFAULT_CHANGE_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CHANGE_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_OLD_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_OLD_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_NEW_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_NEW_VALUE = "BBBBBBBBBB";

    @Autowired
    private BenchCommentHistoryRepository benchCommentHistoryRepository;

    @Autowired
    private BenchCommentHistoryService benchCommentHistoryService;

    @Autowired
    private BenchCommentHistorySearchRepository benchCommentHistorySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBenchCommentHistoryMockMvc;

    private BenchCommentHistory benchCommentHistory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BenchCommentHistoryResource benchCommentHistoryResource = new BenchCommentHistoryResource(benchCommentHistoryService);
        this.restBenchCommentHistoryMockMvc = MockMvcBuilders.standaloneSetup(benchCommentHistoryResource)
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
    public static BenchCommentHistory createEntity(EntityManager em) {
        BenchCommentHistory benchCommentHistory = new BenchCommentHistory()
            .changeTime(DEFAULT_CHANGE_TIME)
            .oldValue(DEFAULT_OLD_VALUE)
            .newValue(DEFAULT_NEW_VALUE);
        return benchCommentHistory;
    }

    @Before
    public void initTest() {
        benchCommentHistorySearchRepository.deleteAll();
        benchCommentHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createBenchCommentHistory() throws Exception {
        int databaseSizeBeforeCreate = benchCommentHistoryRepository.findAll().size();

        // Create the BenchCommentHistory
        restBenchCommentHistoryMockMvc.perform(post("/api/bench-comment-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchCommentHistory)))
            .andExpect(status().isCreated());

        // Validate the BenchCommentHistory in the database
        List<BenchCommentHistory> benchCommentHistoryList = benchCommentHistoryRepository.findAll();
        assertThat(benchCommentHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        BenchCommentHistory testBenchCommentHistory = benchCommentHistoryList.get(benchCommentHistoryList.size() - 1);
        assertThat(testBenchCommentHistory.getChangeTime()).isEqualTo(DEFAULT_CHANGE_TIME);
        assertThat(testBenchCommentHistory.getOldValue()).isEqualTo(DEFAULT_OLD_VALUE);
        assertThat(testBenchCommentHistory.getNewValue()).isEqualTo(DEFAULT_NEW_VALUE);

        // Validate the BenchCommentHistory in Elasticsearch
        BenchCommentHistory benchCommentHistoryEs = benchCommentHistorySearchRepository.findOne(testBenchCommentHistory.getId());
        assertThat(benchCommentHistoryEs).isEqualToComparingFieldByField(testBenchCommentHistory);
    }

    @Test
    @Transactional
    public void createBenchCommentHistoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = benchCommentHistoryRepository.findAll().size();

        // Create the BenchCommentHistory with an existing ID
        benchCommentHistory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBenchCommentHistoryMockMvc.perform(post("/api/bench-comment-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchCommentHistory)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<BenchCommentHistory> benchCommentHistoryList = benchCommentHistoryRepository.findAll();
        assertThat(benchCommentHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkChangeTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = benchCommentHistoryRepository.findAll().size();
        // set the field null
        benchCommentHistory.setChangeTime(null);

        // Create the BenchCommentHistory, which fails.

        restBenchCommentHistoryMockMvc.perform(post("/api/bench-comment-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchCommentHistory)))
            .andExpect(status().isBadRequest());

        List<BenchCommentHistory> benchCommentHistoryList = benchCommentHistoryRepository.findAll();
        assertThat(benchCommentHistoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBenchCommentHistories() throws Exception {
        // Initialize the database
        benchCommentHistoryRepository.saveAndFlush(benchCommentHistory);

        // Get all the benchCommentHistoryList
        restBenchCommentHistoryMockMvc.perform(get("/api/bench-comment-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(benchCommentHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].changeTime").value(hasItem(sameInstant(DEFAULT_CHANGE_TIME))))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE.toString())))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE.toString())));
    }

    @Test
    @Transactional
    public void getBenchCommentHistory() throws Exception {
        // Initialize the database
        benchCommentHistoryRepository.saveAndFlush(benchCommentHistory);

        // Get the benchCommentHistory
        restBenchCommentHistoryMockMvc.perform(get("/api/bench-comment-histories/{id}", benchCommentHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(benchCommentHistory.getId().intValue()))
            .andExpect(jsonPath("$.changeTime").value(sameInstant(DEFAULT_CHANGE_TIME)))
            .andExpect(jsonPath("$.oldValue").value(DEFAULT_OLD_VALUE.toString()))
            .andExpect(jsonPath("$.newValue").value(DEFAULT_NEW_VALUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBenchCommentHistory() throws Exception {
        // Get the benchCommentHistory
        restBenchCommentHistoryMockMvc.perform(get("/api/bench-comment-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBenchCommentHistory() throws Exception {
        // Initialize the database
        benchCommentHistoryService.save(benchCommentHistory);

        int databaseSizeBeforeUpdate = benchCommentHistoryRepository.findAll().size();

        // Update the benchCommentHistory
        BenchCommentHistory updatedBenchCommentHistory = benchCommentHistoryRepository.findOne(benchCommentHistory.getId());
        updatedBenchCommentHistory
            .changeTime(UPDATED_CHANGE_TIME)
            .oldValue(UPDATED_OLD_VALUE)
            .newValue(UPDATED_NEW_VALUE);

        restBenchCommentHistoryMockMvc.perform(put("/api/bench-comment-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBenchCommentHistory)))
            .andExpect(status().isOk());

        // Validate the BenchCommentHistory in the database
        List<BenchCommentHistory> benchCommentHistoryList = benchCommentHistoryRepository.findAll();
        assertThat(benchCommentHistoryList).hasSize(databaseSizeBeforeUpdate);
        BenchCommentHistory testBenchCommentHistory = benchCommentHistoryList.get(benchCommentHistoryList.size() - 1);
        assertThat(testBenchCommentHistory.getChangeTime()).isEqualTo(UPDATED_CHANGE_TIME);
        assertThat(testBenchCommentHistory.getOldValue()).isEqualTo(UPDATED_OLD_VALUE);
        assertThat(testBenchCommentHistory.getNewValue()).isEqualTo(UPDATED_NEW_VALUE);

        // Validate the BenchCommentHistory in Elasticsearch
        BenchCommentHistory benchCommentHistoryEs = benchCommentHistorySearchRepository.findOne(testBenchCommentHistory.getId());
        assertThat(benchCommentHistoryEs).isEqualToComparingFieldByField(testBenchCommentHistory);
    }

    @Test
    @Transactional
    public void updateNonExistingBenchCommentHistory() throws Exception {
        int databaseSizeBeforeUpdate = benchCommentHistoryRepository.findAll().size();

        // Create the BenchCommentHistory

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBenchCommentHistoryMockMvc.perform(put("/api/bench-comment-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(benchCommentHistory)))
            .andExpect(status().isCreated());

        // Validate the BenchCommentHistory in the database
        List<BenchCommentHistory> benchCommentHistoryList = benchCommentHistoryRepository.findAll();
        assertThat(benchCommentHistoryList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBenchCommentHistory() throws Exception {
        // Initialize the database
        benchCommentHistoryService.save(benchCommentHistory);

        int databaseSizeBeforeDelete = benchCommentHistoryRepository.findAll().size();

        // Get the benchCommentHistory
        restBenchCommentHistoryMockMvc.perform(delete("/api/bench-comment-histories/{id}", benchCommentHistory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean benchCommentHistoryExistsInEs = benchCommentHistorySearchRepository.exists(benchCommentHistory.getId());
        assertThat(benchCommentHistoryExistsInEs).isFalse();

        // Validate the database is empty
        List<BenchCommentHistory> benchCommentHistoryList = benchCommentHistoryRepository.findAll();
        assertThat(benchCommentHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBenchCommentHistory() throws Exception {
        // Initialize the database
        benchCommentHistoryService.save(benchCommentHistory);

        // Search the benchCommentHistory
        restBenchCommentHistoryMockMvc.perform(get("/api/_search/bench-comment-histories?query=id:" + benchCommentHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(benchCommentHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].changeTime").value(hasItem(sameInstant(DEFAULT_CHANGE_TIME))))
            .andExpect(jsonPath("$.[*].oldValue").value(hasItem(DEFAULT_OLD_VALUE.toString())))
            .andExpect(jsonPath("$.[*].newValue").value(hasItem(DEFAULT_NEW_VALUE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BenchCommentHistory.class);
        BenchCommentHistory benchCommentHistory1 = new BenchCommentHistory();
        benchCommentHistory1.setId(1L);
        BenchCommentHistory benchCommentHistory2 = new BenchCommentHistory();
        benchCommentHistory2.setId(benchCommentHistory1.getId());
        assertThat(benchCommentHistory1).isEqualTo(benchCommentHistory2);
        benchCommentHistory2.setId(2L);
        assertThat(benchCommentHistory1).isNotEqualTo(benchCommentHistory2);
        benchCommentHistory1.setId(null);
        assertThat(benchCommentHistory1).isNotEqualTo(benchCommentHistory2);
    }
}
