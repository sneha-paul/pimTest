package com.bigname.pim.client.web.controller;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
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
import org.springframework.data.domain.Page;
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
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryController categoryController;

    @Autowired
    private CategoryDAO categoryDAO;

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
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }

    @Test
    public void contexLoads() throws Exception {
        Assert.assertNotNull(categoryController);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createTest() throws Exception {
        //Creating category
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("categoryName", ConversionUtil.toList("TestCategory"));
        params.put("description", ConversionUtil.toList("Test Category Description"));
        params.put("categoryId", ConversionUtil.toList("TEST"));
        ResultActions result = mockMvc.perform(
                post("/pim/categories")
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

        //Creating category
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("categoryName", ConversionUtil.toList("TestCategory"));
        params.put("description", ConversionUtil.toList("Test Category Description"));
        params.put("categoryId", ConversionUtil.toList("TEST"));
        ResultActions result = mockMvc.perform(
                post("/pim/categories")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());

        //Updating category
        params.put("group", ConversionUtil.toList("DETAILS"));
        params.put("categoryName", ConversionUtil.toList("TestCategoryNew"));
        ResultActions result1 = mockMvc.perform(
                put("/pim/categories/TEST")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.size()").value(3));
        result1.andExpect(jsonPath("$.success").value(true));
        result1.andExpect(jsonPath("$.group.length()").value(1));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void detailsTest() throws Exception {

        //Create mode
        mockMvc.perform(
                get("/pim/categories/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("category/category"))
                .andExpect(forwardedUrl("/category/category.jsp"))
                .andExpect(model().attribute("mode", is("CREATE")))
                .andExpect(model().attribute("active", is("CATEGORIES")));

        //Details mode, with non=existing categoryID - TODO

        //Add a website instance
        List<Category> createdCategoryInstances = addCategoryInstances();
        Assert.assertFalse(createdCategoryInstances.isEmpty());

        //Details mode with valid categoryID
        String categoryId = createdCategoryInstances.get(0).getCategoryId();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        mockMvc.perform(
                get("/pim/categories/" + categoryId).params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("category/category"))
                .andExpect(forwardedUrl("/category/category.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("CATEGORIES")))
                .andExpect(model().attribute("category", hasProperty("externalId", is(categoryId))));

        //Adding subCategory
        categoryService.addSubCategory(createdCategoryInstances.get(0).getCategoryId(), FindBy.EXTERNAL_ID, createdCategoryInstances.get(1).getCategoryId(), FindBy.EXTERNAL_ID);
        Category category = categoryService.get(createdCategoryInstances.get(0).getCategoryId(), FindBy.EXTERNAL_ID, false).orElse(null);

        //Details mode with valid categoryID and parentID
        String categoryId1 = createdCategoryInstances.get(1).getCategoryId();
        String parentCategoryId = category.getCategoryId();
        params = new LinkedMultiValueMap<>();
        params.put("parentId", ConversionUtil.toList(parentCategoryId));

        mockMvc.perform(
                get("/pim/categories/" + categoryId1).params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("category/category"))
                .andExpect(forwardedUrl("/category/category.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("CATEGORIES")))
                .andExpect(model().attribute("parentId", is(parentCategoryId)))
                .andExpect(model().attribute("category", hasProperty("externalId", is(categoryId1))));


        //Details mode with reload true
        params = new LinkedMultiValueMap<>();
        params.put("reload", ConversionUtil.toList("true"));

        mockMvc.perform(
                get("/pim/categories/" + categoryId).params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("category/category_body"))
                .andExpect(forwardedUrl("/category/category_body.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("CATEGORIES")))
                .andExpect(model().attribute("category", hasProperty("externalId", is(categoryId))));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void allTest() throws Exception {
        mockMvc.perform(
                get("/pim/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("category/categories"))
                .andExpect(forwardedUrl("/category/categories.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void all1Test() throws Exception {

        //Creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test 1", "externalId", "TEST_1", "description", "TEST 1 description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 2", "externalId", "TEST_2", "description", "TEST 2 description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 3", "externalId", "TEST_3", "description", "TEST 3 description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 4", "externalId", "TEST_4", "description", "TEST 4 description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 5", "externalId", "TEST_5", "description", "TEST 5 description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 6", "externalId", "TEST_6", "description", "TEST 6 description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 7", "externalId", "TEST_7", "description", "TEST 7 description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 8", "externalId", "TEST_8", "description", "TEST 8 description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 9", "externalId", "TEST_9", "description", "TEST 9 description", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryService.create(categoryDTO);
        });

        //Getting categories as page
        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("start", ConversionUtil.toList("0"));
        detailsParams.put("length", ConversionUtil.toList("5"));
        detailsParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/categories/data")
                        .params(detailsParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.data.size()").value(5));
        result.andExpect(jsonPath("$.draw").value(1));
        result.andExpect(jsonPath("$.recordsFiltered").value(9));
        result.andExpect(jsonPath("$.recordsTotal").value(9));

        MultiValueMap<String, String> detailsParams1 = new LinkedMultiValueMap<>();
        detailsParams1.put("start", ConversionUtil.toList("5"));
        detailsParams1.put("length", ConversionUtil.toList("5"));
        detailsParams1.put("draw", ConversionUtil.toList("2"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/categories/data")
                        .params(detailsParams1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(4));
        result1.andExpect(jsonPath("$.draw").value(2));
        result1.andExpect(jsonPath("$.recordsFiltered").value(9));
        result1.andExpect(jsonPath("$.recordsTotal").value(9));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllAsHierarchyTest() throws Exception {

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getSubCategoriesTest() throws Exception {

        //Creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Category5", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        //Adding subCategory
        categoryService.addSubCategory(categoriesData.get(3).get("externalId").toString(), FindBy.EXTERNAL_ID, categoriesData.get(4).get("externalId").toString(), FindBy.EXTERNAL_ID);

        //Getting subCategories
        MultiValueMap<String, String> detailsParams1 = new LinkedMultiValueMap<>();
        detailsParams1.put("start", ConversionUtil.toList("0"));
        detailsParams1.put("length", ConversionUtil.toList("2"));
        detailsParams1.put("draw", ConversionUtil.toList("1"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/categories/TEST_4/subCategories/data")
                        .params(detailsParams1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(1));
        result1.andExpect(jsonPath("$.draw").value(1));
        result1.andExpect(jsonPath("$.recordsFiltered").value(1));
        result1.andExpect(jsonPath("$.recordsTotal").value(1));
    }

    @Test
    public void setSubCategoriesSequenceTest() throws Exception {
    }

    @Test
    public void getCategoryProductsTest() throws Exception {
    }

    @Test
    public void setProductsSequenceTest() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void availableCategoriesTest() throws Exception {

        //Add a website instance
        List<Category> createdCategoryInstances = addCategoryInstances();
        Assert.assertFalse(createdCategoryInstances.isEmpty());

        //AvailableCatalogs with valid websiteID
        String categoryId = createdCategoryInstances.get(0).getCategoryId();
        mockMvc.perform(
                get("/pim/categories/" + categoryId + "/subCategories/available"))
                .andExpect(status().isOk())
                .andExpect(view().name("category/availableSubCategories"))
                .andExpect(forwardedUrl("/category/availableSubCategories.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAvailableSubCategoriesTest() throws Exception {

        //Creating Categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Category5", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        //Adding subCategory
        categoryService.addSubCategory(categoriesData.get(3).get("externalId").toString(), FindBy.EXTERNAL_ID, categoriesData.get(4).get("externalId").toString(), FindBy.EXTERNAL_ID);

        //Getting availableSubCategories
        MultiValueMap<String, String> detailsParams1 = new LinkedMultiValueMap<>();
        detailsParams1.put("start", ConversionUtil.toList("0"));
        detailsParams1.put("length", ConversionUtil.toList("3"));
        detailsParams1.put("draw", ConversionUtil.toList("1"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/categories/TEST_4/subCategories/available/list")
                        .params(detailsParams1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(3));
        result1.andExpect(jsonPath("$.draw").value(1));
        result1.andExpect(jsonPath("$.recordsFiltered").value(3));
        result1.andExpect(jsonPath("$.recordsTotal").value(3));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void addSubCategoryTest() throws Exception {

        //Creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        //Adding subCategoriues

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);
        Page<Category> availableSubCategories = categoryService.getAvailableSubCategoriesForCategory(category.getExternalId(), FindBy.EXTERNAL_ID, 1, 1, null, false);

        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("id", ConversionUtil.toList("TEST_1"));
        detailsParams.put("subCategoryId", ConversionUtil.toList("TEST_2"));
        ResultActions result = mockMvc.perform(
                post("/pim/categories/TEST_1/subCategories/TEST_2")
                        .params(detailsParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(1));
        result.andExpect(jsonPath("$.success").value(true));

        Page<Category> availableSubCategories1 = categoryService.getAvailableSubCategoriesForCategory(category.getExternalId(), FindBy.EXTERNAL_ID, 1, 1, null, false);
        Assert.assertEquals(availableSubCategories1.getTotalElements(), availableSubCategories.getTotalElements() - 1);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toggleSubCategoryTest() throws Exception {

        //Creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Category5", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        //Toggle
        categoryService.addSubCategory(categoriesData.get(3).get("externalId").toString(), FindBy.EXTERNAL_ID, categoriesData.get(4).get("externalId").toString(), FindBy.EXTERNAL_ID);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("id", ConversionUtil.toList("TEST_4"));
        params.put("subCategoryId", ConversionUtil.toList("TEST_5"));
        params.put("active", ConversionUtil.toList("Y"));
        ResultActions result = mockMvc.perform(
                put("/pim/categories/TEST_4/subCategories/TEST_5/active/Y")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(1));

    }

    @Test
    public void availableProductsTest() throws Exception {

    }

    @Test
    public void getAvailableProductsTest() throws Exception {
    }

    @Test
    public void addProductTest() throws Exception {
    }

    @Test
    public void toggleProductTest() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }

    private List<Category> addCategoryInstances() {
        List<Category> createdCategoryInstances = new ArrayList<>();
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test 1", "externalId", "TEST_1", "description", "Test Category 1", "active", "Y", "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 2", "externalId", "TEST_2", "description", "Test Category 2", "active", "Y", "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 3", "externalId", "TEST_3", "description", "Test Category 3", "active", "Y", "discontinued", "N"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDTO.setDiscontinued((String)categoryData.get("discontinued"));
            createdCategoryInstances.add(categoryService.create(categoryDTO));
        });
        return createdCategoryInstances;
    }
}