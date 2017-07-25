package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.OpportunityPosition;
import com.epam.bench.repository.OpportunityPositionRepository;
import com.epam.bench.service.OpportunityPositionService;
import com.epam.bench.repository.search.OpportunityPositionSearchRepository;
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

import com.epam.bench.domain.enumeration.PositionStatus;
/**
 * Test class for the OpportunityPositionResource REST controller.
 *
 * @see OpportunityPositionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class OpportunityPositionResourceIntTest {

    private static final LocalDate DEFAULT_CREATED_TIME = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED_TIME = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_EMPLOYEE_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_EMPLOYEE_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_FULL_NAME = "BBBBBBBBBB";

    private static final PositionStatus DEFAULT_STATUS = PositionStatus.CREATED;
    private static final PositionStatus UPDATED_STATUS = PositionStatus.CLOSED;

    @Autowired
    private OpportunityPositionRepository opportunityPositionRepository;

    @Autowired
    private OpportunityPositionService opportunityPositionService;

    @Autowired
    private OpportunityPositionSearchRepository opportunityPositionSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOpportunityPositionMockMvc;

    private OpportunityPosition opportunityPosition;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OpportunityPositionResource opportunityPositionResource = new OpportunityPositionResource(opportunityPositionService);
        this.restOpportunityPositionMockMvc = MockMvcBuilders.standaloneSetup(opportunityPositionResource)
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
    public static OpportunityPosition createEntity(EntityManager em) {
        OpportunityPosition opportunityPosition = new OpportunityPosition()
            .createdTime(DEFAULT_CREATED_TIME)
            .employeeUpsaId(DEFAULT_EMPLOYEE_UPSA_ID)
            .employeeFullName(DEFAULT_EMPLOYEE_FULL_NAME)
            .ownerUpsaId(DEFAULT_OWNER_UPSA_ID)
            .ownerFullName(DEFAULT_OWNER_FULL_NAME)
            .status(DEFAULT_STATUS);
        return opportunityPosition;
    }

    @Before
    public void initTest() {
        opportunityPositionSearchRepository.deleteAll();
        opportunityPosition = createEntity(em);
    }

    @Test
    @Transactional
    public void createOpportunityPosition() throws Exception {
        int databaseSizeBeforeCreate = opportunityPositionRepository.findAll().size();

        // Create the OpportunityPosition
        restOpportunityPositionMockMvc.perform(post("/api/opportunity-positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunityPosition)))
            .andExpect(status().isCreated());

        // Validate the OpportunityPosition in the database
        List<OpportunityPosition> opportunityPositionList = opportunityPositionRepository.findAll();
        assertThat(opportunityPositionList).hasSize(databaseSizeBeforeCreate + 1);
        OpportunityPosition testOpportunityPosition = opportunityPositionList.get(opportunityPositionList.size() - 1);
        assertThat(testOpportunityPosition.getCreatedTime()).isEqualTo(DEFAULT_CREATED_TIME);
        assertThat(testOpportunityPosition.getEmployeeUpsaId()).isEqualTo(DEFAULT_EMPLOYEE_UPSA_ID);
        assertThat(testOpportunityPosition.getEmployeeFullName()).isEqualTo(DEFAULT_EMPLOYEE_FULL_NAME);
        assertThat(testOpportunityPosition.getOwnerUpsaId()).isEqualTo(DEFAULT_OWNER_UPSA_ID);
        assertThat(testOpportunityPosition.getOwnerFullName()).isEqualTo(DEFAULT_OWNER_FULL_NAME);
        assertThat(testOpportunityPosition.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the OpportunityPosition in Elasticsearch
        OpportunityPosition opportunityPositionEs = opportunityPositionSearchRepository.findOne(testOpportunityPosition.getId());
        assertThat(opportunityPositionEs).isEqualToComparingFieldByField(testOpportunityPosition);
    }

    @Test
    @Transactional
    public void createOpportunityPositionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = opportunityPositionRepository.findAll().size();

        // Create the OpportunityPosition with an existing ID
        opportunityPosition.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOpportunityPositionMockMvc.perform(post("/api/opportunity-positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunityPosition)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<OpportunityPosition> opportunityPositionList = opportunityPositionRepository.findAll();
        assertThat(opportunityPositionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCreatedTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = opportunityPositionRepository.findAll().size();
        // set the field null
        opportunityPosition.setCreatedTime(null);

        // Create the OpportunityPosition, which fails.

        restOpportunityPositionMockMvc.perform(post("/api/opportunity-positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunityPosition)))
            .andExpect(status().isBadRequest());

        List<OpportunityPosition> opportunityPositionList = opportunityPositionRepository.findAll();
        assertThat(opportunityPositionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOwnerUpsaIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = opportunityPositionRepository.findAll().size();
        // set the field null
        opportunityPosition.setOwnerUpsaId(null);

        // Create the OpportunityPosition, which fails.

        restOpportunityPositionMockMvc.perform(post("/api/opportunity-positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunityPosition)))
            .andExpect(status().isBadRequest());

        List<OpportunityPosition> opportunityPositionList = opportunityPositionRepository.findAll();
        assertThat(opportunityPositionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOwnerFullNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = opportunityPositionRepository.findAll().size();
        // set the field null
        opportunityPosition.setOwnerFullName(null);

        // Create the OpportunityPosition, which fails.

        restOpportunityPositionMockMvc.perform(post("/api/opportunity-positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunityPosition)))
            .andExpect(status().isBadRequest());

        List<OpportunityPosition> opportunityPositionList = opportunityPositionRepository.findAll();
        assertThat(opportunityPositionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOpportunityPositions() throws Exception {
        // Initialize the database
        opportunityPositionRepository.saveAndFlush(opportunityPosition);

        // Get all the opportunityPositionList
        restOpportunityPositionMockMvc.perform(get("/api/opportunity-positions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(opportunityPosition.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].employeeUpsaId").value(hasItem(DEFAULT_EMPLOYEE_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].employeeFullName").value(hasItem(DEFAULT_EMPLOYEE_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].ownerUpsaId").value(hasItem(DEFAULT_OWNER_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].ownerFullName").value(hasItem(DEFAULT_OWNER_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getOpportunityPosition() throws Exception {
        // Initialize the database
        opportunityPositionRepository.saveAndFlush(opportunityPosition);

        // Get the opportunityPosition
        restOpportunityPositionMockMvc.perform(get("/api/opportunity-positions/{id}", opportunityPosition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(opportunityPosition.getId().intValue()))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME.toString()))
            .andExpect(jsonPath("$.employeeUpsaId").value(DEFAULT_EMPLOYEE_UPSA_ID.toString()))
            .andExpect(jsonPath("$.employeeFullName").value(DEFAULT_EMPLOYEE_FULL_NAME.toString()))
            .andExpect(jsonPath("$.ownerUpsaId").value(DEFAULT_OWNER_UPSA_ID.toString()))
            .andExpect(jsonPath("$.ownerFullName").value(DEFAULT_OWNER_FULL_NAME.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOpportunityPosition() throws Exception {
        // Get the opportunityPosition
        restOpportunityPositionMockMvc.perform(get("/api/opportunity-positions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOpportunityPosition() throws Exception {
        // Initialize the database
        opportunityPositionService.save(opportunityPosition);

        int databaseSizeBeforeUpdate = opportunityPositionRepository.findAll().size();

        // Update the opportunityPosition
        OpportunityPosition updatedOpportunityPosition = opportunityPositionRepository.findOne(opportunityPosition.getId());
        updatedOpportunityPosition
            .createdTime(UPDATED_CREATED_TIME)
            .employeeUpsaId(UPDATED_EMPLOYEE_UPSA_ID)
            .employeeFullName(UPDATED_EMPLOYEE_FULL_NAME)
            .ownerUpsaId(UPDATED_OWNER_UPSA_ID)
            .ownerFullName(UPDATED_OWNER_FULL_NAME)
            .status(UPDATED_STATUS);

        restOpportunityPositionMockMvc.perform(put("/api/opportunity-positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOpportunityPosition)))
            .andExpect(status().isOk());

        // Validate the OpportunityPosition in the database
        List<OpportunityPosition> opportunityPositionList = opportunityPositionRepository.findAll();
        assertThat(opportunityPositionList).hasSize(databaseSizeBeforeUpdate);
        OpportunityPosition testOpportunityPosition = opportunityPositionList.get(opportunityPositionList.size() - 1);
        assertThat(testOpportunityPosition.getCreatedTime()).isEqualTo(UPDATED_CREATED_TIME);
        assertThat(testOpportunityPosition.getEmployeeUpsaId()).isEqualTo(UPDATED_EMPLOYEE_UPSA_ID);
        assertThat(testOpportunityPosition.getEmployeeFullName()).isEqualTo(UPDATED_EMPLOYEE_FULL_NAME);
        assertThat(testOpportunityPosition.getOwnerUpsaId()).isEqualTo(UPDATED_OWNER_UPSA_ID);
        assertThat(testOpportunityPosition.getOwnerFullName()).isEqualTo(UPDATED_OWNER_FULL_NAME);
        assertThat(testOpportunityPosition.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the OpportunityPosition in Elasticsearch
        OpportunityPosition opportunityPositionEs = opportunityPositionSearchRepository.findOne(testOpportunityPosition.getId());
        assertThat(opportunityPositionEs).isEqualToComparingFieldByField(testOpportunityPosition);
    }

    @Test
    @Transactional
    public void updateNonExistingOpportunityPosition() throws Exception {
        int databaseSizeBeforeUpdate = opportunityPositionRepository.findAll().size();

        // Create the OpportunityPosition

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOpportunityPositionMockMvc.perform(put("/api/opportunity-positions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunityPosition)))
            .andExpect(status().isCreated());

        // Validate the OpportunityPosition in the database
        List<OpportunityPosition> opportunityPositionList = opportunityPositionRepository.findAll();
        assertThat(opportunityPositionList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOpportunityPosition() throws Exception {
        // Initialize the database
        opportunityPositionService.save(opportunityPosition);

        int databaseSizeBeforeDelete = opportunityPositionRepository.findAll().size();

        // Get the opportunityPosition
        restOpportunityPositionMockMvc.perform(delete("/api/opportunity-positions/{id}", opportunityPosition.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean opportunityPositionExistsInEs = opportunityPositionSearchRepository.exists(opportunityPosition.getId());
        assertThat(opportunityPositionExistsInEs).isFalse();

        // Validate the database is empty
        List<OpportunityPosition> opportunityPositionList = opportunityPositionRepository.findAll();
        assertThat(opportunityPositionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOpportunityPosition() throws Exception {
        // Initialize the database
        opportunityPositionService.save(opportunityPosition);

        // Search the opportunityPosition
        restOpportunityPositionMockMvc.perform(get("/api/_search/opportunity-positions?query=id:" + opportunityPosition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(opportunityPosition.getId().intValue())))
            .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME.toString())))
            .andExpect(jsonPath("$.[*].employeeUpsaId").value(hasItem(DEFAULT_EMPLOYEE_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].employeeFullName").value(hasItem(DEFAULT_EMPLOYEE_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].ownerUpsaId").value(hasItem(DEFAULT_OWNER_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].ownerFullName").value(hasItem(DEFAULT_OWNER_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OpportunityPosition.class);
        OpportunityPosition opportunityPosition1 = new OpportunityPosition();
        opportunityPosition1.setId(1L);
        OpportunityPosition opportunityPosition2 = new OpportunityPosition();
        opportunityPosition2.setId(opportunityPosition1.getId());
        assertThat(opportunityPosition1).isEqualTo(opportunityPosition2);
        opportunityPosition2.setId(2L);
        assertThat(opportunityPosition1).isNotEqualTo(opportunityPosition2);
        opportunityPosition1.setId(null);
        assertThat(opportunityPosition1).isNotEqualTo(opportunityPosition2);
    }
}
