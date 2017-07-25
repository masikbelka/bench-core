package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.ProjectRole;
import com.epam.bench.repository.ProjectRoleRepository;
import com.epam.bench.service.ProjectRoleService;
import com.epam.bench.repository.search.ProjectRoleSearchRepository;
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
 * Test class for the ProjectRoleResource REST controller.
 *
 * @see ProjectRoleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class ProjectRoleResourceIntTest {

    private static final String DEFAULT_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private ProjectRoleRepository projectRoleRepository;

    @Autowired
    private ProjectRoleService projectRoleService;

    @Autowired
    private ProjectRoleSearchRepository projectRoleSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restProjectRoleMockMvc;

    private ProjectRole projectRole;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectRoleResource projectRoleResource = new ProjectRoleResource(projectRoleService);
        this.restProjectRoleMockMvc = MockMvcBuilders.standaloneSetup(projectRoleResource)
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
    public static ProjectRole createEntity(EntityManager em) {
        ProjectRole projectRole = new ProjectRole()
            .upsaId(DEFAULT_UPSA_ID)
            .name(DEFAULT_NAME);
        return projectRole;
    }

    @Before
    public void initTest() {
        projectRoleSearchRepository.deleteAll();
        projectRole = createEntity(em);
    }

    @Test
    @Transactional
    public void createProjectRole() throws Exception {
        int databaseSizeBeforeCreate = projectRoleRepository.findAll().size();

        // Create the ProjectRole
        restProjectRoleMockMvc.perform(post("/api/project-roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectRole)))
            .andExpect(status().isCreated());

        // Validate the ProjectRole in the database
        List<ProjectRole> projectRoleList = projectRoleRepository.findAll();
        assertThat(projectRoleList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectRole testProjectRole = projectRoleList.get(projectRoleList.size() - 1);
        assertThat(testProjectRole.getUpsaId()).isEqualTo(DEFAULT_UPSA_ID);
        assertThat(testProjectRole.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the ProjectRole in Elasticsearch
        ProjectRole projectRoleEs = projectRoleSearchRepository.findOne(testProjectRole.getId());
        assertThat(projectRoleEs).isEqualToComparingFieldByField(testProjectRole);
    }

    @Test
    @Transactional
    public void createProjectRoleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = projectRoleRepository.findAll().size();

        // Create the ProjectRole with an existing ID
        projectRole.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectRoleMockMvc.perform(post("/api/project-roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectRole)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ProjectRole> projectRoleList = projectRoleRepository.findAll();
        assertThat(projectRoleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUpsaIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectRoleRepository.findAll().size();
        // set the field null
        projectRole.setUpsaId(null);

        // Create the ProjectRole, which fails.

        restProjectRoleMockMvc.perform(post("/api/project-roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectRole)))
            .andExpect(status().isBadRequest());

        List<ProjectRole> projectRoleList = projectRoleRepository.findAll();
        assertThat(projectRoleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProjectRoles() throws Exception {
        // Initialize the database
        projectRoleRepository.saveAndFlush(projectRole);

        // Get all the projectRoleList
        restProjectRoleMockMvc.perform(get("/api/project-roles?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectRole.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getProjectRole() throws Exception {
        // Initialize the database
        projectRoleRepository.saveAndFlush(projectRole);

        // Get the projectRole
        restProjectRoleMockMvc.perform(get("/api/project-roles/{id}", projectRole.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(projectRole.getId().intValue()))
            .andExpect(jsonPath("$.upsaId").value(DEFAULT_UPSA_ID.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProjectRole() throws Exception {
        // Get the projectRole
        restProjectRoleMockMvc.perform(get("/api/project-roles/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProjectRole() throws Exception {
        // Initialize the database
        projectRoleService.save(projectRole);

        int databaseSizeBeforeUpdate = projectRoleRepository.findAll().size();

        // Update the projectRole
        ProjectRole updatedProjectRole = projectRoleRepository.findOne(projectRole.getId());
        updatedProjectRole
            .upsaId(UPDATED_UPSA_ID)
            .name(UPDATED_NAME);

        restProjectRoleMockMvc.perform(put("/api/project-roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProjectRole)))
            .andExpect(status().isOk());

        // Validate the ProjectRole in the database
        List<ProjectRole> projectRoleList = projectRoleRepository.findAll();
        assertThat(projectRoleList).hasSize(databaseSizeBeforeUpdate);
        ProjectRole testProjectRole = projectRoleList.get(projectRoleList.size() - 1);
        assertThat(testProjectRole.getUpsaId()).isEqualTo(UPDATED_UPSA_ID);
        assertThat(testProjectRole.getName()).isEqualTo(UPDATED_NAME);

        // Validate the ProjectRole in Elasticsearch
        ProjectRole projectRoleEs = projectRoleSearchRepository.findOne(testProjectRole.getId());
        assertThat(projectRoleEs).isEqualToComparingFieldByField(testProjectRole);
    }

    @Test
    @Transactional
    public void updateNonExistingProjectRole() throws Exception {
        int databaseSizeBeforeUpdate = projectRoleRepository.findAll().size();

        // Create the ProjectRole

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProjectRoleMockMvc.perform(put("/api/project-roles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectRole)))
            .andExpect(status().isCreated());

        // Validate the ProjectRole in the database
        List<ProjectRole> projectRoleList = projectRoleRepository.findAll();
        assertThat(projectRoleList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProjectRole() throws Exception {
        // Initialize the database
        projectRoleService.save(projectRole);

        int databaseSizeBeforeDelete = projectRoleRepository.findAll().size();

        // Get the projectRole
        restProjectRoleMockMvc.perform(delete("/api/project-roles/{id}", projectRole.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean projectRoleExistsInEs = projectRoleSearchRepository.exists(projectRole.getId());
        assertThat(projectRoleExistsInEs).isFalse();

        // Validate the database is empty
        List<ProjectRole> projectRoleList = projectRoleRepository.findAll();
        assertThat(projectRoleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProjectRole() throws Exception {
        // Initialize the database
        projectRoleService.save(projectRole);

        // Search the projectRole
        restProjectRoleMockMvc.perform(get("/api/_search/project-roles?query=id:" + projectRole.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectRole.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectRole.class);
        ProjectRole projectRole1 = new ProjectRole();
        projectRole1.setId(1L);
        ProjectRole projectRole2 = new ProjectRole();
        projectRole2.setId(projectRole1.getId());
        assertThat(projectRole1).isEqualTo(projectRole2);
        projectRole2.setId(2L);
        assertThat(projectRole1).isNotEqualTo(projectRole2);
        projectRole1.setId(null);
        assertThat(projectRole1).isNotEqualTo(projectRole2);
    }
}
