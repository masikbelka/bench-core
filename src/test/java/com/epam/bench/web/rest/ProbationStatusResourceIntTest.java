package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.ProbationStatus;
import com.epam.bench.repository.ProbationStatusRepository;
import com.epam.bench.service.ProbationStatusService;
import com.epam.bench.repository.search.ProbationStatusSearchRepository;
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
 * Test class for the ProbationStatusResource REST controller.
 *
 * @see ProbationStatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class ProbationStatusResourceIntTest {

    private static final ZonedDateTime DEFAULT_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    @Autowired
    private ProbationStatusRepository probationStatusRepository;

    @Autowired
    private ProbationStatusService probationStatusService;

    @Autowired
    private ProbationStatusSearchRepository probationStatusSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restProbationStatusMockMvc;

    private ProbationStatus probationStatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProbationStatusResource probationStatusResource = new ProbationStatusResource(probationStatusService);
        this.restProbationStatusMockMvc = MockMvcBuilders.standaloneSetup(probationStatusResource)
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
    public static ProbationStatus createEntity(EntityManager em) {
        ProbationStatus probationStatus = new ProbationStatus()
            .endDate(DEFAULT_END_DATE)
            .status(DEFAULT_STATUS);
        return probationStatus;
    }

    @Before
    public void initTest() {
        probationStatusSearchRepository.deleteAll();
        probationStatus = createEntity(em);
    }

    @Test
    @Transactional
    public void createProbationStatus() throws Exception {
        int databaseSizeBeforeCreate = probationStatusRepository.findAll().size();

        // Create the ProbationStatus
        restProbationStatusMockMvc.perform(post("/api/probation-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(probationStatus)))
            .andExpect(status().isCreated());

        // Validate the ProbationStatus in the database
        List<ProbationStatus> probationStatusList = probationStatusRepository.findAll();
        assertThat(probationStatusList).hasSize(databaseSizeBeforeCreate + 1);
        ProbationStatus testProbationStatus = probationStatusList.get(probationStatusList.size() - 1);
        assertThat(testProbationStatus.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testProbationStatus.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the ProbationStatus in Elasticsearch
        ProbationStatus probationStatusEs = probationStatusSearchRepository.findOne(testProbationStatus.getId());
        assertThat(probationStatusEs).isEqualToComparingFieldByField(testProbationStatus);
    }

    @Test
    @Transactional
    public void createProbationStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = probationStatusRepository.findAll().size();

        // Create the ProbationStatus with an existing ID
        probationStatus.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProbationStatusMockMvc.perform(post("/api/probation-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(probationStatus)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ProbationStatus> probationStatusList = probationStatusRepository.findAll();
        assertThat(probationStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllProbationStatuses() throws Exception {
        // Initialize the database
        probationStatusRepository.saveAndFlush(probationStatus);

        // Get all the probationStatusList
        restProbationStatusMockMvc.perform(get("/api/probation-statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(probationStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getProbationStatus() throws Exception {
        // Initialize the database
        probationStatusRepository.saveAndFlush(probationStatus);

        // Get the probationStatus
        restProbationStatusMockMvc.perform(get("/api/probation-statuses/{id}", probationStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(probationStatus.getId().intValue()))
            .andExpect(jsonPath("$.endDate").value(sameInstant(DEFAULT_END_DATE)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProbationStatus() throws Exception {
        // Get the probationStatus
        restProbationStatusMockMvc.perform(get("/api/probation-statuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProbationStatus() throws Exception {
        // Initialize the database
        probationStatusService.save(probationStatus);

        int databaseSizeBeforeUpdate = probationStatusRepository.findAll().size();

        // Update the probationStatus
        ProbationStatus updatedProbationStatus = probationStatusRepository.findOne(probationStatus.getId());
        updatedProbationStatus
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS);

        restProbationStatusMockMvc.perform(put("/api/probation-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProbationStatus)))
            .andExpect(status().isOk());

        // Validate the ProbationStatus in the database
        List<ProbationStatus> probationStatusList = probationStatusRepository.findAll();
        assertThat(probationStatusList).hasSize(databaseSizeBeforeUpdate);
        ProbationStatus testProbationStatus = probationStatusList.get(probationStatusList.size() - 1);
        assertThat(testProbationStatus.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testProbationStatus.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the ProbationStatus in Elasticsearch
        ProbationStatus probationStatusEs = probationStatusSearchRepository.findOne(testProbationStatus.getId());
        assertThat(probationStatusEs).isEqualToComparingFieldByField(testProbationStatus);
    }

    @Test
    @Transactional
    public void updateNonExistingProbationStatus() throws Exception {
        int databaseSizeBeforeUpdate = probationStatusRepository.findAll().size();

        // Create the ProbationStatus

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProbationStatusMockMvc.perform(put("/api/probation-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(probationStatus)))
            .andExpect(status().isCreated());

        // Validate the ProbationStatus in the database
        List<ProbationStatus> probationStatusList = probationStatusRepository.findAll();
        assertThat(probationStatusList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProbationStatus() throws Exception {
        // Initialize the database
        probationStatusService.save(probationStatus);

        int databaseSizeBeforeDelete = probationStatusRepository.findAll().size();

        // Get the probationStatus
        restProbationStatusMockMvc.perform(delete("/api/probation-statuses/{id}", probationStatus.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean probationStatusExistsInEs = probationStatusSearchRepository.exists(probationStatus.getId());
        assertThat(probationStatusExistsInEs).isFalse();

        // Validate the database is empty
        List<ProbationStatus> probationStatusList = probationStatusRepository.findAll();
        assertThat(probationStatusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProbationStatus() throws Exception {
        // Initialize the database
        probationStatusService.save(probationStatus);

        // Search the probationStatus
        restProbationStatusMockMvc.perform(get("/api/_search/probation-statuses?query=id:" + probationStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(probationStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(sameInstant(DEFAULT_END_DATE))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProbationStatus.class);
        ProbationStatus probationStatus1 = new ProbationStatus();
        probationStatus1.setId(1L);
        ProbationStatus probationStatus2 = new ProbationStatus();
        probationStatus2.setId(probationStatus1.getId());
        assertThat(probationStatus1).isEqualTo(probationStatus2);
        probationStatus2.setId(2L);
        assertThat(probationStatus1).isNotEqualTo(probationStatus2);
        probationStatus1.setId(null);
        assertThat(probationStatus1).isNotEqualTo(probationStatus2);
    }
}
