package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.Opportunity;
import com.epam.bench.repository.OpportunityRepository;
import com.epam.bench.service.OpportunityService;
import com.epam.bench.repository.search.OpportunitySearchRepository;
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

import com.epam.bench.domain.enumeration.OpportunityStatus;
/**
 * Test class for the OpportunityResource REST controller.
 *
 * @see OpportunityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class OpportunityResourceIntTest {

    private static final String DEFAULT_STAFFING_ID = "AAAAAAAAAA";
    private static final String UPDATED_STAFFING_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_FULL_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final OpportunityStatus DEFAULT_STATUS = OpportunityStatus.CREATED;
    private static final OpportunityStatus UPDATED_STATUS = OpportunityStatus.CLOSED;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_STAFFING_COORDINATIOR_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_STAFFING_COORDINATIOR_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_STAFFING_COORDINATIOR_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_STAFFING_COORDINATIOR_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSIBLE_MANAGER_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSIBLE_MANAGER_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSIBLE_MANAGER_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSIBLE_MANAGER_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SUPERVISOR_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_SUPERVISOR_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SUPERVISOR_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SUPERVISOR_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DELIVERY_MANAGER_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_MANAGER_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_DELIVERY_MANAGER_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DELIVERY_MANAGER_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ACCOUNT_MANAGER_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_MANAGER_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_ACCOUNT_MANAGER_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ACCOUNT_MANAGER_FULL_NAME = "BBBBBBBBBB";

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private OpportunityService opportunityService;

    @Autowired
    private OpportunitySearchRepository opportunitySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOpportunityMockMvc;

    private Opportunity opportunity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OpportunityResource opportunityResource = new OpportunityResource(opportunityService);
        this.restOpportunityMockMvc = MockMvcBuilders.standaloneSetup(opportunityResource)
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
    public static Opportunity createEntity(EntityManager em) {
        Opportunity opportunity = new Opportunity()
            .staffingId(DEFAULT_STAFFING_ID)
            .name(DEFAULT_NAME)
            .ownerUpsaId(DEFAULT_OWNER_UPSA_ID)
            .ownerFullName(DEFAULT_OWNER_FULL_NAME)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .status(DEFAULT_STATUS)
            .description(DEFAULT_DESCRIPTION)
            .staffingCoordinatiorUpsaId(DEFAULT_STAFFING_COORDINATIOR_UPSA_ID)
            .staffingCoordinatiorFullName(DEFAULT_STAFFING_COORDINATIOR_FULL_NAME)
            .responsibleManagerUpsaId(DEFAULT_RESPONSIBLE_MANAGER_UPSA_ID)
            .responsibleManagerFullName(DEFAULT_RESPONSIBLE_MANAGER_FULL_NAME)
            .supervisorUpsaId(DEFAULT_SUPERVISOR_UPSA_ID)
            .supervisorFullName(DEFAULT_SUPERVISOR_FULL_NAME)
            .deliveryManagerUpsaId(DEFAULT_DELIVERY_MANAGER_UPSA_ID)
            .deliveryManagerFullName(DEFAULT_DELIVERY_MANAGER_FULL_NAME)
            .accountManagerUpsaId(DEFAULT_ACCOUNT_MANAGER_UPSA_ID)
            .accountManagerFullName(DEFAULT_ACCOUNT_MANAGER_FULL_NAME);
        return opportunity;
    }

    @Before
    public void initTest() {
        opportunitySearchRepository.deleteAll();
        opportunity = createEntity(em);
    }

    @Test
    @Transactional
    public void createOpportunity() throws Exception {
        int databaseSizeBeforeCreate = opportunityRepository.findAll().size();

        // Create the Opportunity
        restOpportunityMockMvc.perform(post("/api/opportunities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunity)))
            .andExpect(status().isCreated());

        // Validate the Opportunity in the database
        List<Opportunity> opportunityList = opportunityRepository.findAll();
        assertThat(opportunityList).hasSize(databaseSizeBeforeCreate + 1);
        Opportunity testOpportunity = opportunityList.get(opportunityList.size() - 1);
        assertThat(testOpportunity.getStaffingId()).isEqualTo(DEFAULT_STAFFING_ID);
        assertThat(testOpportunity.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOpportunity.getOwnerUpsaId()).isEqualTo(DEFAULT_OWNER_UPSA_ID);
        assertThat(testOpportunity.getOwnerFullName()).isEqualTo(DEFAULT_OWNER_FULL_NAME);
        assertThat(testOpportunity.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testOpportunity.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testOpportunity.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOpportunity.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testOpportunity.getStaffingCoordinatiorUpsaId()).isEqualTo(DEFAULT_STAFFING_COORDINATIOR_UPSA_ID);
        assertThat(testOpportunity.getStaffingCoordinatiorFullName()).isEqualTo(DEFAULT_STAFFING_COORDINATIOR_FULL_NAME);
        assertThat(testOpportunity.getResponsibleManagerUpsaId()).isEqualTo(DEFAULT_RESPONSIBLE_MANAGER_UPSA_ID);
        assertThat(testOpportunity.getResponsibleManagerFullName()).isEqualTo(DEFAULT_RESPONSIBLE_MANAGER_FULL_NAME);
        assertThat(testOpportunity.getSupervisorUpsaId()).isEqualTo(DEFAULT_SUPERVISOR_UPSA_ID);
        assertThat(testOpportunity.getSupervisorFullName()).isEqualTo(DEFAULT_SUPERVISOR_FULL_NAME);
        assertThat(testOpportunity.getDeliveryManagerUpsaId()).isEqualTo(DEFAULT_DELIVERY_MANAGER_UPSA_ID);
        assertThat(testOpportunity.getDeliveryManagerFullName()).isEqualTo(DEFAULT_DELIVERY_MANAGER_FULL_NAME);
        assertThat(testOpportunity.getAccountManagerUpsaId()).isEqualTo(DEFAULT_ACCOUNT_MANAGER_UPSA_ID);
        assertThat(testOpportunity.getAccountManagerFullName()).isEqualTo(DEFAULT_ACCOUNT_MANAGER_FULL_NAME);

        // Validate the Opportunity in Elasticsearch
        Opportunity opportunityEs = opportunitySearchRepository.findOne(testOpportunity.getId());
        assertThat(opportunityEs).isEqualToComparingFieldByField(testOpportunity);
    }

    @Test
    @Transactional
    public void createOpportunityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = opportunityRepository.findAll().size();

        // Create the Opportunity with an existing ID
        opportunity.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOpportunityMockMvc.perform(post("/api/opportunities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunity)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Opportunity> opportunityList = opportunityRepository.findAll();
        assertThat(opportunityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllOpportunities() throws Exception {
        // Initialize the database
        opportunityRepository.saveAndFlush(opportunity);

        // Get all the opportunityList
        restOpportunityMockMvc.perform(get("/api/opportunities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(opportunity.getId().intValue())))
            .andExpect(jsonPath("$.[*].staffingId").value(hasItem(DEFAULT_STAFFING_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].ownerUpsaId").value(hasItem(DEFAULT_OWNER_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].ownerFullName").value(hasItem(DEFAULT_OWNER_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].staffingCoordinatiorUpsaId").value(hasItem(DEFAULT_STAFFING_COORDINATIOR_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].staffingCoordinatiorFullName").value(hasItem(DEFAULT_STAFFING_COORDINATIOR_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].responsibleManagerUpsaId").value(hasItem(DEFAULT_RESPONSIBLE_MANAGER_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].responsibleManagerFullName").value(hasItem(DEFAULT_RESPONSIBLE_MANAGER_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].supervisorUpsaId").value(hasItem(DEFAULT_SUPERVISOR_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].supervisorFullName").value(hasItem(DEFAULT_SUPERVISOR_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].deliveryManagerUpsaId").value(hasItem(DEFAULT_DELIVERY_MANAGER_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].deliveryManagerFullName").value(hasItem(DEFAULT_DELIVERY_MANAGER_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].accountManagerUpsaId").value(hasItem(DEFAULT_ACCOUNT_MANAGER_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].accountManagerFullName").value(hasItem(DEFAULT_ACCOUNT_MANAGER_FULL_NAME.toString())));
    }

    @Test
    @Transactional
    public void getOpportunity() throws Exception {
        // Initialize the database
        opportunityRepository.saveAndFlush(opportunity);

        // Get the opportunity
        restOpportunityMockMvc.perform(get("/api/opportunities/{id}", opportunity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(opportunity.getId().intValue()))
            .andExpect(jsonPath("$.staffingId").value(DEFAULT_STAFFING_ID.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.ownerUpsaId").value(DEFAULT_OWNER_UPSA_ID.toString()))
            .andExpect(jsonPath("$.ownerFullName").value(DEFAULT_OWNER_FULL_NAME.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.staffingCoordinatiorUpsaId").value(DEFAULT_STAFFING_COORDINATIOR_UPSA_ID.toString()))
            .andExpect(jsonPath("$.staffingCoordinatiorFullName").value(DEFAULT_STAFFING_COORDINATIOR_FULL_NAME.toString()))
            .andExpect(jsonPath("$.responsibleManagerUpsaId").value(DEFAULT_RESPONSIBLE_MANAGER_UPSA_ID.toString()))
            .andExpect(jsonPath("$.responsibleManagerFullName").value(DEFAULT_RESPONSIBLE_MANAGER_FULL_NAME.toString()))
            .andExpect(jsonPath("$.supervisorUpsaId").value(DEFAULT_SUPERVISOR_UPSA_ID.toString()))
            .andExpect(jsonPath("$.supervisorFullName").value(DEFAULT_SUPERVISOR_FULL_NAME.toString()))
            .andExpect(jsonPath("$.deliveryManagerUpsaId").value(DEFAULT_DELIVERY_MANAGER_UPSA_ID.toString()))
            .andExpect(jsonPath("$.deliveryManagerFullName").value(DEFAULT_DELIVERY_MANAGER_FULL_NAME.toString()))
            .andExpect(jsonPath("$.accountManagerUpsaId").value(DEFAULT_ACCOUNT_MANAGER_UPSA_ID.toString()))
            .andExpect(jsonPath("$.accountManagerFullName").value(DEFAULT_ACCOUNT_MANAGER_FULL_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOpportunity() throws Exception {
        // Get the opportunity
        restOpportunityMockMvc.perform(get("/api/opportunities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOpportunity() throws Exception {
        // Initialize the database
        opportunityService.save(opportunity);

        int databaseSizeBeforeUpdate = opportunityRepository.findAll().size();

        // Update the opportunity
        Opportunity updatedOpportunity = opportunityRepository.findOne(opportunity.getId());
        updatedOpportunity
            .staffingId(UPDATED_STAFFING_ID)
            .name(UPDATED_NAME)
            .ownerUpsaId(UPDATED_OWNER_UPSA_ID)
            .ownerFullName(UPDATED_OWNER_FULL_NAME)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .description(UPDATED_DESCRIPTION)
            .staffingCoordinatiorUpsaId(UPDATED_STAFFING_COORDINATIOR_UPSA_ID)
            .staffingCoordinatiorFullName(UPDATED_STAFFING_COORDINATIOR_FULL_NAME)
            .responsibleManagerUpsaId(UPDATED_RESPONSIBLE_MANAGER_UPSA_ID)
            .responsibleManagerFullName(UPDATED_RESPONSIBLE_MANAGER_FULL_NAME)
            .supervisorUpsaId(UPDATED_SUPERVISOR_UPSA_ID)
            .supervisorFullName(UPDATED_SUPERVISOR_FULL_NAME)
            .deliveryManagerUpsaId(UPDATED_DELIVERY_MANAGER_UPSA_ID)
            .deliveryManagerFullName(UPDATED_DELIVERY_MANAGER_FULL_NAME)
            .accountManagerUpsaId(UPDATED_ACCOUNT_MANAGER_UPSA_ID)
            .accountManagerFullName(UPDATED_ACCOUNT_MANAGER_FULL_NAME);

        restOpportunityMockMvc.perform(put("/api/opportunities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOpportunity)))
            .andExpect(status().isOk());

        // Validate the Opportunity in the database
        List<Opportunity> opportunityList = opportunityRepository.findAll();
        assertThat(opportunityList).hasSize(databaseSizeBeforeUpdate);
        Opportunity testOpportunity = opportunityList.get(opportunityList.size() - 1);
        assertThat(testOpportunity.getStaffingId()).isEqualTo(UPDATED_STAFFING_ID);
        assertThat(testOpportunity.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOpportunity.getOwnerUpsaId()).isEqualTo(UPDATED_OWNER_UPSA_ID);
        assertThat(testOpportunity.getOwnerFullName()).isEqualTo(UPDATED_OWNER_FULL_NAME);
        assertThat(testOpportunity.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testOpportunity.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testOpportunity.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOpportunity.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testOpportunity.getStaffingCoordinatiorUpsaId()).isEqualTo(UPDATED_STAFFING_COORDINATIOR_UPSA_ID);
        assertThat(testOpportunity.getStaffingCoordinatiorFullName()).isEqualTo(UPDATED_STAFFING_COORDINATIOR_FULL_NAME);
        assertThat(testOpportunity.getResponsibleManagerUpsaId()).isEqualTo(UPDATED_RESPONSIBLE_MANAGER_UPSA_ID);
        assertThat(testOpportunity.getResponsibleManagerFullName()).isEqualTo(UPDATED_RESPONSIBLE_MANAGER_FULL_NAME);
        assertThat(testOpportunity.getSupervisorUpsaId()).isEqualTo(UPDATED_SUPERVISOR_UPSA_ID);
        assertThat(testOpportunity.getSupervisorFullName()).isEqualTo(UPDATED_SUPERVISOR_FULL_NAME);
        assertThat(testOpportunity.getDeliveryManagerUpsaId()).isEqualTo(UPDATED_DELIVERY_MANAGER_UPSA_ID);
        assertThat(testOpportunity.getDeliveryManagerFullName()).isEqualTo(UPDATED_DELIVERY_MANAGER_FULL_NAME);
        assertThat(testOpportunity.getAccountManagerUpsaId()).isEqualTo(UPDATED_ACCOUNT_MANAGER_UPSA_ID);
        assertThat(testOpportunity.getAccountManagerFullName()).isEqualTo(UPDATED_ACCOUNT_MANAGER_FULL_NAME);

        // Validate the Opportunity in Elasticsearch
        Opportunity opportunityEs = opportunitySearchRepository.findOne(testOpportunity.getId());
        assertThat(opportunityEs).isEqualToComparingFieldByField(testOpportunity);
    }

    @Test
    @Transactional
    public void updateNonExistingOpportunity() throws Exception {
        int databaseSizeBeforeUpdate = opportunityRepository.findAll().size();

        // Create the Opportunity

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOpportunityMockMvc.perform(put("/api/opportunities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(opportunity)))
            .andExpect(status().isCreated());

        // Validate the Opportunity in the database
        List<Opportunity> opportunityList = opportunityRepository.findAll();
        assertThat(opportunityList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOpportunity() throws Exception {
        // Initialize the database
        opportunityService.save(opportunity);

        int databaseSizeBeforeDelete = opportunityRepository.findAll().size();

        // Get the opportunity
        restOpportunityMockMvc.perform(delete("/api/opportunities/{id}", opportunity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean opportunityExistsInEs = opportunitySearchRepository.exists(opportunity.getId());
        assertThat(opportunityExistsInEs).isFalse();

        // Validate the database is empty
        List<Opportunity> opportunityList = opportunityRepository.findAll();
        assertThat(opportunityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOpportunity() throws Exception {
        // Initialize the database
        opportunityService.save(opportunity);

        // Search the opportunity
        restOpportunityMockMvc.perform(get("/api/_search/opportunities?query=id:" + opportunity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(opportunity.getId().intValue())))
            .andExpect(jsonPath("$.[*].staffingId").value(hasItem(DEFAULT_STAFFING_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].ownerUpsaId").value(hasItem(DEFAULT_OWNER_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].ownerFullName").value(hasItem(DEFAULT_OWNER_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].staffingCoordinatiorUpsaId").value(hasItem(DEFAULT_STAFFING_COORDINATIOR_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].staffingCoordinatiorFullName").value(hasItem(DEFAULT_STAFFING_COORDINATIOR_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].responsibleManagerUpsaId").value(hasItem(DEFAULT_RESPONSIBLE_MANAGER_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].responsibleManagerFullName").value(hasItem(DEFAULT_RESPONSIBLE_MANAGER_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].supervisorUpsaId").value(hasItem(DEFAULT_SUPERVISOR_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].supervisorFullName").value(hasItem(DEFAULT_SUPERVISOR_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].deliveryManagerUpsaId").value(hasItem(DEFAULT_DELIVERY_MANAGER_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].deliveryManagerFullName").value(hasItem(DEFAULT_DELIVERY_MANAGER_FULL_NAME.toString())))
            .andExpect(jsonPath("$.[*].accountManagerUpsaId").value(hasItem(DEFAULT_ACCOUNT_MANAGER_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].accountManagerFullName").value(hasItem(DEFAULT_ACCOUNT_MANAGER_FULL_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Opportunity.class);
        Opportunity opportunity1 = new Opportunity();
        opportunity1.setId(1L);
        Opportunity opportunity2 = new Opportunity();
        opportunity2.setId(opportunity1.getId());
        assertThat(opportunity1).isEqualTo(opportunity2);
        opportunity2.setId(2L);
        assertThat(opportunity1).isNotEqualTo(opportunity2);
        opportunity1.setId(null);
        assertThat(opportunity1).isNotEqualTo(opportunity2);
    }
}
