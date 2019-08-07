package com.bigname.pim.api.service.impl;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.persistence.dao.mongo.CatalogDAO;
import com.bigname.pim.api.persistence.dao.mongo.CategoryDAO;
import com.bigname.pim.api.persistence.dao.mongo.RelatedCategoryDAO;
import com.bigname.pim.api.persistence.dao.mongo.RootCategoryDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.util.GenericCriteria;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.service.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CatalogServiceImplTest {
    @Autowired
    private UserService userService;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RootCategoryDAO rootCategoryDAO;

    @Autowired
    private CatalogDAO catalogDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private RelatedCategoryDAO relatedCategoryDAO;

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
            mongoTemplate = (MongoTemplate) catalogDAO.getTemplate();
        }
        mongoTemplate.dropCollection(Catalog.class);
        mongoTemplate.dropCollection(Category.class);
        rootCategoryDAO.deleteAll();
        relatedCategoryDAO.deleteAll();
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAllRootCategoriesTest() throws Exception {
        //creating catalogs
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

        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),false).orElse(null);
        //creating categories
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

            Category category = categoryService.get(ID.EXTERNAL_ID((String) categoryData.get("externalId")),false).orElse(null);

            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(category.getId());
            rootCategory.setSequenceNum(0);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(category.getActive());
            rootCategoryDAO.insert(rootCategory);
        });
        //Getting rootCategories
        Page<Map<String, Object>> rootCategories =  catalogService.findAllRootCategories(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), "categoryName", "Test", PageRequest.of(0,categoriesData.size(),null), false);
        Assert.assertEquals(rootCategories.getSize(),categoriesData.size());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllRootCategoriesTest() throws Exception {
        //creating catalogs
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

        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),false).orElse(null);
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "N"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);

            Category category = categoryService.get(ID.EXTERNAL_ID((String) categoryData.get("externalId")),false).orElse(null);

            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(category.getId());
            rootCategory.setSequenceNum(0);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(category.getActive());
            rootCategoryDAO.insert(rootCategory);
        });

        //Getting rootCategories
        List<RootCategory> rootCategoryList = catalogService.getAllRootCategories(catalog.getId());
        Assert.assertTrue(ValidationUtil.isNotEmpty(rootCategoryList));

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAvailableRootCategoriesForCatalogTest() throws Exception {
        //creating catalogs
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

        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),false).orElse(null);
        //creating categories
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

        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()),false).orElse(null);

        RootCategory rootCategory = new RootCategory();
        rootCategory.setCatalogId(catalog.getId());
        rootCategory.setRootCategoryId(category.getId());
        rootCategory.setSequenceNum(0);
        rootCategory.setSubSequenceNum(0);
        rootCategory.setActive(category.getActive());
        rootCategoryDAO.insert(rootCategory);
        //Getting available categories
        Page<Category> availableCategoriesPage = catalogService.findAvailableRootCategoriesForCatalog(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), "categoryName", "Test", PageRequest.of(0, categoriesData.size() - 1), false);
        Assert.assertEquals(availableCategoriesPage.getContent().size(), categoriesData.size() - 1);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAvailableRootCategoriesForCatalogTest() throws Exception {
        //creating catalogs
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

        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),false).orElse(null);
        //creating categories
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

        Page<Category> availableRootCategories1 = catalogService.getAvailableRootCategoriesForCatalog(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),0,categoriesData.size(), null, false);
        Assert.assertEquals(availableRootCategories1.getContent().size(), categoriesData.size());

        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()),false).orElse(null);
        RootCategory rootCategory = new RootCategory();
        rootCategory.setCatalogId(catalog.getId());
        rootCategory.setRootCategoryId(category.getId());
        rootCategory.setSequenceNum(0);
        rootCategory.setSubSequenceNum(0);
        rootCategory.setActive(category.getActive());
        rootCategoryDAO.insert(rootCategory);
        //Getting available categories
        Page<Category> availableRootCategories = catalogService.getAvailableRootCategoriesForCatalog(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),0,categoriesData.size() - 1, null, false);
        Assert.assertEquals(availableRootCategories.getContent().size(), categoriesData.size() - 1);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toggleRootCategoryTest() throws Exception {
        //creating catalogs
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

        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),false).orElse(null);
        //creating categories
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

        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()),false).orElse(null);

        RootCategory rootCategory = new RootCategory();
        rootCategory.setCatalogId(catalog.getId());
        rootCategory.setRootCategoryId(category.getId());
        rootCategory.setSequenceNum(0);
        rootCategory.setSubSequenceNum(0);
        rootCategory.setActive(category.getActive());
        rootCategoryDAO.insert(rootCategory);
        //Getting category by rootCategoryId
        RootCategory rootCategory1 = rootCategoryDAO.findById(rootCategory.getId()).orElse(null);
        //toggle
        catalogService.toggleRootCategory(ID.INTERNAL_ID(rootCategory1.getCatalogId()), ID.INTERNAL_ID(rootCategory1.getRootCategoryId()), Toggle.get(rootCategory1.getActive()));

        RootCategory updatedRootCategory = rootCategoryDAO.findById(rootCategory.getId()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedRootCategory));
        Assert.assertEquals(updatedRootCategory.getActive(), "N");

        catalogService.toggleRootCategory(ID.INTERNAL_ID(rootCategory1.getCatalogId()), ID.INTERNAL_ID(rootCategory1.getRootCategoryId()), Toggle.get(updatedRootCategory.getActive()));
        RootCategory updatedRootCategory1 = rootCategoryDAO.findById(rootCategory.getId()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedRootCategory));
        Assert.assertEquals(updatedRootCategory1.getActive(), "Y");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getRootCategoriesTest() throws Exception {
        //creating catalogs
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

        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),false).orElse(null);
        //creating categories
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

            Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()),false).orElse(null);

            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(category.getId());
            rootCategory.setSequenceNum(0);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(category.getActive());
            rootCategoryDAO.insert(rootCategory);
        });
        //Getting rootCategories
        Page<Map<String, Object>> rootCategoriesMap = catalogService.getRootCategories(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), PageRequest.of(0, categoriesData.size(), null), false);
        Assert.assertEquals(rootCategoriesMap.getSize(), categoriesData.size());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getCategoryHierarchyTest() throws Exception {
        //creating catalogs
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

        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),false).orElse(null);
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 1", "externalId", "TEST_CATEGORY_1", "description", "Test description 1", "active", "Y", "parent", "0", "isParent", true, "level", "0", "parentChain", ""));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "Y", "parent", "TEST_CATEGORY_1", "isParent", false,"level", "1", "parentChain", "TEST_CATEGORY_1"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 3", "externalId", "TEST_CATEGORY_3", "description", "Test description 3", "active", "Y", "parent", "TEST_CATEGORY_1", "isParent", false,"level", "1", "parentChain", "TEST_CATEGORY_1"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
        });

        Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()),false).orElse(null);

        RootCategory rootCategory = new RootCategory();
        rootCategory.setCatalogId(catalog.getId());
        rootCategory.setRootCategoryId(category.getId());
        rootCategory.setSequenceNum(0);
        rootCategory.setSubSequenceNum(0);
        rootCategory.setActive(category.getActive());
        rootCategoryDAO.insert(rootCategory);

        categoriesData.stream().skip(1).forEach(categoryData -> {
            Category category1 = categoryService.get(ID.EXTERNAL_ID(categoryData.get("externalId").toString()),false).orElse(null);
            RelatedCategory relatedCategory = new RelatedCategory();
            relatedCategory.setCategoryId(category.getId());
            relatedCategory.setSubCategoryId(category1.getId());
            relatedCategory.setActive("Y");
            relatedCategoryDAO.insert(relatedCategory);
            RelatedCategory relatedCategory1 = relatedCategoryDAO.findById(relatedCategory.getId()).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(relatedCategory1));
        });
        //Getting categoryHierarchy
        List<Map<String, Object>> categoryHierarchy = catalogService.getCategoryHierarchy(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()));

        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryHierarchy));
        Assert.assertEquals(categoryHierarchy.get(0).get("parent").toString(), categoriesData.get(0).get("parent").toString());
        Assert.assertEquals(categoryHierarchy.get(1).get("parent").toString(), categoriesData.get(1).get("parent").toString());
        Assert.assertEquals(categoryHierarchy.get(2).get("parent").toString(), categoriesData.get(2).get("parent").toString());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setRootCategorySequenceTest() throws Exception {
        //creating catalogs
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

        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),false).orElse(null);
        //creating categories
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

        //Adding rootCategory
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

        //Setting rootCategorySequence
        boolean success = catalogService.setRootCategorySequence(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(1).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()));
        Assert.assertTrue(success);
        List<RootCategory> rootCategoryList = catalogService.getAllRootCategories(catalog.getId());
        Assert.assertEquals(rootCategoryList.get(0).getSequenceNum(), 1);
        Assert.assertEquals(rootCategoryList.get(0).getSubSequenceNum(), 0);

        Assert.assertEquals(rootCategoryList.get(1).getSequenceNum(), 1);
        Assert.assertEquals(rootCategoryList.get(1).getSubSequenceNum(), 1);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void addRootCategoryTest() throws Exception {
        //creating catalogs
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
        //Adding rootCategory
        RootCategory rootCategory = catalogService.addRootCategory(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()));
        RootCategory rootCategory1 = rootCategoryDAO.findById(rootCategory.getId()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(rootCategory1));
        Assert.assertEquals(rootCategory.getRootCategoryId(), rootCategory1.getRootCategoryId());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createEntityTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogService.create(catalogDTO);
            //Getting catalog by catalogId
            Catalog newCatalog=catalogService.get(ID.EXTERNAL_ID(catalogDTO.getCatalogId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCatalog));
            Assert.assertTrue(newCatalog.diff(catalogDTO).isEmpty());
        });
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createEntitiesTest(){
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        List<Catalog> catalogDTOs = catalogsData.stream().map(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            return catalogDTO;
        }).collect(Collectors.toList());

        catalogService.create(catalogDTOs);
        //Getting catalogs
        Assert.assertEquals(catalogDAO.findAll(PageRequest.of(0, catalogDTOs.size()), false).getTotalElements(), catalogsData.size());
    }


    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toggleTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG", "description", "Catalog1 description", "active", "Y"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });

        Catalog catalogDetails = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(catalogDetails));
        //toggle
        catalogService.toggle(ID.EXTERNAL_ID(catalogDetails.getCatalogId()), Toggle.get(catalogDetails.getActive()));

        Catalog updatedCatalog = catalogService.get(ID.EXTERNAL_ID(catalogDetails.getCatalogId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedCatalog));
        Assert.assertEquals(updatedCatalog.getActive(), "N");

        catalogService.toggle(ID.EXTERNAL_ID(catalogDetails.getCatalogId()), Toggle.get(updatedCatalog.getActive()));
        Catalog updatedCatalog1 = catalogService.get(ID.EXTERNAL_ID(catalogDetails.getCatalogId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedCatalog1));
        Assert.assertEquals(updatedCatalog1.getActive(), "Y");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);

            //Getting catalog
            Catalog catalogDetails = catalogService.get(ID.EXTERNAL_ID(catalogDTO.getCatalogId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(catalogDetails));
            Map<String, Object> diff = catalogDTO.diff(catalogDetails);
            Assert.assertEquals(diff.size(), 0);
        });
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllAsPageTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });
        //Getting catalogs as Page
        Page<Catalog> paginatedResult = catalogService.getAll(0, 7, null,false);
        Assert.assertEquals(paginatedResult.getContent().size(), catalogsData.size());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllAsListTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });
        // sorting : ascending
        List<Catalog> result = catalogService.getAll(Sort.by("catalogName").ascending(), false);
        String[] actual = result.stream().map(catalog -> catalog.getCatalogName()).collect(Collectors.toList()).toArray(new String[0]);
        String[] expected = catalogsData.stream().map(catalogData -> (String)catalogData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertArrayEquals(expected, actual);

        //creating catalog
        catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });

        // sorting : Descending
        result = catalogService.getAll(Sort.by("catalogName").descending(), false);
        actual = result.stream().map(catalog -> catalog.getCatalogName()).collect(Collectors.toList()).toArray(new String[0]);
        expected = catalogsData.stream().map(catalogData -> (String)catalogData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertNotEquals(expected, actual);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllWithIdsAsPageTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });

        String[] ids = {catalogsData.get(0).get("externalId").toString(), catalogsData.get(1).get("externalId").toString(), catalogsData.get(2).get("externalId").toString()};
        //Getting catalogs by Ids
        Page<Catalog> paginatedResult = catalogService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), 0, 10, null, false);
        Map<String, Catalog> catalogsMap = paginatedResult.getContent().stream().collect(Collectors.toMap(catalog -> catalog.getCatalogId(), catalog -> catalog));
        Assert.assertTrue(catalogsMap.size() == ids.length && catalogsMap.containsKey(ids[0]) && catalogsMap.containsKey(ids[1]) && catalogsMap.containsKey(ids[2]));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllWithIdsAsListTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });

        String[] ids = {catalogsData.get(0).get("externalId").toString(), catalogsData.get(1).get("externalId").toString(), catalogsData.get(2).get("externalId").toString()};
        //Getting catalogs by ids
        List<Catalog> paginatedResult = catalogService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, Catalog> catalogsMap = paginatedResult.stream().collect(Collectors.toMap(catalog -> catalog.getCatalogId(), catalog -> catalog));
        Assert.assertTrue(catalogsMap.size() == ids.length && catalogsMap.containsKey(ids[0]) && catalogsMap.containsKey(ids[1]) && catalogsMap.containsKey(ids[2]));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllWithExclusionsAsPageTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });

        String[] ids = {catalogsData.get(0).get("externalId").toString(), catalogsData.get(1).get("externalId").toString(), catalogsData.get(2).get("externalId").toString()};
        //Getting catalogs with exclude Ids
        Page<Catalog> paginatedResult = catalogService.getAllWithExclusions(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), 0, 10, null, false);
        Map<String, Catalog> catalogsMap = paginatedResult.getContent().stream().collect(Collectors.toMap(catalog -> catalog.getCatalogId(), catalog -> catalog));
        Assert.assertTrue(catalogsMap.size() == (catalogsData.size() - ids.length) && !catalogsMap.containsKey(ids[0]) && !catalogsMap.containsKey(ids[1]) && !catalogsMap.containsKey(ids[2]));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllWithExclusionsAsListTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });

        String[] ids = {catalogsData.get(0).get("externalId").toString(), catalogsData.get(1).get("externalId").toString(), catalogsData.get(2).get("externalId").toString()};
        //Getting catalogs with exclude Ids
        List<Catalog> paginatedResult = catalogService.getAllWithExclusions(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, Catalog> catalogssMap = paginatedResult.stream().collect(Collectors.toMap(catalog -> catalog.getCatalogId(), catalog -> catalog));
        Assert.assertTrue(catalogssMap.size() == (catalogsData.size() - ids.length) && !catalogssMap.containsKey(ids[0]) && !catalogssMap.containsKey(ids[1]) && !catalogssMap.containsKey(ids[2]));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAllAtSearchTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });
        //Getting catalogs by searchField
        long size = catalogsData.stream().filter(x -> x.get("active").equals("Y")).count();
        Page<Catalog> paginatedResult = catalogService.findAll("name", "Test", PageRequest.of(0, catalogsData.size()), true);
        Assert.assertEquals(paginatedResult.getContent().size(), size);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAll1Test() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });
        //Getting catalogs
        long size = catalogsData.stream().filter(x -> x.get("active").equals("Y")).count();
        Page<Catalog> paginatedResult = catalogService.findAll(PageRequest.of(0, catalogsData.size()), true);
        Assert.assertEquals(paginatedResult.getContent().size(), size);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void updateEntityTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);

            Catalog catalogDetails = catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(catalogDetails));
            //updating catalog
            catalogDetails.setDescription("Catalog description");
            catalogDetails.setGroup("DETAILS");

            //Getting updated catalog
            catalogService.update(ID.EXTERNAL_ID(catalogDetails.getCatalogId()), catalogDetails);
            Catalog updatedCatalog = catalogService.get(ID.EXTERNAL_ID(catalogDetails.getCatalogId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(updatedCatalog));
            Map<String, Object> diff = catalogDTO.diff(updatedCatalog);
            Assert.assertEquals(diff.size(), 1);
            Assert.assertEquals(diff.get("description"), "Catalog description");
        });

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void updateEntitiesTest(){
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "Y"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });

        String[] ids = {catalogsData.get(0).get("externalId").toString(), catalogsData.get(1).get("externalId").toString(), catalogsData.get(2).get("externalId").toString()};

        List<Catalog> result = catalogService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, Catalog> catalogsMap = result.stream().collect(Collectors.toMap(catalog -> catalog.getCatalogId(), catalog -> catalog));
        Assert.assertTrue(catalogsMap.size() == ids.length && catalogsMap.containsKey(ids[0]) && catalogsMap.containsKey(ids[1]) && catalogsMap.containsKey(ids[2]));
        //updating catalogs
        List<Catalog> catalogs = result.stream().map(result1 -> {
            result1.setActive("N");
            return result1;
        }).collect(Collectors.toList());
        catalogService.update(catalogs);

        //Getting updated catalogs
        result = catalogService.getAll(Sort.by("catalogName").descending(), true);
        catalogsMap = result.stream().collect(Collectors.toMap(catalog -> catalog.getCatalogId(), catalog -> catalog));
        Assert.assertTrue(catalogsMap.size() == (catalogsData.size() - ids.length) && !catalogsMap.containsKey(ids[0]) && !catalogsMap.containsKey(ids[1]) && !catalogsMap.containsKey(ids[2]));
        Assert.assertFalse(catalogsMap.size() == catalogsData.size() && catalogsMap.containsKey(ids[0]) && catalogsMap.containsKey(ids[1]) && catalogsMap.containsKey(ids[2]));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void cloneInstanceTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
            //Getting catalog
            Catalog newCatalog = catalogService.get(ID.EXTERNAL_ID(catalogDTO.getCatalogId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCatalog));
            Assert.assertTrue(newCatalog.diff(catalogDTO).isEmpty());
            //cloning catalog instance
            Catalog catalogClone = catalogService.cloneInstance(ID.EXTERNAL_ID(newCatalog.getCatalogId()), Entity.CloneType.LIGHT);
            Assert.assertTrue(catalogClone.getCatalogId() .equals(newCatalog.getCatalogId() + "_COPY") && catalogClone.getCatalogName().equals(newCatalog.getCatalogName() + "_COPY") && catalogClone.getDescription().equals(newCatalog.getDescription() + "_COPY") && catalogClone.getActive() != newCatalog.getActive());
        });
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAllTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });
        //Getting catalogs
        long size = catalogsData.stream().filter(x -> x.get("active").equals("N")).count();
        List<Catalog> result = catalogService.findAll(CollectionsUtil.toMap("active", "N"), false);
        Assert.assertTrue(result.size() == size);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAll2Test() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });
        //Getting catalogs
        long size = catalogsData.stream().filter(x -> x.get("active").equals("N")).count();
        GenericCriteria criteria = PlatformUtil.buildCriteria(CollectionsUtil.toMap("active", "N"));
        List<Catalog> result = catalogService.findAll(criteria, false);
        Assert.assertTrue(result.size() == size);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findOneTest() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });
        //Getting catalog
        Optional<Catalog> result = catalogService.findOne(CollectionsUtil.toMap("catalogName", catalogsData.get(0).get("name")));
        Assert.assertEquals(catalogsData.get(0).get("name"), result.get().getCatalogName());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findOne1Test() {
        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog1", "externalId", "TEST_CATALOG_1", "description", "Catalog1 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog2", "externalId", "TEST_CATALOG_2", "description", "Catalog2 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog3", "externalId", "TEST_CATALOG_3", "description", "Catalog3 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog4", "externalId", "TEST_CATALOG_4", "description", "Catalog4 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog5", "externalId", "TEST_CATALOG_5", "description", "Catalog5 description", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog6", "externalId", "TEST_CATALOG_6", "description", "Catalog6 description", "active", "N"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog7", "externalId", "TEST_CATALOG_7", "description", "Catalog7 description", "active", "N"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogDAO.insert(catalogDTO);
        });
        //Getting catalog
        GenericCriteria criteria = PlatformUtil.buildCriteria(CollectionsUtil.toMap("catalogName", catalogsData.get(0).get("name")));
        Optional<Catalog> result = catalogService.findOne(criteria);
        Assert.assertEquals(catalogsData.get(0).get("name"), result.get().getCatalogName());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void validate1Test() throws Exception {
        /* Create a valid new instance with id CATALOG_TEST */
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("Catalog Test");
        catalogDTO.setCatalogId("CATALOG_TEST");
        catalogDTO.setDescription("Catalog description");
        catalogDTO.setActive("Y");

        Map<String, Object> context = new HashMap<>();

        Class groups = ValidatableEntity.CreateGroup.class;
//        validate
        Assert.assertTrue(catalogService.validate(catalogDTO, context, groups).isEmpty());
//        insert the valid instance
        catalogDAO.insert(catalogDTO);

        /*Create a second instance with the same id CATALOG_TEST to check the unique constraint violation of catalogId*/

        Catalog catalogDTO1 = new Catalog();
        catalogDTO1.setCatalogName("Envelope");
        catalogDTO1.setCatalogId("CATALOG_TEST");
        catalogDTO1.setDescription("Catalog1 description");
        catalogDTO1.setActive("Y");

        Assert.assertEquals(catalogService.validate(catalogDTO1, context, groups).size(), 1);

        /*Testing forceUniqueId*/
        context.put("forceUniqueId", true);
        Assert.assertTrue(catalogService.validate(catalogDTO1, context, groups).isEmpty());
        Assert.assertEquals(catalogDTO1.getExternalId(), "CATALOG_TEST_1");
        catalogDAO.insert(catalogDTO1);

        context.clear();

        /*Testing uniqueConstraint violation of catalogId with update operation*/
        Catalog catalog = catalogDAO.findById(ID.EXTERNAL_ID(catalogDTO.getCatalogId())).orElse(null);
        catalog.setCatalogName("Envelope");
        catalog.setCatalogId("CATALOG_TEST_1");
        catalog.setDescription("Catalog1 description");
        catalog.setActive("Y");
        context.put("id", catalogDTO.getExternalId());

        groups = ValidatableEntity.DetailsGroup.class;
        Assert.assertEquals(catalogService.validate(catalog, context, groups).size(), 1);

        /*Testing forceUniqueId with update operation*/
        context.put("forceUniqueId", true);
        Assert.assertTrue(catalogService.validate(catalog, context, groups).isEmpty());
        Assert.assertEquals(catalog.getExternalId(), "CATALOG_TEST_1_1");
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Catalog.class);
        mongoTemplate.dropCollection(Category.class);
        rootCategoryDAO.deleteAll();
        relatedCategoryDAO.deleteAll();
    }

}