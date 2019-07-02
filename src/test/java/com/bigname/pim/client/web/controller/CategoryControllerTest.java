package com.bigname.pim.client.web.controller;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.*;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
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
import org.springframework.data.domain.PageRequest;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private RelatedCategoryDAO relatedCategoryDAO;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private ProductService productService;

    @Autowired
    private FamilyDAO familyDAO;

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private CategoryProductDAO categoryProductDAO;

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
            mongoTemplate = ((GenericRepositoryImpl)categoryDAO).getMongoTemplate();
        }
        mongoTemplate.dropCollection(Category.class);
        relatedCategoryDAO.deleteAll();
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Family.class);
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

        //Add a category instance
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
        categoryService.addSubCategory(ID.EXTERNAL_ID(createdCategoryInstances.get(0).getCategoryId()), ID.EXTERNAL_ID(createdCategoryInstances.get(1).getCategoryId()));
        Category category = categoryService.get(ID.EXTERNAL_ID(createdCategoryInstances.get(0).getCategoryId()), false).orElse(null);

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
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test 1", "externalId", "TEST_1", "description", "Test Category1", "active", "Y", "parent", "0", "isParent", "true", "level", "0", "parentChain", ""));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 2", "externalId", "TEST_2", "description", "Test Category2", "active", "Y", "parent", "TEST_1", "isParent", "true", "level", "1", "parentChain", "TEST_1"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 3", "externalId", "TEST_3", "description", "Test Category3", "active", "Y", "parent", "TEST_2", "isParent", "true", "level", "2", "parentChain", "TEST_1|TEST_2"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 4", "externalId", "TEST_4", "description", "Test Category4", "active", "Y", "parent", "TEST_3", "isParent", "true", "level", "3", "parentChain", "TEST_1|TEST_2|TEST_3"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 5", "externalId", "TEST_5", "description", "Test Category5", "active", "Y", "parent", "TEST_4", "isParent", "false", "level", "4", "parentChain", "TEST_1|TEST_2|TEST_3|TEST_4"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test 6", "externalId", "TEST_6", "description", "Test Category6", "active", "Y", "parent", "TEST_3", "isParent", "false", "level", "3", "parentChain", "TEST_1|TEST_2|TEST_3"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));

            categoryService.create(categoryDTO);

            Category newCategory = categoryService.get(ID.EXTERNAL_ID(categoryDTO.getCategoryId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));
            Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());
        });

        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(1).get("externalId").toString()));
        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(1).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(2).get("externalId").toString()));
        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(2).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(3).get("externalId").toString()));
        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(3).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(4).get("externalId").toString()));
        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(2).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(5).get("externalId").toString()));

        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("start", ConversionUtil.toList("0"));
        detailsParams.put("length", ConversionUtil.toList("4"));
        detailsParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/categories/hierarchy")
                        .params(detailsParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[0].parent").value(0));
        result.andExpect(jsonPath("$[0].isParent").value("true"));
        result.andExpect(jsonPath("$[0].level").value(0));
        result.andExpect(jsonPath("$[0].parentChain").value(""));

        result.andExpect(jsonPath("$[1].parent").value("TEST_1"));
        result.andExpect(jsonPath("$[1].isParent").value("true"));
        result.andExpect(jsonPath("$[1].level").value(1));
        result.andExpect(jsonPath("$[1].parentChain").value("TEST_1"));

        result.andExpect(jsonPath("$[2].parent").value("TEST_2"));
        result.andExpect(jsonPath("$[2].isParent").value("true"));
        result.andExpect(jsonPath("$[2].level").value(2));
        result.andExpect(jsonPath("$[2].parentChain").value("TEST_1|TEST_2"));

        result.andExpect(jsonPath("$[3].parent").value("TEST_3"));
        result.andExpect(jsonPath("$[3].isParent").value("false"));
        result.andExpect(jsonPath("$[3].level").value(3));
        result.andExpect(jsonPath("$[3].parentChain").value("TEST_1|TEST_2|TEST_3"));

        result.andExpect(jsonPath("$[4].parent").value("TEST_3"));
        result.andExpect(jsonPath("$[4].isParent").value("true"));
        result.andExpect(jsonPath("$[4].level").value(3));
        result.andExpect(jsonPath("$[4].parentChain").value("TEST_1|TEST_2|TEST_3"));

        result.andExpect(jsonPath("$[5].parent").value("TEST_4"));
        result.andExpect(jsonPath("$[5].isParent").value("false"));
        result.andExpect(jsonPath("$[5].level").value(4));
        result.andExpect(jsonPath("$[5].parentChain").value("TEST_1|TEST_2|TEST_3|TEST_4"));
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
        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(3).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(4).get("externalId").toString()));

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

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setSubCategoriesSequenceTest() throws Exception {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "Test Category 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "Test Category 2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "description", "Test Category 3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "description", "Test Category 4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "description", "Test Category 5", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "description", "Test Category 6", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "description", "Test Category 7", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "description", "Test Category 8", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        //adding subCategories
        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), false).orElse(null);

        String[] ids = {category.getCategoryId()};

        List<Category> categories = categoryService.getAllWithExclusions(Arrays.stream(ids).map(e -> ID.EXTERNAL_ID(e)).collect(Collectors.toList()), null, false);
        int count[] = {1};
        categories.forEach(relatedCategoryData -> {
            //categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategoryData.getExternalId(), FindBy.EXTERNAL_ID);

            RelatedCategory relatedCategory = new RelatedCategory();
            relatedCategory.setCategoryId(category.getId());
            relatedCategory.setSubCategoryId(relatedCategoryData.getId());
            relatedCategory.setFullSubCategoryId("|" + relatedCategoryData);
            relatedCategory.setSequenceNum(count[0]);
            relatedCategory.setSubSequenceNum(0);
            relatedCategory.setActive("Y");
            relatedCategoryDAO.insert(relatedCategory);
            count[0] ++;
        });

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("sourceId", ConversionUtil.toList(categories.get(1).getCategoryId()));
        params.put("destinationId", ConversionUtil.toList(categories.get(0).getCategoryId()));

        //sequencing
        ResultActions result = mockMvc.perform(
                put("/pim/categories/TEST_1/subCategories/data")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(jsonPath("$.success").value(true));

        Page<Map<String, Object>> subCategories = categoryService.getSubCategories(ID.EXTERNAL_ID(category.getCategoryId()), PageRequest.of(0, categoriesData.size(), null), false);
        List<Map<String, Object>> subCategoriesList = subCategories.getContent();

        long actualValue1 = 1;
        Assert.assertEquals(subCategoriesList.get(0).get("subSequenceNum"), 1);
        Assert.assertEquals(subCategoriesList.get(1).get("subSequenceNum"), 0);
        Assert.assertEquals(subCategoriesList.get(0).get("sequenceNum"), actualValue1);
        Assert.assertEquals(subCategoriesList.get(1).get("sequenceNum"), actualValue1);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getCategoryProductsTest() throws Exception {
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 2", "externalId", "TEST_PRODUCT_2", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 3", "externalId", "TEST_PRODUCT_3", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);

            Product product = productService.get(ID.EXTERNAL_ID(productData.get("externalId").toString()), false).orElse(null);
            categoryService.addProduct(ID.EXTERNAL_ID(category.getCategoryId()), ID.EXTERNAL_ID(product.getProductId()));
        });

        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("start", ConversionUtil.toList("0"));
        detailParams.put("length", ConversionUtil.toList("3"));
        detailParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/categories/TEST_1/products/data")
                        .params(detailParams)
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
    public void setProductsSequenceTest() throws Exception {

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 2", "externalId", "TEST_PRODUCT_2", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 3", "externalId", "TEST_PRODUCT_3", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 4", "externalId", "TEST_PRODUCT_4", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 5", "externalId", "TEST_PRODUCT_5", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 6", "externalId", "TEST_PRODUCT_6", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 7", "externalId", "TEST_PRODUCT_7", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        List<Product> productList = productService.getAll(null, false);
        int count[] = {1};
        productList.forEach(getCategoryProduct -> {
            CategoryProduct categoryProduct = new CategoryProduct();
            categoryProduct.setCategoryId(category.getId());
            categoryProduct.setProductId(getCategoryProduct.getId());
            categoryProduct.setSequenceNum(count[0]);
            categoryProduct.setSubSequenceNum(0);
            categoryProduct.setActive("Y");
            categoryProductDAO.insert(categoryProduct);
            count[0] ++;
        });

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("sourceId", ConversionUtil.toList(productsData.get(1).get("externalId").toString()));
        params.put("destinationId", ConversionUtil.toList(productsData.get(0).get("externalId").toString()));

        ResultActions result = mockMvc.perform(
                put("/pim/categories/TEST_1/products/data")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(jsonPath("$.success").value(true));

        List<CategoryProduct> categoryProducts = categoryService.getAllCategoryProducts(ID.EXTERNAL_ID(category.getId()));
        Assert.assertEquals(categoryProducts.get(0).getSequenceNum(), 1);
        Assert.assertEquals(categoryProducts.get(0).getSubSequenceNum(), 0);

        Assert.assertEquals(categoryProducts.get(1).getSequenceNum(), 1);
        Assert.assertEquals(categoryProducts.get(1).getSubSequenceNum(), 1);

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void availableCategoriesTest() throws Exception {

        //Add a category instance
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
        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(3).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(4).get("externalId").toString()));

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

        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), false).orElse(null);
        Page<Category> availableSubCategories = categoryService.getAvailableSubCategoriesForCategory(ID.EXTERNAL_ID(category.getExternalId()), 1, 1, null, false);

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

        Page<Category> availableSubCategories1 = categoryService.getAvailableSubCategoriesForCategory(ID.EXTERNAL_ID(category.getExternalId()), 1, 1, null, false);
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
        categoryService.addSubCategory(ID.EXTERNAL_ID(categoriesData.get(3).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(4).get("externalId").toString()));
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

        Page<Map<String, Object>> relatedCategories = categoryService.getSubCategories(ID.EXTERNAL_ID(categoriesData.get(3).get("externalId").toString()), PageRequest.of(0, categoriesData.size(), null), false);
        List<Map<String, Object>> relatedCategoriesList = relatedCategories.getContent();
        Assert.assertEquals(relatedCategoriesList.get(0).get("active"), "N");

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void availableProductsTest() throws Exception {
        //Add  categories instance
        List<Category> createdCategoryInstances = addCategoryInstances();
        Assert.assertFalse(createdCategoryInstances.isEmpty());

        //Details mode with valid categoryID
        String categoryId = createdCategoryInstances.get(0).getCategoryId();
        mockMvc.perform(
                get("/pim/categories/" + categoryId + "/products/available"))
                .andExpect(status().isOk())
                .andExpect(view().name("product/availableProducts"))
                .andExpect(forwardedUrl("/product/availableProducts.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAvailableProductsTest() throws Exception {

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 2", "externalId", "TEST_PRODUCT_2", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 3", "externalId", "TEST_PRODUCT_3", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);
        categoryService.addProduct(ID.EXTERNAL_ID(category.getCategoryId()), ID.EXTERNAL_ID(product.getProductId()));

        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("start", ConversionUtil.toList("0"));
        detailParams.put("length", ConversionUtil.toList("4"));
        detailParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/categories/TEST_1/products/available/list")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.data.size()").value(2));
        result.andExpect(jsonPath("$.draw").value(1));
        result.andExpect(jsonPath("$.recordsFiltered").value(2));
        result.andExpect(jsonPath("$.recordsTotal").value(2));

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void addProductTest() throws Exception {
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 2", "externalId", "TEST_PRODUCT_2", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 3", "externalId", "TEST_PRODUCT_3", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Page<Product> categoryProducts = categoryService.getAvailableProductsForCategory(ID.EXTERNAL_ID(category.getCategoryId()), 0, 1, null, false);

        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("id", ConversionUtil.toList("TEST_1"));
        detailParams.put("productId", ConversionUtil.toList("TEST_PRODUCT_1"));
        ResultActions result1 = mockMvc.perform(
                post("/pim/categories/TEST_1/products/TEST_PRODUCT_1")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));


        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.size()").value(1));
        result1.andExpect(jsonPath("$.success").value(true));

        Page<Product> categoryProducts1 = categoryService.getAvailableProductsForCategory(ID.EXTERNAL_ID(category.getCategoryId()), 0, 1, null, false);
        Assert.assertEquals(categoryProducts1.getTotalElements(), categoryProducts.getTotalElements() - 1);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toggleProductTest() throws Exception {
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), false).orElse(null);

        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 2", "externalId", "TEST_PRODUCT_2", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 3", "externalId", "TEST_PRODUCT_3", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);

            Product product = productService.get(ID.EXTERNAL_ID(productData.get("externalId").toString()), false).orElse(null);
            categoryService.addProduct(ID.EXTERNAL_ID(category.getCategoryId()), ID.EXTERNAL_ID(product.getProductId()));
        });

        MultiValueMap<String, String> detailParams = new LinkedMultiValueMap<>();
        detailParams.put("id", ConversionUtil.toList("TEST_CATALOG_MAIN"));
        detailParams.put("rootCategoryId", ConversionUtil.toList("TEST_CATEGORY_1"));
        detailParams.put("active", ConversionUtil.toList("Y"));
        ResultActions result = mockMvc.perform(
                put("/pim/categories/TEST_1/products/TEST_PRODUCT_1/active/Y")
                        .params(detailParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(1));
        result.andExpect(jsonPath("$.success").value(true));

        Page<Map<String, Object>> categoryProduct = categoryService.getCategoryProducts(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), PageRequest.of(0, productsData.size(), null), false);
        List<Map<String, Object>> categoryProductList = categoryProduct.getContent();

        Assert.assertEquals(categoryProductList.get(2).get("active"), "N");
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Category.class);
        relatedCategoryDAO.deleteAll();
        mongoTemplate.dropCollection(Family.class);
        mongoTemplate.dropCollection(Product.class);

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