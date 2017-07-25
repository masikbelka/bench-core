package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.ProductionStatus;
import com.epam.bench.repository.ProductionStatusRepository;
import com.epam.bench.service.ProductionStatusService;
import com.epam.bench.repository.search.ProductionStatusSearchRepository;
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
 * Test class for the ProductionStatusResource REST controller.
 *
 * @see ProductionStatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class ProductionStatusResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private ProductionStatusRepository productionStatusRepository;

    @Autowired
    private ProductionStatusService productionStatusService;

    @Autowired
    private ProductionStatusSearchRepository productionStatusSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restProductionStatusMockMvc;

    private ProductionStatus productionStatus;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProductionStatusResource productionStatusResource = new ProductionStatusResource(productionStatusService);
        this.restProductionStatusMockMvc = MockMvcBuilders.standaloneSetup(productionStatusResource)
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
    public static ProductionStatus createEntity(EntityManager em) {
        ProductionStatus productionStatus = new ProductionStatus()
            .name(DEFAULT_NAME);
        return productionStatus;
    }

    @Before
    public void initTest() {
        productionStatusSearchRepository.deleteAll();
        productionStatus = createEntity(em);
    }

    @Test
    @Transactional
    public void createProductionStatus() throws Exception {
        int databaseSizeBeforeCreate = productionStatusRepository.findAll().size();

        // Create the ProductionStatus
        restProductionStatusMockMvc.perform(post("/api/production-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productionStatus)))
            .andExpect(status().isCreated());

        // Validate the ProductionStatus in the database
        List<ProductionStatus> productionStatusList = productionStatusRepository.findAll();
        assertThat(productionStatusList).hasSize(databaseSizeBeforeCreate + 1);
        ProductionStatus testProductionStatus = productionStatusList.get(productionStatusList.size() - 1);
        assertThat(testProductionStatus.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the ProductionStatus in Elasticsearch
        ProductionStatus productionStatusEs = productionStatusSearchRepository.findOne(testProductionStatus.getId());
        assertThat(productionStatusEs).isEqualToComparingFieldByField(testProductionStatus);
    }

    @Test
    @Transactional
    public void createProductionStatusWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productionStatusRepository.findAll().size();

        // Create the ProductionStatus with an existing ID
        productionStatus.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductionStatusMockMvc.perform(post("/api/production-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productionStatus)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ProductionStatus> productionStatusList = productionStatusRepository.findAll();
        assertThat(productionStatusList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productionStatusRepository.findAll().size();
        // set the field null
        productionStatus.setName(null);

        // Create the ProductionStatus, which fails.

        restProductionStatusMockMvc.perform(post("/api/production-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productionStatus)))
            .andExpect(status().isBadRequest());

        List<ProductionStatus> productionStatusList = productionStatusRepository.findAll();
        assertThat(productionStatusList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProductionStatuses() throws Exception {
        // Initialize the database
        productionStatusRepository.saveAndFlush(productionStatus);

        // Get all the productionStatusList
        restProductionStatusMockMvc.perform(get("/api/production-statuses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productionStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getProductionStatus() throws Exception {
        // Initialize the database
        productionStatusRepository.saveAndFlush(productionStatus);

        // Get the productionStatus
        restProductionStatusMockMvc.perform(get("/api/production-statuses/{id}", productionStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(productionStatus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProductionStatus() throws Exception {
        // Get the productionStatus
        restProductionStatusMockMvc.perform(get("/api/production-statuses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductionStatus() throws Exception {
        // Initialize the database
        productionStatusService.save(productionStatus);

        int databaseSizeBeforeUpdate = productionStatusRepository.findAll().size();

        // Update the productionStatus
        ProductionStatus updatedProductionStatus = productionStatusRepository.findOne(productionStatus.getId());
        updatedProductionStatus
            .name(UPDATED_NAME);

        restProductionStatusMockMvc.perform(put("/api/production-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProductionStatus)))
            .andExpect(status().isOk());

        // Validate the ProductionStatus in the database
        List<ProductionStatus> productionStatusList = productionStatusRepository.findAll();
        assertThat(productionStatusList).hasSize(databaseSizeBeforeUpdate);
        ProductionStatus testProductionStatus = productionStatusList.get(productionStatusList.size() - 1);
        assertThat(testProductionStatus.getName()).isEqualTo(UPDATED_NAME);

        // Validate the ProductionStatus in Elasticsearch
        ProductionStatus productionStatusEs = productionStatusSearchRepository.findOne(testProductionStatus.getId());
        assertThat(productionStatusEs).isEqualToComparingFieldByField(testProductionStatus);
    }

    @Test
    @Transactional
    public void updateNonExistingProductionStatus() throws Exception {
        int databaseSizeBeforeUpdate = productionStatusRepository.findAll().size();

        // Create the ProductionStatus

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProductionStatusMockMvc.perform(put("/api/production-statuses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productionStatus)))
            .andExpect(status().isCreated());

        // Validate the ProductionStatus in the database
        List<ProductionStatus> productionStatusList = productionStatusRepository.findAll();
        assertThat(productionStatusList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProductionStatus() throws Exception {
        // Initialize the database
        productionStatusService.save(productionStatus);

        int databaseSizeBeforeDelete = productionStatusRepository.findAll().size();

        // Get the productionStatus
        restProductionStatusMockMvc.perform(delete("/api/production-statuses/{id}", productionStatus.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean productionStatusExistsInEs = productionStatusSearchRepository.exists(productionStatus.getId());
        assertThat(productionStatusExistsInEs).isFalse();

        // Validate the database is empty
        List<ProductionStatus> productionStatusList = productionStatusRepository.findAll();
        assertThat(productionStatusList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProductionStatus() throws Exception {
        // Initialize the database
        productionStatusService.save(productionStatus);

        // Search the productionStatus
        restProductionStatusMockMvc.perform(get("/api/_search/production-statuses?query=id:" + productionStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productionStatus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductionStatus.class);
        ProductionStatus productionStatus1 = new ProductionStatus();
        productionStatus1.setId(1L);
        ProductionStatus productionStatus2 = new ProductionStatus();
        productionStatus2.setId(productionStatus1.getId());
        assertThat(productionStatus1).isEqualTo(productionStatus2);
        productionStatus2.setId(2L);
        assertThat(productionStatus1).isNotEqualTo(productionStatus2);
        productionStatus1.setId(null);
        assertThat(productionStatus1).isNotEqualTo(productionStatus2);
    }
}
