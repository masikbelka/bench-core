package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.PrimarySkill;
import com.epam.bench.repository.PrimarySkillRepository;
import com.epam.bench.service.PrimarySkillService;
import com.epam.bench.repository.search.PrimarySkillSearchRepository;
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
 * Test class for the PrimarySkillResource REST controller.
 *
 * @see PrimarySkillResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class PrimarySkillResourceIntTest {

    private static final String DEFAULT_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private PrimarySkillRepository primarySkillRepository;

    @Autowired
    private PrimarySkillService primarySkillService;

    @Autowired
    private PrimarySkillSearchRepository primarySkillSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPrimarySkillMockMvc;

    private PrimarySkill primarySkill;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PrimarySkillResource primarySkillResource = new PrimarySkillResource(primarySkillService);
        this.restPrimarySkillMockMvc = MockMvcBuilders.standaloneSetup(primarySkillResource)
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
    public static PrimarySkill createEntity(EntityManager em) {
        PrimarySkill primarySkill = new PrimarySkill()
            .upsaId(DEFAULT_UPSA_ID)
            .name(DEFAULT_NAME);
        return primarySkill;
    }

    @Before
    public void initTest() {
        primarySkillSearchRepository.deleteAll();
        primarySkill = createEntity(em);
    }

    @Test
    @Transactional
    public void createPrimarySkill() throws Exception {
        int databaseSizeBeforeCreate = primarySkillRepository.findAll().size();

        // Create the PrimarySkill
        restPrimarySkillMockMvc.perform(post("/api/primary-skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(primarySkill)))
            .andExpect(status().isCreated());

        // Validate the PrimarySkill in the database
        List<PrimarySkill> primarySkillList = primarySkillRepository.findAll();
        assertThat(primarySkillList).hasSize(databaseSizeBeforeCreate + 1);
        PrimarySkill testPrimarySkill = primarySkillList.get(primarySkillList.size() - 1);
        assertThat(testPrimarySkill.getUpsaId()).isEqualTo(DEFAULT_UPSA_ID);
        assertThat(testPrimarySkill.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the PrimarySkill in Elasticsearch
        PrimarySkill primarySkillEs = primarySkillSearchRepository.findOne(testPrimarySkill.getId());
        assertThat(primarySkillEs).isEqualToComparingFieldByField(testPrimarySkill);
    }

    @Test
    @Transactional
    public void createPrimarySkillWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = primarySkillRepository.findAll().size();

        // Create the PrimarySkill with an existing ID
        primarySkill.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrimarySkillMockMvc.perform(post("/api/primary-skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(primarySkill)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<PrimarySkill> primarySkillList = primarySkillRepository.findAll();
        assertThat(primarySkillList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUpsaIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = primarySkillRepository.findAll().size();
        // set the field null
        primarySkill.setUpsaId(null);

        // Create the PrimarySkill, which fails.

        restPrimarySkillMockMvc.perform(post("/api/primary-skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(primarySkill)))
            .andExpect(status().isBadRequest());

        List<PrimarySkill> primarySkillList = primarySkillRepository.findAll();
        assertThat(primarySkillList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = primarySkillRepository.findAll().size();
        // set the field null
        primarySkill.setName(null);

        // Create the PrimarySkill, which fails.

        restPrimarySkillMockMvc.perform(post("/api/primary-skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(primarySkill)))
            .andExpect(status().isBadRequest());

        List<PrimarySkill> primarySkillList = primarySkillRepository.findAll();
        assertThat(primarySkillList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPrimarySkills() throws Exception {
        // Initialize the database
        primarySkillRepository.saveAndFlush(primarySkill);

        // Get all the primarySkillList
        restPrimarySkillMockMvc.perform(get("/api/primary-skills?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(primarySkill.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getPrimarySkill() throws Exception {
        // Initialize the database
        primarySkillRepository.saveAndFlush(primarySkill);

        // Get the primarySkill
        restPrimarySkillMockMvc.perform(get("/api/primary-skills/{id}", primarySkill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(primarySkill.getId().intValue()))
            .andExpect(jsonPath("$.upsaId").value(DEFAULT_UPSA_ID.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPrimarySkill() throws Exception {
        // Get the primarySkill
        restPrimarySkillMockMvc.perform(get("/api/primary-skills/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePrimarySkill() throws Exception {
        // Initialize the database
        primarySkillService.save(primarySkill);

        int databaseSizeBeforeUpdate = primarySkillRepository.findAll().size();

        // Update the primarySkill
        PrimarySkill updatedPrimarySkill = primarySkillRepository.findOne(primarySkill.getId());
        updatedPrimarySkill
            .upsaId(UPDATED_UPSA_ID)
            .name(UPDATED_NAME);

        restPrimarySkillMockMvc.perform(put("/api/primary-skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPrimarySkill)))
            .andExpect(status().isOk());

        // Validate the PrimarySkill in the database
        List<PrimarySkill> primarySkillList = primarySkillRepository.findAll();
        assertThat(primarySkillList).hasSize(databaseSizeBeforeUpdate);
        PrimarySkill testPrimarySkill = primarySkillList.get(primarySkillList.size() - 1);
        assertThat(testPrimarySkill.getUpsaId()).isEqualTo(UPDATED_UPSA_ID);
        assertThat(testPrimarySkill.getName()).isEqualTo(UPDATED_NAME);

        // Validate the PrimarySkill in Elasticsearch
        PrimarySkill primarySkillEs = primarySkillSearchRepository.findOne(testPrimarySkill.getId());
        assertThat(primarySkillEs).isEqualToComparingFieldByField(testPrimarySkill);
    }

    @Test
    @Transactional
    public void updateNonExistingPrimarySkill() throws Exception {
        int databaseSizeBeforeUpdate = primarySkillRepository.findAll().size();

        // Create the PrimarySkill

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPrimarySkillMockMvc.perform(put("/api/primary-skills")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(primarySkill)))
            .andExpect(status().isCreated());

        // Validate the PrimarySkill in the database
        List<PrimarySkill> primarySkillList = primarySkillRepository.findAll();
        assertThat(primarySkillList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePrimarySkill() throws Exception {
        // Initialize the database
        primarySkillService.save(primarySkill);

        int databaseSizeBeforeDelete = primarySkillRepository.findAll().size();

        // Get the primarySkill
        restPrimarySkillMockMvc.perform(delete("/api/primary-skills/{id}", primarySkill.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean primarySkillExistsInEs = primarySkillSearchRepository.exists(primarySkill.getId());
        assertThat(primarySkillExistsInEs).isFalse();

        // Validate the database is empty
        List<PrimarySkill> primarySkillList = primarySkillRepository.findAll();
        assertThat(primarySkillList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchPrimarySkill() throws Exception {
        // Initialize the database
        primarySkillService.save(primarySkill);

        // Search the primarySkill
        restPrimarySkillMockMvc.perform(get("/api/_search/primary-skills?query=id:" + primarySkill.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(primarySkill.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PrimarySkill.class);
        PrimarySkill primarySkill1 = new PrimarySkill();
        primarySkill1.setId(1L);
        PrimarySkill primarySkill2 = new PrimarySkill();
        primarySkill2.setId(primarySkill1.getId());
        assertThat(primarySkill1).isEqualTo(primarySkill2);
        primarySkill2.setId(2L);
        assertThat(primarySkill1).isNotEqualTo(primarySkill2);
        primarySkill1.setId(null);
        assertThat(primarySkill1).isNotEqualTo(primarySkill2);
    }
}
