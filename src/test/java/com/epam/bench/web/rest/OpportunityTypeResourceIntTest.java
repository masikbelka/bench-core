package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.OpportunityType;
import com.epam.bench.repository.OpportunityTypeRepository;
import com.epam.bench.service.OpportunityTypeService;
import com.epam.bench.repository.search.OpportunityTypeSearchRepository;
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
 * Test class for the OpportunityTypeResource REST controller.
 *
 * @see OpportunityTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class OpportunityTypeResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private OpportunityTypeRepository opportunityTypeRepository;

    @Autowired
    private OpportunityTypeService opportunityTypeService;

    @Autowired
    private OpportunityTypeSearchRepository opportunityTypeSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOpportunityTypeMockMvc;

    private OpportunityType opportunityType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OpportunityTypeResource opportunityTypeResource = new OpportunityTypeResource(opportunityTypeService);
        this.restOpportunityTypeMockMvc = MockMvcBuilders.standaloneSetup(opportunityTypeResource)
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
    public static OpportunityType createEntity(EntityManager em) {
        OpportunityType opportunityType = new OpportunityType()
            .name(DEFAULT_NAME);
        return opportunityType;
    }

    @Before
    public void initTest() {
        opportunityTypeSearchRepository.deleteAll();
        opportunityType = createEntity(em);
    }

    @Test
    @Transactional
    public void createOpportunityType() throws Exception {
        int databaseSizeBeforeCreate = opportunityTypeRepository.findAll().size();

        // Create the OpportunityType
        restOpportunityTypeMockMvc.perform(post("/api/opportunity-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunityType)))
            .andExpect(status().isCreated());

        // Validate the OpportunityType in the database
        List<OpportunityType> opportunityTypeList = opportunityTypeRepository.findAll();
        assertThat(opportunityTypeList).hasSize(databaseSizeBeforeCreate + 1);
        OpportunityType testOpportunityType = opportunityTypeList.get(opportunityTypeList.size() - 1);
        assertThat(testOpportunityType.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the OpportunityType in Elasticsearch
        OpportunityType opportunityTypeEs = opportunityTypeSearchRepository.findOne(testOpportunityType.getId());
        assertThat(opportunityTypeEs).isEqualToComparingFieldByField(testOpportunityType);
    }

    @Test
    @Transactional
    public void createOpportunityTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = opportunityTypeRepository.findAll().size();

        // Create the OpportunityType with an existing ID
        opportunityType.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOpportunityTypeMockMvc.perform(post("/api/opportunity-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunityType)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<OpportunityType> opportunityTypeList = opportunityTypeRepository.findAll();
        assertThat(opportunityTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = opportunityTypeRepository.findAll().size();
        // set the field null
        opportunityType.setName(null);

        // Create the OpportunityType, which fails.

        restOpportunityTypeMockMvc.perform(post("/api/opportunity-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunityType)))
            .andExpect(status().isBadRequest());

        List<OpportunityType> opportunityTypeList = opportunityTypeRepository.findAll();
        assertThat(opportunityTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOpportunityTypes() throws Exception {
        // Initialize the database
        opportunityTypeRepository.saveAndFlush(opportunityType);

        // Get all the opportunityTypeList
        restOpportunityTypeMockMvc.perform(get("/api/opportunity-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(opportunityType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getOpportunityType() throws Exception {
        // Initialize the database
        opportunityTypeRepository.saveAndFlush(opportunityType);

        // Get the opportunityType
        restOpportunityTypeMockMvc.perform(get("/api/opportunity-types/{id}", opportunityType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(opportunityType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOpportunityType() throws Exception {
        // Get the opportunityType
        restOpportunityTypeMockMvc.perform(get("/api/opportunity-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOpportunityType() throws Exception {
        // Initialize the database
        opportunityTypeService.save(opportunityType);

        int databaseSizeBeforeUpdate = opportunityTypeRepository.findAll().size();

        // Update the opportunityType
        OpportunityType updatedOpportunityType = opportunityTypeRepository.findOne(opportunityType.getId());
        updatedOpportunityType
            .name(UPDATED_NAME);

        restOpportunityTypeMockMvc.perform(put("/api/opportunity-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOpportunityType)))
            .andExpect(status().isOk());

        // Validate the OpportunityType in the database
        List<OpportunityType> opportunityTypeList = opportunityTypeRepository.findAll();
        assertThat(opportunityTypeList).hasSize(databaseSizeBeforeUpdate);
        OpportunityType testOpportunityType = opportunityTypeList.get(opportunityTypeList.size() - 1);
        assertThat(testOpportunityType.getName()).isEqualTo(UPDATED_NAME);

        // Validate the OpportunityType in Elasticsearch
        OpportunityType opportunityTypeEs = opportunityTypeSearchRepository.findOne(testOpportunityType.getId());
        assertThat(opportunityTypeEs).isEqualToComparingFieldByField(testOpportunityType);
    }

    @Test
    @Transactional
    public void updateNonExistingOpportunityType() throws Exception {
        int databaseSizeBeforeUpdate = opportunityTypeRepository.findAll().size();

        // Create the OpportunityType

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOpportunityTypeMockMvc.perform(put("/api/opportunity-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunityType)))
            .andExpect(status().isCreated());

        // Validate the OpportunityType in the database
        List<OpportunityType> opportunityTypeList = opportunityTypeRepository.findAll();
        assertThat(opportunityTypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOpportunityType() throws Exception {
        // Initialize the database
        opportunityTypeService.save(opportunityType);

        int databaseSizeBeforeDelete = opportunityTypeRepository.findAll().size();

        // Get the opportunityType
        restOpportunityTypeMockMvc.perform(delete("/api/opportunity-types/{id}", opportunityType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean opportunityTypeExistsInEs = opportunityTypeSearchRepository.exists(opportunityType.getId());
        assertThat(opportunityTypeExistsInEs).isFalse();

        // Validate the database is empty
        List<OpportunityType> opportunityTypeList = opportunityTypeRepository.findAll();
        assertThat(opportunityTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOpportunityType() throws Exception {
        // Initialize the database
        opportunityTypeService.save(opportunityType);

        // Search the opportunityType
        restOpportunityTypeMockMvc.perform(get("/api/_search/opportunity-types?query=id:" + opportunityType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(opportunityType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OpportunityType.class);
        OpportunityType opportunityType1 = new OpportunityType();
        opportunityType1.setId(1L);
        OpportunityType opportunityType2 = new OpportunityType();
        opportunityType2.setId(opportunityType1.getId());
        assertThat(opportunityType1).isEqualTo(opportunityType2);
        opportunityType2.setId(2L);
        assertThat(opportunityType1).isNotEqualTo(opportunityType2);
        opportunityType1.setId(null);
        assertThat(opportunityType1).isNotEqualTo(opportunityType2);
    }
}
