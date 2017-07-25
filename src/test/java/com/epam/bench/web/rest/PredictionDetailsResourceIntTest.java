package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.PredictionDetails;
import com.epam.bench.repository.PredictionDetailsRepository;
import com.epam.bench.service.PredictionDetailsService;
import com.epam.bench.repository.search.PredictionDetailsSearchRepository;
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

/**
 * Test class for the PredictionDetailsResource REST controller.
 *
 * @see PredictionDetailsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class PredictionDetailsResourceIntTest {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_ASSIGNED_TO_PROJECT = false;
    private static final Boolean UPDATED_ASSIGNED_TO_PROJECT = true;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Boolean DEFAULT_REMOVED_FROM_PROJECT = false;
    private static final Boolean UPDATED_REMOVED_FROM_PROJECT = true;

    private static final Boolean DEFAULT_MATERNITY_LEAVE = false;
    private static final Boolean UPDATED_MATERNITY_LEAVE = true;

    @Autowired
    private PredictionDetailsRepository predictionDetailsRepository;

    @Autowired
    private PredictionDetailsService predictionDetailsService;

    @Autowired
    private PredictionDetailsSearchRepository predictionDetailsSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPredictionDetailsMockMvc;

    private PredictionDetails predictionDetails;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PredictionDetailsResource predictionDetailsResource = new PredictionDetailsResource(predictionDetailsService);
        this.restPredictionDetailsMockMvc = MockMvcBuilders.standaloneSetup(predictionDetailsResource)
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
    public static PredictionDetails createEntity(EntityManager em) {
        PredictionDetails predictionDetails = new PredictionDetails()
            .date(DEFAULT_DATE)
            .assignedToProject(DEFAULT_ASSIGNED_TO_PROJECT)
            .active(DEFAULT_ACTIVE)
            .removedFromProject(DEFAULT_REMOVED_FROM_PROJECT)
            .maternityLeave(DEFAULT_MATERNITY_LEAVE);
        return predictionDetails;
    }

    @Before
    public void initTest() {
        predictionDetailsSearchRepository.deleteAll();
        predictionDetails = createEntity(em);
    }

    @Test
    @Transactional
    public void createPredictionDetails() throws Exception {
        int databaseSizeBeforeCreate = predictionDetailsRepository.findAll().size();

        // Create the PredictionDetails
        restPredictionDetailsMockMvc.perform(post("/api/prediction-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(predictionDetails)))
            .andExpect(status().isCreated());

        // Validate the PredictionDetails in the database
        List<PredictionDetails> predictionDetailsList = predictionDetailsRepository.findAll();
        assertThat(predictionDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        PredictionDetails testPredictionDetails = predictionDetailsList.get(predictionDetailsList.size() - 1);
        assertThat(testPredictionDetails.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testPredictionDetails.isAssignedToProject()).isEqualTo(DEFAULT_ASSIGNED_TO_PROJECT);
        assertThat(testPredictionDetails.isActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testPredictionDetails.isRemovedFromProject()).isEqualTo(DEFAULT_REMOVED_FROM_PROJECT);
        assertThat(testPredictionDetails.isMaternityLeave()).isEqualTo(DEFAULT_MATERNITY_LEAVE);

        // Validate the PredictionDetails in Elasticsearch
        PredictionDetails predictionDetailsEs = predictionDetailsSearchRepository.findOne(testPredictionDetails.getId());
        assertThat(predictionDetailsEs).isEqualToComparingFieldByField(testPredictionDetails);
    }

    @Test
    @Transactional
    public void createPredictionDetailsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = predictionDetailsRepository.findAll().size();

        // Create the PredictionDetails with an existing ID
        predictionDetails.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPredictionDetailsMockMvc.perform(post("/api/prediction-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(predictionDetails)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<PredictionDetails> predictionDetailsList = predictionDetailsRepository.findAll();
        assertThat(predictionDetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkAssignedToProjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = predictionDetailsRepository.findAll().size();
        // set the field null
        predictionDetails.setAssignedToProject(null);

        // Create the PredictionDetails, which fails.

        restPredictionDetailsMockMvc.perform(post("/api/prediction-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(predictionDetails)))
            .andExpect(status().isBadRequest());

        List<PredictionDetails> predictionDetailsList = predictionDetailsRepository.findAll();
        assertThat(predictionDetailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = predictionDetailsRepository.findAll().size();
        // set the field null
        predictionDetails.setActive(null);

        // Create the PredictionDetails, which fails.

        restPredictionDetailsMockMvc.perform(post("/api/prediction-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(predictionDetails)))
            .andExpect(status().isBadRequest());

        List<PredictionDetails> predictionDetailsList = predictionDetailsRepository.findAll();
        assertThat(predictionDetailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRemovedFromProjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = predictionDetailsRepository.findAll().size();
        // set the field null
        predictionDetails.setRemovedFromProject(null);

        // Create the PredictionDetails, which fails.

        restPredictionDetailsMockMvc.perform(post("/api/prediction-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(predictionDetails)))
            .andExpect(status().isBadRequest());

        List<PredictionDetails> predictionDetailsList = predictionDetailsRepository.findAll();
        assertThat(predictionDetailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMaternityLeaveIsRequired() throws Exception {
        int databaseSizeBeforeTest = predictionDetailsRepository.findAll().size();
        // set the field null
        predictionDetails.setMaternityLeave(null);

        // Create the PredictionDetails, which fails.

        restPredictionDetailsMockMvc.perform(post("/api/prediction-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(predictionDetails)))
            .andExpect(status().isBadRequest());

        List<PredictionDetails> predictionDetailsList = predictionDetailsRepository.findAll();
        assertThat(predictionDetailsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPredictionDetails() throws Exception {
        // Initialize the database
        predictionDetailsRepository.saveAndFlush(predictionDetails);

        // Get all the predictionDetailsList
        restPredictionDetailsMockMvc.perform(get("/api/prediction-details?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(predictionDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].assignedToProject").value(hasItem(DEFAULT_ASSIGNED_TO_PROJECT.booleanValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].removedFromProject").value(hasItem(DEFAULT_REMOVED_FROM_PROJECT.booleanValue())))
            .andExpect(jsonPath("$.[*].maternityLeave").value(hasItem(DEFAULT_MATERNITY_LEAVE.booleanValue())));
    }

    @Test
    @Transactional
    public void getPredictionDetails() throws Exception {
        // Initialize the database
        predictionDetailsRepository.saveAndFlush(predictionDetails);

        // Get the predictionDetails
        restPredictionDetailsMockMvc.perform(get("/api/prediction-details/{id}", predictionDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(predictionDetails.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.assignedToProject").value(DEFAULT_ASSIGNED_TO_PROJECT.booleanValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.removedFromProject").value(DEFAULT_REMOVED_FROM_PROJECT.booleanValue()))
            .andExpect(jsonPath("$.maternityLeave").value(DEFAULT_MATERNITY_LEAVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPredictionDetails() throws Exception {
        // Get the predictionDetails
        restPredictionDetailsMockMvc.perform(get("/api/prediction-details/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePredictionDetails() throws Exception {
        // Initialize the database
        predictionDetailsService.save(predictionDetails);

        int databaseSizeBeforeUpdate = predictionDetailsRepository.findAll().size();

        // Update the predictionDetails
        PredictionDetails updatedPredictionDetails = predictionDetailsRepository.findOne(predictionDetails.getId());
        updatedPredictionDetails
            .date(UPDATED_DATE)
            .assignedToProject(UPDATED_ASSIGNED_TO_PROJECT)
            .active(UPDATED_ACTIVE)
            .removedFromProject(UPDATED_REMOVED_FROM_PROJECT)
            .maternityLeave(UPDATED_MATERNITY_LEAVE);

        restPredictionDetailsMockMvc.perform(put("/api/prediction-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPredictionDetails)))
            .andExpect(status().isOk());

        // Validate the PredictionDetails in the database
        List<PredictionDetails> predictionDetailsList = predictionDetailsRepository.findAll();
        assertThat(predictionDetailsList).hasSize(databaseSizeBeforeUpdate);
        PredictionDetails testPredictionDetails = predictionDetailsList.get(predictionDetailsList.size() - 1);
        assertThat(testPredictionDetails.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testPredictionDetails.isAssignedToProject()).isEqualTo(UPDATED_ASSIGNED_TO_PROJECT);
        assertThat(testPredictionDetails.isActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testPredictionDetails.isRemovedFromProject()).isEqualTo(UPDATED_REMOVED_FROM_PROJECT);
        assertThat(testPredictionDetails.isMaternityLeave()).isEqualTo(UPDATED_MATERNITY_LEAVE);

        // Validate the PredictionDetails in Elasticsearch
        PredictionDetails predictionDetailsEs = predictionDetailsSearchRepository.findOne(testPredictionDetails.getId());
        assertThat(predictionDetailsEs).isEqualToComparingFieldByField(testPredictionDetails);
    }

    @Test
    @Transactional
    public void updateNonExistingPredictionDetails() throws Exception {
        int databaseSizeBeforeUpdate = predictionDetailsRepository.findAll().size();

        // Create the PredictionDetails

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPredictionDetailsMockMvc.perform(put("/api/prediction-details")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(predictionDetails)))
            .andExpect(status().isCreated());

        // Validate the PredictionDetails in the database
        List<PredictionDetails> predictionDetailsList = predictionDetailsRepository.findAll();
        assertThat(predictionDetailsList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePredictionDetails() throws Exception {
        // Initialize the database
        predictionDetailsService.save(predictionDetails);

        int databaseSizeBeforeDelete = predictionDetailsRepository.findAll().size();

        // Get the predictionDetails
        restPredictionDetailsMockMvc.perform(delete("/api/prediction-details/{id}", predictionDetails.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean predictionDetailsExistsInEs = predictionDetailsSearchRepository.exists(predictionDetails.getId());
        assertThat(predictionDetailsExistsInEs).isFalse();

        // Validate the database is empty
        List<PredictionDetails> predictionDetailsList = predictionDetailsRepository.findAll();
        assertThat(predictionDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPredictionDetails() throws Exception {
        // Initialize the database
        predictionDetailsService.save(predictionDetails);

        // Search the predictionDetails
        restPredictionDetailsMockMvc.perform(get("/api/_search/prediction-details?query=id:" + predictionDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(predictionDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].assignedToProject").value(hasItem(DEFAULT_ASSIGNED_TO_PROJECT.booleanValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].removedFromProject").value(hasItem(DEFAULT_REMOVED_FROM_PROJECT.booleanValue())))
            .andExpect(jsonPath("$.[*].maternityLeave").value(hasItem(DEFAULT_MATERNITY_LEAVE.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PredictionDetails.class);
        PredictionDetails predictionDetails1 = new PredictionDetails();
        predictionDetails1.setId(1L);
        PredictionDetails predictionDetails2 = new PredictionDetails();
        predictionDetails2.setId(predictionDetails1.getId());
        assertThat(predictionDetails1).isEqualTo(predictionDetails2);
        predictionDetails2.setId(2L);
        assertThat(predictionDetails1).isNotEqualTo(predictionDetails2);
        predictionDetails1.setId(null);
        assertThat(predictionDetails1).isNotEqualTo(predictionDetails2);
    }
}
