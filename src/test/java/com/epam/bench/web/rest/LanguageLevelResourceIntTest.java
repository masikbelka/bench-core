package com.epam.bench.web.rest;

import com.epam.bench.BenchCoreApp;

import com.epam.bench.domain.LanguageLevel;
import com.epam.bench.repository.LanguageLevelRepository;
import com.epam.bench.service.LanguageLevelService;
import com.epam.bench.repository.search.LanguageLevelSearchRepository;
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
 * Test class for the LanguageLevelResource REST controller.
 *
 * @see LanguageLevelResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BenchCoreApp.class)
public class LanguageLevelResourceIntTest {

    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final String DEFAULT_SPEAKING = "AAAAAAAAAA";
    private static final String UPDATED_SPEAKING = "BBBBBBBBBB";

    private static final String DEFAULT_WRITING = "AAAAAAAAAA";
    private static final String UPDATED_WRITING = "BBBBBBBBBB";

    @Autowired
    private LanguageLevelRepository languageLevelRepository;

    @Autowired
    private LanguageLevelService languageLevelService;

    @Autowired
    private LanguageLevelSearchRepository languageLevelSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restLanguageLevelMockMvc;

    private LanguageLevel languageLevel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LanguageLevelResource languageLevelResource = new LanguageLevelResource(languageLevelService);
        this.restLanguageLevelMockMvc = MockMvcBuilders.standaloneSetup(languageLevelResource)
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
    public static LanguageLevel createEntity(EntityManager em) {
        LanguageLevel languageLevel = new LanguageLevel()
            .language(DEFAULT_LANGUAGE)
            .speaking(DEFAULT_SPEAKING)
            .writing(DEFAULT_WRITING);
        return languageLevel;
    }

    @Before
    public void initTest() {
        languageLevelSearchRepository.deleteAll();
        languageLevel = createEntity(em);
    }

