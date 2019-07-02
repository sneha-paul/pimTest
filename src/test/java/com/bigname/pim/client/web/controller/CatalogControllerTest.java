package com.bigname.pim.client.web.controller;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.RootCategoryDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.service.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
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

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(!userService.get(ID.EXTERNAL_ID("MANU@BLACWOOD.COM")).isPresent()) {
            User user = new User();
            user.setUserName("MANU@BLACWOOD.COm");
            user.setPassword("temppass");
            user.setEmail("manu@blacwood.com");
            user.setActive("Y");
            userService.create(user);
        }
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = ((GenericRepositoryImpl)catalogDAO).getMongoTemplate();
        }
        mongoTemplate.dropCollection(Catalog.class);
        mongoTemplate.dropCollection(Category.class);
        rootCategoryDAO.deleteAll();
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Catalog.class);
        mongoTemplate.dropCollection(Category.class);
        rootCategoryDAO.deleteAll();
    }

    @Test
    public void contexLoads() throws Exception {
        Assert.assertNotNull(catalogController);
        Assert.assertNotNull(catalogDAO);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createTest() throws Exception {

        //Create a new Catalog

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
    public void updateTest() throws Exception {
        //Create a new Catalog
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

        //update the Catalog
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

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void detailsTest() throws Exception {
        //Create mode
        mockMvc.perform(
                get("/pim/catalogs/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog/catalog"))
                .andExpect(forwardedUrl("/catalog/catalog.jsp"))
                .andExpect(model().attribute("mode", is("CREATE")))
                .andExpect(model().attribute("active", is("CATALOGS")));

        //Details mode, with non=existing catalogID - TODO

        //Add a catalog instance
        List<Catalog> createdCatalogInstances = addCatalogInstances();
        Assert.assertFalse(createdCatalogInstances.isEmpty());

        //Details mode with valid catalogID
        String catalogId = createdCatalogInstances.get(0).getCatalogId();
        mockMvc.perform(
                get("/pim/catalogs/" + catalogId))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog/catalog"))
                .andExpect(forwardedUrl("/catalog/catalog.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("CATALOGS")))
                .andExpect(model().attribute("catalog", hasProperty("externalId", is(catalogId))));

        //Details mode with reload true
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("reload", ConversionUtil.toList("true"));

        mockMvc.perform(
                get("/pim/catalogs/" + catalogId).params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog/catalog_body"))
                .andExpect(forwardedUrl("/catalog/catalog_body.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("CATALOGS")))
                .andExpect(model().attribute("catalog", hasProperty("externalId", is(catalogId))));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void allTest() throws Exception {
        mockMvc.perform(
                get("/pim/catalogs"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog/catalogs"))
                .andExpect(forwardedUrl("/catalog/catalogs.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void allTest1() throws Exception {
        //Create Catalogs
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

        //retrieve those entries by pagination
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
    public void getRootCategoriesTest() throws Exception {
        //Create a new Catalog
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

        //Create Categories and add them as rootCategories
        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), false).orElse(null);

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

            Category category = categoryService.get(ID.EXTERNAL_ID(categoryData.get("externalId").toString()), false).orElse(null);
            catalogService.addRootCategory(ID.EXTERNAL_ID(catalog.getExternalId()), ID.EXTERNAL_ID(category.getExternalId()));
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

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setRootCategoriesSequenceTest() throws Exception {

        //creating catalog
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

        //creating category
        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 3", "externalId", "TEST_CATEGORY_3", "description", "Test description 3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 4", "externalId", "TEST_CATEGORY_4", "description", "Test description 4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 5", "externalId", "TEST_CATEGORY_5", "description", "Test description 5", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 6", "externalId", "TEST_CATEGORY_6", "description", "Test description 6", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 7", "externalId", "TEST_CATEGORY_7", "description", "Test description 7", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        //Adding root categories
        List<Category> categoryList = categoryService.getAll(null, false);
        int count[] = {1};
        categoryList.forEach(rootCategoryData -> {
            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(rootCategoryData.getId());
            rootCategory.setSequenceNum(count[0]);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(rootCategoryData.getActive());
            rootCategoryDAO.insert(rootCategory);
            count[0] ++;
        });

        //getting sequencing
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("sourceId", ConversionUtil.toList(categoriesData.get(1).get("externalId").toString()));
        params.put("destinationId", ConversionUtil.toList(categoriesData.get(0).get("externalId").toString()));

        ResultActions result = mockMvc.perform(
                put("/pim/catalogs/TEST_CATALOG_MAIN/rootCategories/data")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(jsonPath("$.success").value(true));

        List<RootCategory> rootCategoryList = catalogService.getAllRootCategories(catalog.getId());
        Assert.assertEquals(rootCategoryList.get(0).getSequenceNum(), 1);
        Assert.assertEquals(rootCategoryList.get(0).getSubSequenceNum(), 0);

        Assert.assertEquals(rootCategoryList.get(1).getSequenceNum(), 1);
        Assert.assertEquals(rootCategoryList.get(1).getSubSequenceNum(), 1);

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void availableCategoriesTest() throws Exception {
        //Add a catalog instance
        List<Catalog> createdCatalogInstances = addCatalogInstances();
        Assert.assertFalse(createdCatalogInstances.isEmpty());

        //Details mode with valid catalogID
        String catalogId = createdCatalogInstances.get(0).getCatalogId();
        mockMvc.perform(
                get("/pim/catalogs/" + catalogId + "/rootCategories/available"))
                .andExpect(status().isOk())
                .andExpect(view().name("category/availableRootCategories"))
                .andExpect(forwardedUrl("/category/availableRootCategories.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAvailableRootCategoriesTest() throws Exception {
        //creating catalog
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

        //creating categories
        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), false).orElse(null);

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

        //Adding root category
        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), false).orElse(null);
        catalogService.addRootCategory(ID.EXTERNAL_ID(catalog.getExternalId()), ID.EXTERNAL_ID(category.getExternalId()));

        //Getting available root categories
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
    public void addRootCategoryTest() throws Exception {
        //create catalog
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

        //create categories
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

        //Adding root categories
        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), false).orElse(null);

        Page<Category> rootCategories = catalogService.getAvailableRootCategoriesForCatalog(ID.EXTERNAL_ID(catalog.getExternalId()), 1, 1, null, false);

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

        Page<Category> availableRootCategories = catalogService.getAvailableRootCategoriesForCatalog(ID.EXTERNAL_ID(catalog.getExternalId()), 1, 1, null, false);
        Assert.assertEquals(availableRootCategories.getTotalElements(), rootCategories.getTotalElements()-1);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toggleRootCategoryTest() throws Exception {
        //creating catalog
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

        //creating categories
        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), false).orElse(null);

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

            Category category = categoryService.get(ID.EXTERNAL_ID(categoryData.get("externalId").toString()), false).orElse(null);
            catalogService.addRootCategory(ID.EXTERNAL_ID(catalog.getExternalId()), ID.EXTERNAL_ID(category.getExternalId()));
        });

        //Toggle
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

        List<RootCategory> rootCategoryList = catalogService.getAllRootCategories(catalog.getId());
        Assert.assertEquals(rootCategoryList.get(0).getActive(), "N");

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getCategoriesHierarchyTest() throws Exception {
        //creating catalog
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

        //creating categories
        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y", "parent", "0", "isParent", "true", "level", "0", "parentChain", ""));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "Y", "parent", "TEST_CATEGORY_1", "isParent", "true", "level", "1", "parentChain", "TEST_CATEGORY_1"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 3", "externalId", "TEST_CATEGORY_3", "description", "Test description 3", "active", "Y", "parent", "TEST_CATEGORY_2", "isParent", "true", "level", "2", "parentChain", "TEST_CATEGORY_1|TEST_CATEGORY_2"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 4", "externalId", "TEST_CATEGORY_4", "description", "Test description 4", "active", "Y", "parent", "TEST_CATEGORY_3", "isParent", "true", "level", "3", "parentChain", "TEST_CATEGORY_1|TEST_CATEGORY_2|TEST_CATEGORY_3"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 5", "externalId", "TEST_CATEGORY_5", "description", "Test description 5", "active", "Y", "parent", "TEST_CATEGORY_4", "isParent", "false", "level", "4", "parentChain", "TEST_CATEGORY_1|TEST_CATEGORY_2|TEST_CATEGORY_3|TEST_CATEGORY_4"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 6", "externalId", "TEST_CATEGORY_6", "description", "Test description 6", "active", "Y", "parent", "TEST_CATEGORY_3", "isParent", "false", "level", "3", "parentChain", "TEST_CATEGORY_1|TEST_CATEGORY_2|TEST_CATEGORY_3"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        //Adding root categories
        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()),  false).orElse(null);

        catalogService.addRootCategory(ID.EXTERNAL_ID(catalog.getExternalId()), ID.EXTERNAL_ID(category.getExternalId()));

        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(1).get("externalId").toString()));
        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(1).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(2).get("externalId").toString()));
        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(2).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(3).get("externalId").toString()));
        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(3).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(4).get("externalId").toString()));
        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(2).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(5).get("externalId").toString()));

        //Getting categories in hierarchy
        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("start", ConversionUtil.toList("0"));
        detailsParams.put("length", ConversionUtil.toList("6"));
        detailsParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/catalogs/TEST_CATALOG_MAIN/hierarchy")
                        .params(detailsParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].parent").value(0));
        result.andExpect(jsonPath("$[0].isParent").value("true"));
        result.andExpect(jsonPath("$[0].level").value(0));
        result.andExpect(jsonPath("$[0].parentChain").value(""));

        result.andExpect(jsonPath("$[1].parent").value("TEST_CATEGORY_1"));
        result.andExpect(jsonPath("$[1].isParent").value("true"));
        result.andExpect(jsonPath("$[1].level").value(1));
        result.andExpect(jsonPath("$[1].parentChain").value("TEST_CATEGORY_1"));

        result.andExpect(jsonPath("$[2].parent").value("TEST_CATEGORY_2"));
        result.andExpect(jsonPath("$[2].isParent").value("true"));
        result.andExpect(jsonPath("$[2].level").value(2));
        result.andExpect(jsonPath("$[2].parentChain").value("TEST_CATEGORY_1|TEST_CATEGORY_2"));

        result.andExpect(jsonPath("$[3].parent").value("TEST_CATEGORY_3"));
        result.andExpect(jsonPath("$[3].isParent").value("false"));
        result.andExpect(jsonPath("$[3].level").value(3));
        result.andExpect(jsonPath("$[3].parentChain").value("TEST_CATEGORY_1|TEST_CATEGORY_2|TEST_CATEGORY_3"));

        result.andExpect(jsonPath("$[4].parent").value("TEST_CATEGORY_3"));
        result.andExpect(jsonPath("$[4].isParent").value("true"));
        result.andExpect(jsonPath("$[4].level").value(3));
        result.andExpect(jsonPath("$[4].parentChain").value("TEST_CATEGORY_1|TEST_CATEGORY_2|TEST_CATEGORY_3"));

        result.andExpect(jsonPath("$[5].parent").value("TEST_CATEGORY_4"));
        result.andExpect(jsonPath("$[5].isParent").value("false"));
        result.andExpect(jsonPath("$[5].level").value(4));
        result.andExpect(jsonPath("$[5].parentChain").value("TEST_CATEGORY_1|TEST_CATEGORY_2|TEST_CATEGORY_3|TEST_CATEGORY_4"));

    }

    private List<Catalog> addCatalogInstances() {
        List<Catalog> createdCatalogInstances = new ArrayList<>();
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog Main", "externalId", "TEST_CATALOG_MAIN", "description", "Test Catalog Main description", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            createdCatalogInstances.add(catalogService.create(catalogDTO));
        });
        return createdCatalogInstances;
    }

}