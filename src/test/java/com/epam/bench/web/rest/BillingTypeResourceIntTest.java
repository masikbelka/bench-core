package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.BillingType;
import com.epam.bench.repository.BillingTypeRepository;
import com.epam.bench.service.BillingTypeService;
import com.epam.bench.repository.search.BillingTypeSearchRepository;
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
 * Test class for the BillingTypeResource REST controller.
 *
 * @see BillingTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class BillingTypeResourceIntTest {

    private static final String DEFAULT_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private BillingTypeRepository billingTypeRepository;

    @Autowired
    private BillingTypeService billingTypeService;

    @Autowired
    private BillingTypeSearchRepository billingTypeSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBillingTypeMockMvc;

    private BillingType billingType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BillingTypeResource billingTypeResource = new BillingTypeResource(billingTypeService);
        this.restBillingTypeMockMvc = MockMvcBuilders.standaloneSetup(billingTypeResource)
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
    public static BillingType createEntity(EntityManager em) {
        BillingType billingType = new BillingType()
            .upsaId(DEFAULT_UPSA_ID)
            .name(DEFAULT_NAME);
        return billingType;
    }

    @Before
    public void initTest() {
        billingTypeSearchRepository.deleteAll();
        billingType = createEntity(em);
    }

    @Test
    @Transactional
    public void createBillingType() throws Exception {
        int databaseSizeBeforeCreate = billingTypeRepository.findAll().size();

        // Create the BillingType
        restBillingTypeMockMvc.perform(post("/api/billing-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(billingType)))
            .andExpect(status().isCreated());

        // Validate the BillingType in the database
        List<BillingType> billingTypeList = billingTypeRepository.findAll();
        assertThat(billingTypeList).hasSize(databaseSizeBeforeCreate + 1);
        BillingType testBillingType = billingTypeList.get(billingTypeList.size() - 1);
        assertThat(testBillingType.getUpsaId()).isEqualTo(DEFAULT_UPSA_ID);
        assertThat(testBillingType.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the BillingType in Elasticsearch
        BillingType billingTypeEs = billingTypeSearchRepository.findOne(testBillingType.getId());
        assertThat(billingTypeEs).isEqualToComparingFieldByField(testBillingType);
    }

    @Test
    @Transactional
    public void createBillingTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = billingTypeRepository.findAll().size();

        // Create the BillingType with an existing ID
        billingType.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBillingTypeMockMvc.perform(post("/api/billing-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(billingType)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<BillingType> billingTypeList = billingTypeRepository.findAll();
        assertThat(billingTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUpsaIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = billingTypeRepository.findAll().size();
        // set the field null
        billingType.setUpsaId(null);

        // Create the BillingType, which fails.

        restBillingTypeMockMvc.perform(post("/api/billing-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(billingType)))
            .andExpect(status().isBadRequest());

        List<BillingType> billingTypeList = billingTypeRepository.findAll();
        assertThat(billingTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBillingTypes() throws Exception {
        // Initialize the database
        billingTypeRepository.saveAndFlush(billingType);

        // Get all the billingTypeList
        restBillingTypeMockMvc.perform(get("/api/billing-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(billingType.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getBillingType() throws Exception {
        // Initialize the database
        billingTypeRepository.saveAndFlush(billingType);

        // Get the billingType
        restBillingTypeMockMvc.perform(get("/api/billing-types/{id}", billingType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(billingType.getId().intValue()))
            .andExpect(jsonPath("$.upsaId").value(DEFAULT_UPSA_ID.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBillingType() throws Exception {
        // Get the billingType
        restBillingTypeMockMvc.perform(get("/api/billing-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBillingType() throws Exception {
        // Initialize the database
        billingTypeService.save(billingType);

        int databaseSizeBeforeUpdate = billingTypeRepository.findAll().size();

        // Update the billingType
        BillingType updatedBillingType = billingTypeRepository.findOne(billingType.getId());
        updatedBillingType
            .upsaId(UPDATED_UPSA_ID)
            .name(UPDATED_NAME);

        restBillingTypeMockMvc.perform(put("/api/billing-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBillingType)))
            .andExpect(status().isOk());

        // Validate the BillingType in the database
        List<BillingType> billingTypeList = billingTypeRepository.findAll();
        assertThat(billingTypeList).hasSize(databaseSizeBeforeUpdate);
        BillingType testBillingType = billingTypeList.get(billingTypeList.size() - 1);
        assertThat(testBillingType.getUpsaId()).isEqualTo(UPDATED_UPSA_ID);
        assertThat(testBillingType.getName()).isEqualTo(UPDATED_NAME);

        // Validate the BillingType in Elasticsearch
        BillingType billingTypeEs = billingTypeSearchRepository.findOne(testBillingType.getId());
        assertThat(billingTypeEs).isEqualToComparingFieldByField(testBillingType);
    }

    @Test
    @Transactional
    public void updateNonExistingBillingType() throws Exception {
        int databaseSizeBeforeUpdate = billingTypeRepository.findAll().size();

        // Create the BillingType

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBillingTypeMockMvc.perform(put("/api/billing-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(billingType)))
            .andExpect(status().isCreated());

        // Validate the BillingType in the database
        List<BillingType> billingTypeList = billingTypeRepository.findAll();
        assertThat(billingTypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBillingType() throws Exception {
        // Initialize the database
        billingTypeService.save(billingType);

        int databaseSizeBeforeDelete = billingTypeRepository.findAll().size();

        // Get the billingType
        restBillingTypeMockMvc.perform(delete("/api/billing-types/{id}", billingType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean billingTypeExistsInEs = billingTypeSearchRepository.exists(billingType.getId());
        assertThat(billingTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<BillingType> billingTypeList = billingTypeRepository.findAll();
        assertThat(billingTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBillingType() throws Exception {
        // Initialize the database
        billingTypeService.save(billingType);

        // Search the billingType
        restBillingTypeMockMvc.perform(get("/api/_search/billing-types?query=id:" + billingType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(billingType.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BillingType.class);
        BillingType billingType1 = new BillingType();
        billingType1.setId(1L);
        BillingType billingType2 = new BillingType();
        billingType2.setId(billingType1.getId());
        assertThat(billingType1).isEqualTo(billingType2);
        billingType2.setId(2L);
        assertThat(billingType1).isNotEqualTo(billingType2);
        billingType1.setId(null);
        assertThat(billingType1).isNotEqualTo(billingType2);
    }
}