    @Test
    @Transactional
    public void createLanguageLevel() throws Exception {
        int databaseSizeBeforeCreate = languageLevelRepository.findAll().size();

        // Create the LanguageLevel
        restLanguageLevelMockMvc.perform(post("/api/language-levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(languageLevel)))
            .andExpect(status().isCreated());

        // Validate the LanguageLevel in the database
        List<LanguageLevel> languageLevelList = languageLevelRepository.findAll();
        assertThat(languageLevelList).hasSize(databaseSizeBeforeCreate + 1);
        LanguageLevel testLanguageLevel = languageLevelList.get(languageLevelList.size() - 1);
        assertThat(testLanguageLevel.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
        assertThat(testLanguageLevel.getSpeaking()).isEqualTo(DEFAULT_SPEAKING);
        assertThat(testLanguageLevel.getWriting()).isEqualTo(DEFAULT_WRITING);

        // Validate the LanguageLevel in Elasticsearch
        LanguageLevel languageLevelEs = languageLevelSearchRepository.findOne(testLanguageLevel.getId());
        assertThat(languageLevelEs).isEqualToComparingFieldByField(testLanguageLevel);
    }

    @Test
    @Transactional
    public void createLanguageLevelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = languageLevelRepository.findAll().size();

        // Create the LanguageLevel with an existing ID
        languageLevel.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLanguageLevelMockMvc.perform(post("/api/language-levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(languageLevel)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<LanguageLevel> languageLevelList = languageLevelRepository.findAll();
        assertThat(languageLevelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkLanguageIsRequired() throws Exception {
        int databaseSizeBeforeTest = languageLevelRepository.findAll().size();
        // set the field null
        languageLevel.setLanguage(null);

        // Create the LanguageLevel, which fails.

        restLanguageLevelMockMvc.perform(post("/api/language-levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(languageLevel)))
            .andExpect(status().isBadRequest());

        List<LanguageLevel> languageLevelList = languageLevelRepository.findAll();
        assertThat(languageLevelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLanguageLevels() throws Exception {
        // Initialize the database
        languageLevelRepository.saveAndFlush(languageLevel);

        // Get all the languageLevelList
        restLanguageLevelMockMvc.perform(get("/api/language-levels?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(languageLevel.getId().intValue())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].speaking").value(hasItem(DEFAULT_SPEAKING.toString())))
            .andExpect(jsonPath("$.[*].writing").value(hasItem(DEFAULT_WRITING.toString())));
    }

    @Test
    @Transactional
    public void getLanguageLevel() throws Exception {
        // Initialize the database
        languageLevelRepository.saveAndFlush(languageLevel);

        // Get the languageLevel
        restLanguageLevelMockMvc.perform(get("/api/language-levels/{id}", languageLevel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(languageLevel.getId().intValue()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()))
            .andExpect(jsonPath("$.speaking").value(DEFAULT_SPEAKING.toString()))
            .andExpect(jsonPath("$.writing").value(DEFAULT_WRITING.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLanguageLevel() throws Exception {
        // Get the languageLevel
        restLanguageLevelMockMvc.perform(get("/api/language-levels/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLanguageLevel() throws Exception {
        // Initialize the database
        languageLevelService.save(languageLevel);

        int databaseSizeBeforeUpdate = languageLevelRepository.findAll().size();

        // Update the languageLevel
        LanguageLevel updatedLanguageLevel = languageLevelRepository.findOne(languageLevel.getId());
        updatedLanguageLevel
            .language(UPDATED_LANGUAGE)
            .speaking(UPDATED_SPEAKING)
            .writing(UPDATED_WRITING);

        restLanguageLevelMockMvc.perform(put("/api/language-levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLanguageLevel)))
            .andExpect(status().isOk());

        // Validate the LanguageLevel in the database
        List<LanguageLevel> languageLevelList = languageLevelRepository.findAll();
        assertThat(languageLevelList).hasSize(databaseSizeBeforeUpdate);
        LanguageLevel testLanguageLevel = languageLevelList.get(languageLevelList.size() - 1);
        assertThat(testLanguageLevel.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        assertThat(testLanguageLevel.getSpeaking()).isEqualTo(UPDATED_SPEAKING);
        assertThat(testLanguageLevel.getWriting()).isEqualTo(UPDATED_WRITING);

        // Validate the LanguageLevel in Elasticsearch
        LanguageLevel languageLevelEs = languageLevelSearchRepository.findOne(testLanguageLevel.getId());
        assertThat(languageLevelEs).isEqualToComparingFieldByField(testLanguageLevel);
    }

    @Test
    @Transactional
    public void updateNonExistingLanguageLevel() throws Exception {
        int databaseSizeBeforeUpdate = languageLevelRepository.findAll().size();

        // Create the LanguageLevel

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLanguageLevelMockMvc.perform(put("/api/language-levels")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(languageLevel)))
            .andExpect(status().isCreated());

        // Validate the LanguageLevel in the database
        List<LanguageLevel> languageLevelList = languageLevelRepository.findAll();
        assertThat(languageLevelList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLanguageLevel() throws Exception {
        // Initialize the database
        languageLevelService.save(languageLevel);

        int databaseSizeBeforeDelete = languageLevelRepository.findAll().size();

        // Get the languageLevel
        restLanguageLevelMockMvc.perform(delete("/api/language-levels/{id}", languageLevel.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean languageLevelExistsInEs = languageLevelSearchRepository.exists(languageLevel.getId());
        assertThat(languageLevelExistsInEs).isFalse();

        // Validate the database is empty
        List<LanguageLevel> languageLevelList = languageLevelRepository.findAll();
        assertThat(languageLevelList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchLanguageLevel() throws Exception {
        // Initialize the database
        languageLevelService.save(languageLevel);

        // Search the languageLevel
        restLanguageLevelMockMvc.perform(get("/api/_search/language-levels?query=id:" + languageLevel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(languageLevel.getId().intValue())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].speaking").value(hasItem(DEFAULT_SPEAKING.toString())))
            .andExpect(jsonPath("$.[*].writing").value(hasItem(DEFAULT_WRITING.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LanguageLevel.class);
        LanguageLevel languageLevel1 = new LanguageLevel();
        languageLevel1.setId(1L);
        LanguageLevel languageLevel2 = new LanguageLevel();
        languageLevel2.setId(languageLevel1.getId());
        assertThat(languageLevel1).isEqualTo(languageLevel2);
        languageLevel2.setId(2L);
        assertThat(languageLevel1).isNotEqualTo(languageLevel2);
        languageLevel1.setId(null);
        assertThat(languageLevel1).isNotEqualTo(languageLevel2);
    }
}
