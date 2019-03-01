package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.RelatedCategoryDAO;
import com.bigname.pim.api.persistence.dao.RootCategoryDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CatalogServiceImplTest {

    @Autowired
    CatalogService catalogService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    RootCategoryDAO rootCategoryDAO;
    @Autowired
    CatalogDAO catalogDAO;
    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    RelatedCategoryDAO relatedCategoryDAO;

    @Before
    public void setUp() throws Exception {
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }

    @Test
    public void findAllRootCategoriesTest() throws Exception {

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

            Category category = categoryService.get((String)categoryData.get("externalId"), FindBy.EXTERNAL_ID,false).orElse(null);

            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(category.getId());
            rootCategory.setSequenceNum(0);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(category.getActive());
            rootCategoryDAO.insert(rootCategory);
        });

        Page<Map<String, Object>> rootCategories =  catalogService.findAllRootCategories(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, "categoryName", "Test", PageRequest.of(0,categoriesData.size(),null), false);
        Assert.assertEquals(rootCategories.getSize(),categoriesData.size());//TODO pagination
    }

    @Test
    public void getAllRootCategoriesTest() throws Exception {

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
        categoriesData.add(CollectionsUtil.toMap("name", "Test Category 2", "externalId", "TEST_CATEGORY_2", "description", "Test description 2", "active", "N"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);

            Category category = categoryService.get((String)categoryData.get("externalId"), FindBy.EXTERNAL_ID,false).orElse(null);

            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(category.getId());
            rootCategory.setSequenceNum(0);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(category.getActive());
            rootCategoryDAO.insert(rootCategory);
        });

        List<RootCategory> rootCategoryList = catalogService.getAllRootCategories(catalog.getId());
        Assert.assertTrue(ValidationUtil.isNotEmpty(rootCategoryList));

    }

    @Test
    public void findAvailableRootCategoriesForCatalogTest() throws Exception {

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
        });

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        RootCategory rootCategory = new RootCategory();
        rootCategory.setCatalogId(catalog.getId());
        rootCategory.setRootCategoryId(category.getId());
        rootCategory.setSequenceNum(0);
        rootCategory.setSubSequenceNum(0);
        rootCategory.setActive(category.getActive());
        rootCategoryDAO.insert(rootCategory);

        Page<Category> availableCategoriesPage = catalogService.findAvailableRootCategoriesForCatalog(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, "categoryName", "Test", PageRequest.of(0, categoriesData.size() - 1), false);
        Assert.assertEquals(availableCategoriesPage.getContent().size(), 1); //TODO pagination
    }

    @Test
    public void getAvailableRootCategoriesForCatalogTest() throws Exception {

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
        });

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        RootCategory rootCategory = new RootCategory();
        rootCategory.setCatalogId(catalog.getId());
        rootCategory.setRootCategoryId(category.getId());
        rootCategory.setSequenceNum(0);
        rootCategory.setSubSequenceNum(0);
        rootCategory.setActive(category.getActive());
        rootCategoryDAO.insert(rootCategory);

        Page<Category> availableRootCategories = catalogService.getAvailableRootCategoriesForCatalog(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,0,categoriesData.size() - 1, null, false);
        Assert.assertEquals(availableRootCategories.getContent().size(), 1); //TODO pagination
    }

    @Test
    public void toggleRootCategoryTest() throws Exception {

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
        });

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        RootCategory rootCategory = new RootCategory();
        rootCategory.setCatalogId(catalog.getId());
        rootCategory.setRootCategoryId(category.getId());
        rootCategory.setSequenceNum(0);
        rootCategory.setSubSequenceNum(0);
        rootCategory.setActive(category.getActive());
        rootCategoryDAO.insert(rootCategory);

        RootCategory rootCategory1 = rootCategoryDAO.findById(rootCategory.getId()).orElse(null);
        catalogService.toggleRootCategory(rootCategory1.getCatalogId(), FindBy.INTERNAL_ID, rootCategory1.getRootCategoryId(), FindBy.INTERNAL_ID, Toggle.get(rootCategory1.getActive()));

        RootCategory updatedRootCategory = rootCategoryDAO.findById(rootCategory.getId()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedRootCategory));

        Assert.assertEquals(updatedRootCategory.getActive(), "N");

    }

    @Test
    public void getRootCategoriesTest() throws Exception {

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

            Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(category.getId());
            rootCategory.setSequenceNum(0);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(category.getActive());
            rootCategoryDAO.insert(rootCategory);
        });

        Page<Map<String, Object>> rootCategoriesMap = catalogService.getRootCategories(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, PageRequest.of(0, categoriesData.size(), null), false);
        Assert.assertEquals(rootCategoriesMap.getSize(), categoriesData.size()); //TODO pagination
    }

    @Test
    public void getCategoryHierarchyTest() throws Exception {

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
        });

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);
        Category category1 = categoryService.get(categoriesData.get(1).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        RootCategory rootCategory = new RootCategory();
        rootCategory.setCatalogId(catalog.getId());
        rootCategory.setRootCategoryId(category.getId());
        rootCategory.setSequenceNum(0);
        rootCategory.setSubSequenceNum(0);
        rootCategory.setActive(category.getActive());
        rootCategoryDAO.insert(rootCategory);

        RelatedCategory relatedCategory = new RelatedCategory();
        relatedCategory.setCategoryId(category.getId());
        relatedCategory.setSubCategoryId(category1.getId());
        relatedCategoryDAO.insert(relatedCategory);
        RelatedCategory relatedCategory1 = relatedCategoryDAO.findById(relatedCategory.getId()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(relatedCategory1));

        List<Map<String, Object>> categoryHierarchy = catalogService.getCategoryHierarchy(catalogsData.get(0).get("externalId").toString());// TODO verify code


    }

    @Test
    public void setRootCategorySequenceTest() throws Exception {

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

            Category category = categoryService.get((String)categoryData.get("externalId"), FindBy.EXTERNAL_ID,false).orElse(null);

            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(category.getId());
            rootCategory.setSequenceNum(0);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(category.getActive());
            rootCategoryDAO.insert(rootCategory);
        });

        boolean success = catalogService.setRootCategorySequence(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, categoriesData.get(1).get("externalId").toString(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(success);
    }

    @Test
    public void addRootCategoryTest() throws Exception {

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

        RootCategory rootCategory = catalogService.addRootCategory(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID);
        RootCategory rootCategory1 = rootCategoryDAO.findById(rootCategory.getId()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(rootCategory1));
        Assert.assertEquals(rootCategory.getRootCategoryId(), rootCategory1.getRootCategoryId());
    }

    @After
    public void tearDown() throws Exception {
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }

}