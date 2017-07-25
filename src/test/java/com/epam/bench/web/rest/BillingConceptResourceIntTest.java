package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.BillingConcept;
import com.epam.bench.repository.BillingConceptRepository;
import com.epam.bench.service.BillingConceptService;
import com.epam.bench.repository.search.BillingConceptSearchRepository;
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
 * Test class for the BillingConceptResource REST controller.
 *
 * @see BillingConceptResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class BillingConceptResourceIntTest {

    private static final String DEFAULT_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private BillingConceptRepository billingConceptRepository;

    @Autowired
    private BillingConceptService billingConceptService;

    @Autowired
    private BillingConceptSearchRepository billingConceptSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBillingConceptMockMvc;

    private BillingConcept billingConcept;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BillingConceptResource billingConceptResource = new BillingConceptResource(billingConceptService);
        this.restBillingConceptMockMvc = MockMvcBuilders.standaloneSetup(billingConceptResource)
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
    public static BillingConcept createEntity(EntityManager em) {
        BillingConcept billingConcept = new BillingConcept()
            .upsaId(DEFAULT_UPSA_ID)
            .name(DEFAULT_NAME);
        return billingConcept;
    }

    @Before
    public void initTest() {
        billingConceptSearchRepository.deleteAll();
        billingConcept = createEntity(em);
    }

    @Test
    @Transactional
    public void createBillingConcept() throws Exception {
        int databaseSizeBeforeCreate = billingConceptRepository.findAll().size();

        // Create the BillingConcept
        restBillingConceptMockMvc.perform(post("/api/billing-concepts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(billingConcept)))
            .andExpect(status().isCreated());

        // Validate the BillingConcept in the database
        List<BillingConcept> billingConceptList = billingConceptRepository.findAll();
        assertThat(billingConceptList).hasSize(databaseSizeBeforeCreate + 1);
        BillingConcept testBillingConcept = billingConceptList.get(billingConceptList.size() - 1);
        assertThat(testBillingConcept.getUpsaId()).isEqualTo(DEFAULT_UPSA_ID);
        assertThat(testBillingConcept.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the BillingConcept in Elasticsearch
        BillingConcept billingConceptEs = billingConceptSearchRepository.findOne(testBillingConcept.getId());
        assertThat(billingConceptEs).isEqualToComparingFieldByField(testBillingConcept);
    }

    @Test
    @Transactional
    public void createBillingConceptWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = billingConceptRepository.findAll().size();

        // Create the BillingConcept with an existing ID
        billingConcept.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBillingConceptMockMvc.perform(post("/api/billing-concepts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(billingConcept)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<BillingConcept> billingConceptList = billingConceptRepository.findAll();
        assertThat(billingConceptList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUpsaIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = billingConceptRepository.findAll().size();
        // set the field null
        billingConcept.setUpsaId(null);

        // Create the BillingConcept, which fails.

        restBillingConceptMockMvc.perform(post("/api/billing-concepts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(billingConcept)))
            .andExpect(status().isBadRequest());

        List<BillingConcept> billingConceptList = billingConceptRepository.findAll();
        assertThat(billingConceptList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBillingConcepts() throws Exception {
        // Initialize the database
        billingConceptRepository.saveAndFlush(billingConcept);

        // Get all the billingConceptList
        restBillingConceptMockMvc.perform(get("/api/billing-concepts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(billingConcept.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getBillingConcept() throws Exception {
        // Initialize the database
        billingConceptRepository.saveAndFlush(billingConcept);

        // Get the billingConcept
        restBillingConceptMockMvc.perform(get("/api/billing-concepts/{id}", billingConcept.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(billingConcept.getId().intValue()))
            .andExpect(jsonPath("$.upsaId").value(DEFAULT_UPSA_ID.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBillingConcept() throws Exception {
        // Get the billingConcept
        restBillingConceptMockMvc.perform(get("/api/billing-concepts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBillingConcept() throws Exception {
        // Initialize the database
        billingConceptService.save(billingConcept);

        int databaseSizeBeforeUpdate = billingConceptRepository.findAll().size();

        // Update the billingConcept
        BillingConcept updatedBillingConcept = billingConceptRepository.findOne(billingConcept.getId());
        updatedBillingConcept
            .upsaId(UPDATED_UPSA_ID)
            .name(UPDATED_NAME);

        restBillingConceptMockMvc.perform(put("/api/billing-concepts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBillingConcept)))
            .andExpect(status().isOk());

        // Validate the BillingConcept in the database
        List<BillingConcept> billingConceptList = billingConceptRepository.findAll();
        assertThat(billingConceptList).hasSize(databaseSizeBeforeUpdate);
        BillingConcept testBillingConcept = billingConceptList.get(billingConceptList.size() - 1);
        assertThat(testBillingConcept.getUpsaId()).isEqualTo(UPDATED_UPSA_ID);
        assertThat(testBillingConcept.getName()).isEqualTo(UPDATED_NAME);

        // Validate the BillingConcept in Elasticsearch
        BillingConcept billingConceptEs = billingConceptSearchRepository.findOne(testBillingConcept.getId());
        assertThat(billingConceptEs).isEqualToComparingFieldByField(testBillingConcept);
    }

    @Test
    @Transactional
    public void updateNonExistingBillingConcept() throws Exception {
        int databaseSizeBeforeUpdate = billingConceptRepository.findAll().size();

        // Create the BillingConcept

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBillingConceptMockMvc.perform(put("/api/billing-concepts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(billingConcept)))
            .andExpect(status().isCreated());

        // Validate the BillingConcept in the database
        List<BillingConcept> billingConceptList = billingConceptRepository.findAll();
        assertThat(billingConceptList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBillingConcept() throws Exception {
        // Initialize the database
        billingConceptService.save(billingConcept);

        int databaseSizeBeforeDelete = billingConceptRepository.findAll().size();

        // Get the billingConcept
        restBillingConceptMockMvc.perform(delete("/api/billing-concepts/{id}", billingConcept.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean billingConceptExistsInEs = billingConceptSearchRepository.exists(billingConcept.getId());
        assertThat(billingConceptExistsInEs).isFalse();

        // Validate the database is empty
        List<BillingConcept> billingConceptList = billingConceptRepository.findAll();
        assertThat(billingConceptList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBillingConcept() throws Exception {
        // Initialize the database
        billingConceptService.save(billingConcept);

        // Search the billingConcept
        restBillingConceptMockMvc.perform(get("/api/_search/billing-concepts?query=id:" + billingConcept.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(billingConcept.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BillingConcept.class);
        BillingConcept billingConcept1 = new BillingConcept();
        billingConcept1.setId(1L);
        BillingConcept billingConcept2 = new BillingConcept();
        billingConcept2.setId(billingConcept1.getId());
        assertThat(billingConcept1).isEqualTo(billingConcept2);
        billingConcept2.setId(2L);
        assertThat(billingConcept1).isNotEqualTo(billingConcept2);
        billingConcept1.setId(null);
        assertThat(billingConcept1).isNotEqualTo(billingConcept2);
    }
}
