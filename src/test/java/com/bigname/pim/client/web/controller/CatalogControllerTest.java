package com.bigname.pim.client.web.controller;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.RootCategoryDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes={PimApplication.class})
public class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private CatalogController catalogController;

    @Autowired
    private CatalogDAO catalogDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private RootCategoryDAO rootCategoryDAO;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CategoryService categoryService;

    @Before
    public void setUp() throws Exception {
        if(!userService.get("MANU@BLACWOOD.COM", FindBy.EXTERNAL_ID).isPresent()) {
            User user = new User();
            user.setUserName("MANU@BLACWOOD.COm");
            user.setPassword("temppass");
            user.setEmail("manu@blacwood.com");
            user.setActive("Y");
            userService.create(user);
        }
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
        rootCategoryDAO.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
        rootCategoryDAO.deleteAll();
    }

    @Test
    public void contexLoads() throws Exception {
        Assert.assertNotNull(catalogController);
        Assert.assertNotNull(catalogDAO);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void create() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("catalogName", ConversionUtil.toList("TestCatalog"));
        params.put("catalogId", ConversionUtil.toList("TEST"));
        params.put("description", ConversionUtil.toList("Test Catalog"));
        ResultActions result = mockMvc.perform(
                post("/pim/catalogs")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(3));
        result.andExpect(jsonPath("$.success").value(true));
        result.andExpect(jsonPath("$.group.length()").value(1));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void update() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("catalogName", ConversionUtil.toList("TestCatalog"));
        params.put("catalogId", ConversionUtil.toList("TEST"));
        params.put("description", ConversionUtil.toList("Test Catalog"));
        ResultActions result = mockMvc.perform(
                post("/pim/catalogs")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result.andExpect(status().isOk());

        MultiValueMap<String, String> updateParams = new LinkedMultiValueMap<>();
        updateParams.put("group", ConversionUtil.toList("DETAILS"));
        updateParams.put("catalogName", ConversionUtil.toList("TestCatalog1"));
        updateParams.put("catalogId", ConversionUtil.toList("TEST"));
        updateParams.put("description", ConversionUtil.toList("Test Catalog"));
        ResultActions updateResult = mockMvc.perform(
                put("/pim/catalogs/TEST")
                        .params(updateParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        updateResult.andExpect(status().isOk());
        updateResult.andExpect(jsonPath("$.size()").value(3));
        updateResult.andExpect(jsonPath("$.success").value(true));
        updateResult.andExpect(jsonPath("$.group.length()").value(1));
    }

    @Test
    public void details() throws Exception {
    }

    @Test
    public void all() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void all1() throws Exception {
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "description", "TEST_3description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "description", "TEST_4description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "description", "TEST_5description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "description", "TEST_6description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "description", "TEST_7description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "description", "TEST_8description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test9", "externalId", "TEST_9", "description", "TEST_9description", "active", "Y"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogService.create(catalogDTO);
        });

        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("start", ConversionUtil.toList("0"));
        detailParams.put("length", ConversionUtil.toList("5"));
        detailParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/catalogs/data")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(5));
        result1.andExpect(jsonPath("$.draw").value(1));
        result1.andExpect(jsonPath("$.recordsFiltered").value(9));
        result1.andExpect(jsonPath("$.recordsTotal").value(9));

        detailParams = new LinkedMultiValueMap<>();
        detailParams.put("start", ConversionUtil.toList("5"));
        detailParams.put("length", ConversionUtil.toList("5"));
        detailParams.put("draw", ConversionUtil.toList("2"));
        ResultActions result2 = mockMvc.perform(
                get("/pim/catalogs/data")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result2.andExpect(status().isOk());
        result2.andExpect(jsonPath("$.data.size()").value(4));
        result2.andExpect(jsonPath("$.draw").value(2));
        result2.andExpect(jsonPath("$.recordsFiltered").value(9));
        result2.andExpect(jsonPath("$.recordsTotal").value(9));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getRootCategories() throws Exception {
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog Main", "externalId", "TEST_CATALOG_MAIN", "description", "Test Catalog Main description", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogService.create(catalogDTO);
        });

        Catalog catalog = catalogService.get(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);

            Category category = categoryService.get(categoryData.get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);
            catalogService.addRootCategory(catalog.getExternalId(), FindBy.EXTERNAL_ID, category.getExternalId(), FindBy.EXTERNAL_ID);
        });

        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("start", ConversionUtil.toList("0"));
        detailParams.put("length", ConversionUtil.toList("5"));
        detailParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/catalogs/TEST_CATALOG_MAIN/rootCategories/data")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(2));
        result1.andExpect(jsonPath("$.draw").value(1));
        result1.andExpect(jsonPath("$.recordsFiltered").value(2));
        result1.andExpect(jsonPath("$.recordsTotal").value(2));
    }

    @Test
    public void setRootCategoriesSequence() throws Exception {
    }

    @Test
    public void availableCategories() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAvailableRootCategories() throws Exception {
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog Main", "externalId", "TEST_CATALOG_MAIN", "description", "Test Catalog Main description", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogService.create(catalogDTO);
        });

        Catalog catalog = catalogService.get(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 3", "externalId", "TEST_CATEGORY_3", "description", "Test description 3", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);
        catalogService.addRootCategory(catalog.getExternalId(), FindBy.EXTERNAL_ID, category.getExternalId(), FindBy.EXTERNAL_ID);

        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("start", ConversionUtil.toList("0"));
        detailParams.put("length", ConversionUtil.toList("5"));
        detailParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/catalogs/TEST_CATALOG_MAIN/rootCategories/available/list")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(2));
        result1.andExpect(jsonPath("$.draw").value(1));
        result1.andExpect(jsonPath("$.recordsFiltered").value(2));
        result1.andExpect(jsonPath("$.recordsTotal").value(2));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void addRootCategory() throws Exception {
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog Main", "externalId", "TEST_CATALOG_MAIN", "description", "Test Catalog Main description", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogService.create(catalogDTO);
        });

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("id", ConversionUtil.toList("TEST_CATALOG_MAIN"));
        detailParams.put("rootCategoryId", ConversionUtil.toList("TEST_CATEGORY_1"));
        ResultActions result1 = mockMvc.perform(
                post("/pim/catalogs/TEST_CATALOG_MAIN/rootCategories/TEST_CATEGORY_1")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.size()").value(1));
        result1.andExpect(jsonPath("$.success").value(true));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toggleRootCategory() throws Exception {
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog Main", "externalId", "TEST_CATALOG_MAIN", "description", "Test Catalog Main description", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogService.create(catalogDTO);
        });

        Catalog catalog = catalogService.get(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);

            Category category = categoryService.get(categoryData.get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);
            catalogService.addRootCategory(catalog.getExternalId(), FindBy.EXTERNAL_ID, category.getExternalId(), FindBy.EXTERNAL_ID);
        });

        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("id", ConversionUtil.toList("TEST_CATALOG_MAIN"));
        detailParams.put("rootCategoryId", ConversionUtil.toList("TEST_CATEGORY_1"));
        detailParams.put("active", ConversionUtil.toList("Y"));
        ResultActions result1 = mockMvc.perform(
                put("/pim/catalogs/TEST_CATALOG_MAIN/rootCategories/TEST_CATEGORY_1/active/Y")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.size()").value(1));
        result1.andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void getCategoriesHierarchy() throws Exception {

    }

}