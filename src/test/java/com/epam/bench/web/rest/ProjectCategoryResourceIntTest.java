package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.ProjectCategory;
import com.epam.bench.repository.ProjectCategoryRepository;
import com.epam.bench.service.ProjectCategoryService;
import com.epam.bench.repository.search.ProjectCategorySearchRepository;
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
 * Test class for the ProjectCategoryResource REST controller.
 *
 * @see ProjectCategoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class ProjectCategoryResourceIntTest {

    private static final String DEFAULT_UPSA_ID = "AAAAAAAAAA";
    private static final String UPDATED_UPSA_ID = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private ProjectCategoryRepository projectCategoryRepository;

    @Autowired
    private ProjectCategoryService projectCategoryService;

    @Autowired
    private ProjectCategorySearchRepository projectCategorySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restProjectCategoryMockMvc;

    private ProjectCategory projectCategory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectCategoryResource projectCategoryResource = new ProjectCategoryResource(projectCategoryService);
        this.restProjectCategoryMockMvc = MockMvcBuilders.standaloneSetup(projectCategoryResource)
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
    public static ProjectCategory createEntity(EntityManager em) {
        ProjectCategory projectCategory = new ProjectCategory()
            .upsaId(DEFAULT_UPSA_ID)
            .name(DEFAULT_NAME);
        return projectCategory;
    }

    @Before
    public void initTest() {
        projectCategorySearchRepository.deleteAll();
        projectCategory = createEntity(em);
    }

    @Test
    @Transactional
    public void createProjectCategory() throws Exception {
        int databaseSizeBeforeCreate = projectCategoryRepository.findAll().size();

        // Create the ProjectCategory
        restProjectCategoryMockMvc.perform(post("/api/project-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectCategory)))
            .andExpect(status().isCreated());

        // Validate the ProjectCategory in the database
        List<ProjectCategory> projectCategoryList = projectCategoryRepository.findAll();
        assertThat(projectCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        ProjectCategory testProjectCategory = projectCategoryList.get(projectCategoryList.size() - 1);
        assertThat(testProjectCategory.getUpsaId()).isEqualTo(DEFAULT_UPSA_ID);
        assertThat(testProjectCategory.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the ProjectCategory in Elasticsearch
        ProjectCategory projectCategoryEs = projectCategorySearchRepository.findOne(testProjectCategory.getId());
        assertThat(projectCategoryEs).isEqualToComparingFieldByField(testProjectCategory);
    }

    @Test
    @Transactional
    public void createProjectCategoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = projectCategoryRepository.findAll().size();

        // Create the ProjectCategory with an existing ID
        projectCategory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectCategoryMockMvc.perform(post("/api/project-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectCategory)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ProjectCategory> projectCategoryList = projectCategoryRepository.findAll();
        assertThat(projectCategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUpsaIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = projectCategoryRepository.findAll().size();
        // set the field null
        projectCategory.setUpsaId(null);

        // Create the ProjectCategory, which fails.

        restProjectCategoryMockMvc.perform(post("/api/project-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectCategory)))
            .andExpect(status().isBadRequest());

        List<ProjectCategory> projectCategoryList = projectCategoryRepository.findAll();
        assertThat(projectCategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProjectCategories() throws Exception {
        // Initialize the database
        projectCategoryRepository.saveAndFlush(projectCategory);

        // Get all the projectCategoryList
        restProjectCategoryMockMvc.perform(get("/api/project-categories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getProjectCategory() throws Exception {
        // Initialize the database
        projectCategoryRepository.saveAndFlush(projectCategory);

        // Get the projectCategory
        restProjectCategoryMockMvc.perform(get("/api/project-categories/{id}", projectCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(projectCategory.getId().intValue()))
            .andExpect(jsonPath("$.upsaId").value(DEFAULT_UPSA_ID.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProjectCategory() throws Exception {
        // Get the projectCategory
        restProjectCategoryMockMvc.perform(get("/api/project-categories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProjectCategory() throws Exception {
        // Initialize the database
        projectCategoryService.save(projectCategory);

        int databaseSizeBeforeUpdate = projectCategoryRepository.findAll().size();

        // Update the projectCategory
        ProjectCategory updatedProjectCategory = projectCategoryRepository.findOne(projectCategory.getId());
        updatedProjectCategory
            .upsaId(UPDATED_UPSA_ID)
            .name(UPDATED_NAME);

        restProjectCategoryMockMvc.perform(put("/api/project-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProjectCategory)))
            .andExpect(status().isOk());

        // Validate the ProjectCategory in the database
        List<ProjectCategory> projectCategoryList = projectCategoryRepository.findAll();
        assertThat(projectCategoryList).hasSize(databaseSizeBeforeUpdate);
        ProjectCategory testProjectCategory = projectCategoryList.get(projectCategoryList.size() - 1);
        assertThat(testProjectCategory.getUpsaId()).isEqualTo(UPDATED_UPSA_ID);
        assertThat(testProjectCategory.getName()).isEqualTo(UPDATED_NAME);

        // Validate the ProjectCategory in Elasticsearch
        ProjectCategory projectCategoryEs = projectCategorySearchRepository.findOne(testProjectCategory.getId());
        assertThat(projectCategoryEs).isEqualToComparingFieldByField(testProjectCategory);
    }

    @Test
    @Transactional
    public void updateNonExistingProjectCategory() throws Exception {
        int databaseSizeBeforeUpdate = projectCategoryRepository.findAll().size();

        // Create the ProjectCategory

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restProjectCategoryMockMvc.perform(put("/api/project-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectCategory)))
            .andExpect(status().isCreated());

        // Validate the ProjectCategory in the database
        List<ProjectCategory> projectCategoryList = projectCategoryRepository.findAll();
        assertThat(projectCategoryList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteProjectCategory() throws Exception {
        // Initialize the database
        projectCategoryService.save(projectCategory);

        int databaseSizeBeforeDelete = projectCategoryRepository.findAll().size();

        // Get the projectCategory
        restProjectCategoryMockMvc.perform(delete("/api/project-categories/{id}", projectCategory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean projectCategoryExistsInEs = projectCategorySearchRepository.exists(projectCategory.getId());
        assertThat(projectCategoryExistsInEs).isFalse();

        // Validate the database is empty
        List<ProjectCategory> projectCategoryList = projectCategoryRepository.findAll();
        assertThat(projectCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchProjectCategory() throws Exception {
        // Initialize the database
        projectCategoryService.save(projectCategory);

        // Search the projectCategory
        restProjectCategoryMockMvc.perform(get("/api/_search/project-categories?query=id:" + projectCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].upsaId").value(hasItem(DEFAULT_UPSA_ID.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectCategory.class);
        ProjectCategory projectCategory1 = new ProjectCategory();
        projectCategory1.setId(1L);
        ProjectCategory projectCategory2 = new ProjectCategory();
        projectCategory2.setId(projectCategory1.getId());
        assertThat(projectCategory1).isEqualTo(projectCategory2);
        projectCategory2.setId(2L);
        assertThat(projectCategory1).isNotEqualTo(projectCategory2);
        projectCategory1.setId(null);
        assertThat(projectCategory1).isNotEqualTo(projectCategory2);
    }
}
