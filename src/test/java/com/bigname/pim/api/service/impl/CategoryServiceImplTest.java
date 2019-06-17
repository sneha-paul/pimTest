package com.bigname.pim.api.service.impl;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.*;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PimUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.util.FindBy;
import com.m7.xtreme.xcore.util.Toggle;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;

import static com.m7.xtreme.xcore.util.FindBy.EXTERNAL_ID;


/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private RelatedCategoryDAO relatedCategoryDAO;

    @Autowired
    private FamilyDAO familyDAO;

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryProductDAO categoryProductDAO;

    @Before
    public void setUp() throws Exception {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
        relatedCategoryDAO.deleteAll();
        categoryProductDAO.deleteAll();
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        productDAO.getMongoTemplate().dropCollection(Product.class);
    }

    @Test
    public void findAllSubCategoriesTest() throws Exception {
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
            //Getting category
            Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));
            Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());
        });
        //Adding subCategory
        RelatedCategory newRelatedCategory1 = categoryService.addSubCategory(categoriesData.get(3).get("externalId").toString(), FindBy.EXTERNAL_ID, categoriesData.get(4).get("externalId").toString(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory1));

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        String[] ids = {category.getCategoryId(), categoriesData.get(3).get("externalId").toString(), categoriesData.get(4).get("externalId").toString()};

        List<Category> categories = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);

        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());
        //Getting subCategories
        Page<Map<String, Object>> paginatedResult = categoryService.findAllSubCategories(category.getExternalId(), FindBy.EXTERNAL_ID, "description", "Test", PageRequest.of(0, newRelatedCategories.size(), null), false);
        Assert.assertEquals(paginatedResult.getSize(), newRelatedCategories.size());
    }

    @Test
    public void findAvailableSubCategoriesForCategoryTest() throws Exception {
        //creating categories
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

            Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));
            Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());
        });
        //Adding subCategory
        RelatedCategory newRelatedCategory1 = categoryService.addSubCategory(categoriesData.get(3).get("externalId").toString(), FindBy.EXTERNAL_ID, categoriesData.get(4).get("externalId").toString(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory1));

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        String[] ids = {category.getCategoryId(), categoriesData.get(3).get("externalId").toString(), categoriesData.get(4).get("externalId").toString()};

        List<Category> categories = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);

        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());
        //Getting available subCategories
        Page<Category> paginatedResult = categoryService.findAvailableSubCategoriesForCategory(category.getExternalId(), FindBy.EXTERNAL_ID, "description", "Test", PageRequest.of(0, 2, Sort.Direction.ASC, category.getCategoryName()), false);
        Assert.assertEquals(paginatedResult.getSize(), (categoriesData.size()-1) - newRelatedCategories.size());
    }

    @Test
    public void getCategoryHierarchyTest() throws Exception {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y", "parent", "0", "isParent", "true", "level", "0", "parentChain", ""));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y", "parent", "TEST_1", "isParent", "false", "level", "1", "parentChain", "TEST_1"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y", "parent", "TEST_1", "isParent", "false", "level", "1", "parentChain", "TEST_1"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y", "parent", "TEST_1", "isParent", "false", "level", "1", "parentChain", "TEST_1"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y", "parent", "TEST_1", "isParent", "false", "level", "1", "parentChain", "TEST_1"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));

            categoryService.create(categoryDTO);

            Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));
            Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());
        });
        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        String[] ids = {category.getCategoryId()};
        //Getting categories by ids
        List<Category> categories = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);

        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());
        //Getting categoryHierarchy
        List<Map<String, Object>> subCategories = categoryService.getCategoryHierarchy(false);
        Assert.assertTrue(ValidationUtil.isNotEmpty(subCategories));
        Assert.assertEquals(subCategories.get(0).get("parent"), categoriesData.get(0).get("parent"));
        Assert.assertEquals(subCategories.get(0).get("level").toString(), categoriesData.get(0).get("level"));
        Assert.assertEquals(subCategories.get(1).get("parent"), categoriesData.get(1).get("parent"));
        Assert.assertEquals(subCategories.get(1).get("parentChain"), categoriesData.get(1).get("parentChain"));
    }

    @Test
    public void getAvailableSubCategoriesForCategoryTest() throws Exception {
        //creating categories
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

            Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));
            Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());
        });
        //Adding subCategory
        RelatedCategory newRelatedCategory1 = categoryService.addSubCategory(categoriesData.get(3).get("externalId").toString(), FindBy.EXTERNAL_ID, categoriesData.get(4).get("externalId").toString(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory1));

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        String[] ids = {category.getCategoryId(), categoriesData.get(3).get("externalId").toString(), categoriesData.get(4).get("externalId").toString()};

        List<Category> categories = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);

        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());
        //Getting availableSubCategoriesForCategory
        Page<Category> paginatedResult = categoryService.getAvailableSubCategoriesForCategory(category.getExternalId(), FindBy.EXTERNAL_ID, 0, 2, null, false);
        Assert.assertEquals(paginatedResult.getSize(), (categoriesData.size()-1) - newRelatedCategories.size());
    }

    @Test
    public void getSubCategoriesTest() throws Exception {
        //creating categories
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

            Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));
            Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());
        });
        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        String[] ids = {category.getCategoryId()};

        List<Category> categories = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);
        //Adding subCategory
        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());
        //Getting subCategories
        Page<Map<String, Object>> subCategories = categoryService.getSubCategories(category.getExternalId(), EXTERNAL_ID, PageRequest.of(0, newRelatedCategories.size(), null), false);
        Assert.assertEquals(subCategories.getSize(), newRelatedCategories.size());
    }

    @Test
    public void setSubCategorySequenceTest() throws Exception {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));

            categoryService.create(categoryDTO);

            Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));
            Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());
        });
        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        String[] ids = {category.getCategoryId()};
        // Getting Categories with exclude ids
        List<Category> categories = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);

        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());
        //setting subCategorySequence
        boolean success = categoryService.setSubCategorySequence(category.getExternalId(), FindBy.EXTERNAL_ID, categoriesData.get(1).get("externalId").toString(), FindBy.EXTERNAL_ID, categoriesData.get(4).get("externalId").toString(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(success);
    }

    @Test
    public void addSubCategoryTest() throws Exception {
        //creating categories
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

            Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));
            Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());
        });
        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        String[] ids = {category.getCategoryId()};

        List<Category> categories = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);
        //Adding subCategory
        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());
    }

    @Test
    public void toggleSubCategoryTest() throws Exception {
        //creating categories
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

            Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));
            Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());
        });
        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        String[] ids = {category.getCategoryId()};
        //Getting Categories with exclude ids
        List<Category> categories = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);

        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());
        //toggle
        boolean success = categoryService.toggleSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, categoriesData.get(1).get("externalId").toString(), FindBy.EXTERNAL_ID, Toggle.get("Y"));
        Assert.assertTrue(success);
        List<RelatedCategory> relatedCategories = relatedCategoryDAO.findBySubCategoryId(newRelatedCategories.get(0).getSubCategoryId());
        Assert.assertEquals(relatedCategories.get(0).getSubCategoryId(), newRelatedCategories.get(0).getSubCategoryId());
        Assert.assertEquals(relatedCategories.get(0).getActive(), "N");


        boolean activeSuccess = categoryService.toggleSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, categoriesData.get(1).get("externalId").toString(), FindBy.EXTERNAL_ID, Toggle.get("N"));
        Assert.assertTrue(activeSuccess);
        List<RelatedCategory> relatedCategories1 = relatedCategoryDAO.findBySubCategoryId(newRelatedCategories.get(0).getSubCategoryId());
        Assert.assertEquals(relatedCategories1.get(0).getSubCategoryId(), newRelatedCategories.get(0).getSubCategoryId());
        Assert.assertEquals(relatedCategories1.get(0).getActive(), "Y");
    }

    @Test
    public void findAvailableProductsForCategoryTest() throws Exception {
        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categories
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

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 2", "externalId", "TEST_PRODUCT_2", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Product 3", "externalId", "PRODUCT_3", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId(productData.get("productFamilyId").toString());
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categoryProduct
        CategoryProduct categoryProduct = new CategoryProduct();
        categoryProduct.setCategoryId(category.getId());
        categoryProduct.setProductId(product.getId());
        categoryProduct.setSequenceNum(0);
        categoryProduct.setSubSequenceNum(0);
        categoryProduct.setActive(product.getActive());
        categoryProductDAO.insert(categoryProduct);
        //Getting availableProducts
        Page<Product> availableProducts = categoryService.findAvailableProductsForCategory(category.getId(), FindBy.INTERNAL_ID, "productName", "Test", PageRequest.of(0,  productsData.size()),  false);
        Assert.assertEquals(availableProducts.getContent().size(), 1);
    }

    @Test
    public void setProductSequenceTest() throws Exception {
        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categories
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

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 2", "externalId", "TEST_PRODUCT_2", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 3", "externalId", "TEST_PRODUCT_3", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId(productData.get("productFamilyId").toString());
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        List<Product> product = productService.getAll(null, false);
        //creating categoryProduct
        int count[] = {1};
        product.forEach(productData -> {
            CategoryProduct categoryProduct = new CategoryProduct();
            categoryProduct.setCategoryId(category.getId());
            categoryProduct.setProductId(productData.getId());
            categoryProduct.setSequenceNum(count[0]);
            categoryProduct.setSubSequenceNum(0);
            categoryProduct.setActive(productData.getActive());
            categoryProductDAO.insert(categoryProduct);
            count[0] ++;
        });
        //setting Sequence number
        boolean success = categoryService.setProductSequence(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, productsData.get(1).get("externalId").toString(), FindBy.EXTERNAL_ID, productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID);

        Assert.assertTrue(success);

        List<CategoryProduct> categoryProducts = categoryService.getAllCategoryProducts(category.getId());
        Assert.assertEquals(categoryProducts.get(0).getSequenceNum(), 1);
        Assert.assertEquals(categoryProducts.get(0).getSubSequenceNum(), 0);

        Assert.assertEquals(categoryProducts.get(1).getSequenceNum(), 1);
        Assert.assertEquals(categoryProducts.get(1).getSubSequenceNum(), 1);

    }

    @Test
    public void toggleProductTest() throws Exception {
        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categories
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

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId(productData.get("productFamilyId").toString());
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categoryProduct
        CategoryProduct categoryProduct = new CategoryProduct();
        categoryProduct.setCategoryId(category.getId());
        categoryProduct.setProductId(product.getId());
        categoryProduct.setSequenceNum(0);
        categoryProduct.setSubSequenceNum(0);
        categoryProduct.setActive(product.getActive());
        categoryProductDAO.insert(categoryProduct);

        List<CategoryProduct> categoryProductList = categoryProductDAO.findByCategoryId(category.getId());

        //toggle
        categoryService.toggleProduct(category.getId(), FindBy.INTERNAL_ID, product.getId(), FindBy.INTERNAL_ID, Toggle.get(product.getActive()));
        List<CategoryProduct> updatedCategoryProduct = categoryProductDAO.findByCategoryId(categoryProductList.get(0).getCategoryId());
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedCategoryProduct));
        Assert.assertEquals(updatedCategoryProduct.get(0).getActive(), "N");

        List<CategoryProduct> categoryProductList1 = categoryProductDAO.findByCategoryId(category.getId());
        categoryService.toggleProduct(category.getId(), FindBy.INTERNAL_ID, product.getId(), FindBy.INTERNAL_ID, Toggle.get(updatedCategoryProduct.get(0).getActive()));
        List<CategoryProduct> updatedCategoryProduct1 = categoryProductDAO.findByCategoryId(categoryProductList1.get(0).getCategoryId());
        Assert.assertEquals(updatedCategoryProduct1.get(0).getActive(), "Y");

    }

    @Test
    public void getCategoryProductsTest() throws Exception {
        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDAO.insert(familyDTO);
        });

        Family family1 = familyDAO.findById(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });

        Category category = categoryDAO.findById(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productDAO.insert(productDTO);

            Product product = productDAO.findById(productData.get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
            //creating categoryProduct
            CategoryProduct categoryProduct = new CategoryProduct();
            categoryProduct.setCategoryId(category.getId());
            categoryProduct.setProductId(product.getId());
            categoryProduct.setSequenceNum(0);
            categoryProduct.setSubSequenceNum(0);
            categoryProduct.setActive(product.getActive());
            categoryProductDAO.insert(categoryProduct);
        });

        List<CategoryProduct> categoryProductList = categoryProductDAO.findByCategoryId(category.getId());
        Page<CategoryProduct> categoryProductsList = categoryService.getCategoryProducts(category.getCategoryId(), FindBy.EXTERNAL_ID, 0, 1, null, false);
        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryProductsList));
        Assert.assertEquals(categoryProductsList.getContent().size(), categoryProductList.size());
    }

    @Test
    public void getCategoryProducts1Test() throws Exception {
        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDAO.insert(familyDTO);
        });

        Family family1 = familyDAO.findById(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });

        Category category = categoryDAO.findById(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId((String)productData.get("productFamilyId"));
            productDTO.setActive((String)productData.get("active"));
            productDAO.insert(productDTO);

            Product product = productDAO.findById(productData.get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
            //creating categoryProduct
            CategoryProduct categoryProduct = new CategoryProduct();
            categoryProduct.setCategoryId(category.getId());
            categoryProduct.setProductId(product.getId());
            categoryProduct.setSequenceNum(0);
            categoryProduct.setSubSequenceNum(0);
            categoryProduct.setActive(product.getActive());
            categoryProductDAO.insert(categoryProduct);
        });

        List<CategoryProduct> categoryProductList = categoryProductDAO.findByCategoryId(category.getId());
        //Getting categoryProduct
        Page<Map<String, Object>> categoryProductsList = categoryService.getCategoryProducts(category.getCategoryId(), FindBy.EXTERNAL_ID, PageRequest.of(0,  productsData.size(), null), false);
        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryProductsList));
        Assert.assertEquals(categoryProductsList.getContent().size(), categoryProductList.size());
    }

    @Test
    public void getAvailableProductsForCategoryTest() throws Exception {
        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categories
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

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 2", "externalId", "TEST_PRODUCT_2", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 3", "externalId", "TEST_PRODUCT_3", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId(productData.get("productFamilyId").toString());
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categoryProduct
        CategoryProduct categoryProduct = new CategoryProduct();
        categoryProduct.setCategoryId(category.getId());
        categoryProduct.setProductId(product.getId());
        categoryProduct.setSequenceNum(0);
        categoryProduct.setSubSequenceNum(0);
        categoryProduct.setActive(product.getActive());
        categoryProductDAO.insert(categoryProduct);

        //Getting available product
        Page<Product> availableProducts = categoryService.getAvailableProductsForCategory(category.getId(), FindBy.INTERNAL_ID, 0, 3, null, false);
        Assert.assertEquals(availableProducts.getContent().size(), productsData.size()-1);
    }

    @Test
    public void addProductTest() throws Exception {
        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categories
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

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 2", "externalId", "TEST_PRODUCT_2", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 3", "externalId", "TEST_PRODUCT_3", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId(productData.get("productFamilyId").toString());
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //Adding product
        CategoryProduct categoryProduct = categoryService.addProduct(category.getId(), FindBy.INTERNAL_ID, product.getId(), FindBy.INTERNAL_ID);
        List<CategoryProduct> categoryProductList = categoryProductDAO.findByCategoryId(category.getId());
        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryProductList));
    }

    @Test
    public void findAllCategoryProductsTest() throws Exception {
        //creating Families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categories
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

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 2", "externalId", "TEST_PRODUCT_2", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.add(CollectionsUtil.toMap("name", "Test Product 3", "externalId", "TEST_PRODUCT_3", "productFamilyId", family1.getFamilyId(), "active", "N"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId(productData.get("productFamilyId").toString());
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categoryProduct
        CategoryProduct categoryProduct = new CategoryProduct();
        categoryProduct.setCategoryId(category.getId());
        categoryProduct.setProductId(product.getId());
        categoryProduct.setSequenceNum(0);
        categoryProduct.setSubSequenceNum(0);
        categoryProduct.setActive(product.getActive());
        categoryProductDAO.insert(categoryProduct);

        //Getting categoryProduct
        Page<Map<String, Object>> categoryProductMap = categoryService.findAllCategoryProducts(category.getId(), FindBy.INTERNAL_ID, "active", "Y", PageRequest.of(0,productsData.size(),null), false);
        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryProductMap));
        long size = productsData.stream().filter(x -> x.get("active").equals("Y")).count();
        Assert.assertEquals(categoryProductMap.getContent().size(),size-1);
    }

    @Test
    public void getAllCategoryProductsTest() throws Exception {
        //creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyService.create(familyDTO);
        });

        Family family1 = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categories
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

        Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating Products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Test Product 1", "externalId", "TEST_PRODUCT_1", "productFamilyId", family1.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String)productData.get("name"));
            productDTO.setProductId((String)productData.get("externalId"));
            productDTO.setProductFamilyId(productData.get("productFamilyId").toString());
            productDTO.setActive((String)productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        //creating categoryProduct
        CategoryProduct categoryProduct = new CategoryProduct();
        categoryProduct.setCategoryId(category.getId());
        categoryProduct.setProductId(product.getId());
        categoryProduct.setSequenceNum(0);
        categoryProduct.setSubSequenceNum(0);
        categoryProduct.setActive(product.getActive());
        categoryProductDAO.insert(categoryProduct);
        //Getting categoryProduct
        List<CategoryProduct> categoryProductsList = categoryService.getAllCategoryProducts(category.getId());
        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryProductsList));
        Assert.assertEquals(categoryProductsList.size(),productsData.size());
    }

    @Test
    public void createEntityTest() {
        //creating categories
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

            //Getting categories by categoryId
            Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));
            Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());
        });
    }
    @Test
    public void createEntitiesTest(){
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));

        List<Category> categoryDTOs = categoriesData.stream().map(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            return categoryDTO;
        }).collect(Collectors.toList());

        categoryService.create(categoryDTOs);
        //Getting categories
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), false).getTotalElements(), categoriesData.size());
    }

    @Test
    public void toggleTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });

        Category categoryDetails = categoryService.get(categoriesData.get(0).get("externalId").toString(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryDetails));
        //toggle
        categoryService.toggle(categoryDetails.getCategoryId(), EXTERNAL_ID, Toggle.get(categoryDetails.getActive()));
        Category updatedCategory = categoryService.get(categoryDetails.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedCategory));
        Assert.assertEquals(updatedCategory.getActive(), "N");

        categoryService.toggle(categoryDetails.getCategoryId(), EXTERNAL_ID, Toggle.get(updatedCategory.getActive()));

        Category updatedCategory1 = categoryService.get(categoryDetails.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedCategory1));
        Assert.assertEquals(updatedCategory1.getActive(), "Y");
    }

    @Test
    public void getTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);

            //Getting categories
            Category categoryDetails = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(categoryDetails));
            Map<String, Object> diff = categoryDTO.diff(categoryDetails);
            Assert.assertEquals(diff.size(), 0);
        });
    }

    @Test
    public void getAllAsPageTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });
        //Getting categories as Page
        Page<Category> paginatedResult = categoryService.getAll(0, 10, null,false);
        Assert.assertEquals(paginatedResult.getContent().size(), categoriesData.size());
    }

    @Test
    public void getAllAsListTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });
        // sorting : ascending
        List<Category> result = categoryService.getAll(Sort.by("categoryName").ascending(), false);
        String[] actual = result.stream().map(category -> category.getCategoryName()).collect(Collectors.toList()).toArray(new String[0]);
        String[] expected = categoriesData.stream().map(categoryData -> (String)categoryData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertArrayEquals(expected, actual);


        //creating categories
        categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });
        // sorting : Descending
        result = categoryService.getAll(Sort.by("categoryName").descending(), false);
        actual = result.stream().map(category -> category.getCategoryName()).collect(Collectors.toList()).toArray(new String[0]);
        expected = categoriesData.stream().map(categoryData -> (String)categoryData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertNotEquals(expected, actual);
    }

    @Test
    public void getAllWithIdsAsPageTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });


        String[] ids = {categoriesData.get(0).get("externalId").toString(), categoriesData.get(1).get("externalId").toString(), categoriesData.get(2).get("externalId").toString()};
        //Getting categories as page
        Page<Category> paginatedResult = categoryService.getAll(ids, EXTERNAL_ID, 0, 10, null, false);
        Map<String, Category> categoriesMap = paginatedResult.getContent().stream().collect(Collectors.toMap(category -> category.getCategoryId(), category -> category));
        Assert.assertTrue(categoriesMap.size() == ids.length && categoriesMap.containsKey(ids[0]) && categoriesMap.containsKey(ids[1]) && categoriesMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithIdsAsListTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });

        String[] ids = {categoriesData.get(0).get("externalId").toString(), categoriesData.get(1).get("externalId").toString(), categoriesData.get(2).get("externalId").toString()};
        //Getting categories by ids
        List<Category> listedResult = categoryService.getAll(ids, EXTERNAL_ID, null, false);
        Map<String, Category> categoriesMap = listedResult.stream().collect(Collectors.toMap(category -> category.getCategoryId(), category -> category));
        Assert.assertTrue(categoriesMap.size() == ids.length && categoriesMap.containsKey(ids[0]) && categoriesMap.containsKey(ids[1]) && categoriesMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithExclusionsAsPageTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });

        String[] ids = {categoriesData.get(0).get("externalId").toString(), categoriesData.get(1).get("externalId").toString(), categoriesData.get(2).get("externalId").toString()};
        //Getting categories with exclude Ids
        Page<Category> paginatedResult = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, 0, 10, null, false);
        Map<String, Category> categoriesMap = paginatedResult.getContent().stream().collect(Collectors.toMap(category -> category.getCategoryId(), category -> category));
        Assert.assertTrue(categoriesMap.size() == (categoriesData.size() - ids.length) && !categoriesMap.containsKey(ids[0]) && !categoriesMap.containsKey(ids[1]) && !categoriesMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithExclusionsAsListTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });

        String[] ids = {categoriesData.get(0).get("externalId").toString(), categoriesData.get(1).get("externalId").toString(), categoriesData.get(2).get("externalId").toString()};
        //Getting categories with exclude Ids
        List<Category> listedResult = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);
        Map<String, Category> categoriesMap = listedResult.stream().collect(Collectors.toMap(category -> category.getCategoryId(), category -> category));
        Assert.assertTrue(categoriesMap.size() == (categoriesData.size() - ids.length) && !categoriesMap.containsKey(ids[0]) && !categoriesMap.containsKey(ids[1]) && !categoriesMap.containsKey(ids[2]));
    }

    @Test
    public void findAllAtSearchTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });
        //Getting categories by searchField
        Page<Category> paginatedResult = categoryService.findAll("name", "Test", PageRequest.of(0, categoriesData.size()), false);
        Assert.assertEquals(paginatedResult.getContent().size(), categoriesData.size());
    }

    @Test
    public void findAllTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });
        //Getting categories
        Page<Category> paginatedResult = categoryService.findAll(PageRequest.of(0, categoriesData.size()), false);
        Assert.assertEquals(paginatedResult.getContent().size(), categoriesData.size());//size
    }

    @Test
    public void updateEntityTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);

            Category categoryDetails = categoryService.get(categoriesData.get(0).get("externalId").toString(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(categoryDetails));
            //updating category
            categoryDetails.setDescription("Test Category");
            categoryDetails.setGroup("DETAILS");

            categoryService.update(categoryDetails.getCategoryId(), EXTERNAL_ID, categoryDetails);
            //Getting updated categories
            Category updatedCategory = categoryService.get(categoryDetails.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(updatedCategory));
            Map<String, Object> diff = categoryDTO.diff(updatedCategory);
            Assert.assertEquals(diff.size(), 1);
            Assert.assertEquals(diff.get("description"), "Test Category");
        });
    }

    @Test
    public void updateEntitiesTest(){
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });

        String[] ids = {categoriesData.get(0).get("externalId").toString(), categoriesData.get(1).get("externalId").toString(), categoriesData.get(2).get("externalId").toString()};

        List<Category> result = categoryService.getAll(ids, EXTERNAL_ID, null, false);
        Map<String, Category> categoriesMap = result.stream().collect(Collectors.toMap(category -> category.getCategoryId(), category -> category));
        Assert.assertTrue(categoriesMap.size() == ids.length && categoriesMap.containsKey(ids[0]) && categoriesMap.containsKey(ids[1]) && categoriesMap.containsKey(ids[2]));
        //updating categories
        List<Category> categories = result.stream().map(result1 -> {
            result1.setActive("N");
            return result1;
        }).collect(Collectors.toList());

        categoryService.update(categories);
        //Getting updated categories
        result = categoryService.getAll(Sort.by("categoryName").descending(), true);
        categoriesMap = result.stream().collect(Collectors.toMap(category -> category.getCategoryId(), category -> category));
        Assert.assertTrue(categoriesMap.size() == (categoriesData.size() - ids.length) && !categoriesMap.containsKey(ids[0]) && !categoriesMap.containsKey(ids[1]) && !categoriesMap.containsKey(ids[2]));
        Assert.assertFalse(categoriesMap.size() == categoriesData.size() && categoriesMap.containsKey(ids[0]) && categoriesMap.containsKey(ids[1]) && categoriesMap.containsKey(ids[2]));
    }

    @Test
    public void cloneInstanceTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
            //Getting category
            Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));
            Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());
            //cloning category instance
            Category websiteClone = categoryService.cloneInstance(newCategory.getCategoryId(), EXTERNAL_ID, Entity.CloneType.LIGHT);
            Assert.assertTrue(websiteClone.getCategoryId() .equals(newCategory.getCategoryId() + "_COPY") && websiteClone.getCategoryName().equals(newCategory.getCategoryName() + "_COPY") && websiteClone.getDescription().equals(newCategory.getDescription() + "_COPY") && websiteClone.getActive() != newCategory.getActive());
        });
    }

    @Test
    public void findAll1Test() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "N"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });

        //Getting categories
        List<Category> result = categoryService.findAll(CollectionsUtil.toMap("active", "N"));
        long size = categoriesData.stream().filter(x -> x.get("active").equals("N")).count();
        Assert.assertTrue(result.size() == size);
    }

    @Test
    public void findAll2Test() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "N"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });
        //Getting categories
        Criteria criteria = PimUtil.buildCriteria(CollectionsUtil.toMap("active", "N"));
        List<Category> result = categoryService.findAll(criteria);
        long size = categoriesData.stream().filter(x -> x.get("active").equals("N")).count();
        Assert.assertTrue(result.size() == size);
    }

    @Test
    public void findOneTest() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "N"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });
        //Getting category
        Optional<Category> result = categoryService.findOne(CollectionsUtil.toMap("categoryName", categoriesData.get(0).get("name")));
        Assert.assertEquals(categoriesData.get(0).get("name"), result.get().getCategoryName());
    }

    @Test
    public void findOne1Test() {
        //creating categories
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "Test Category1", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "description", "Test Category2", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "description", "Test Category3", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "description", "Test Category4", "active", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "description", "Test Category5", "active", "N"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });
        //Getting category
        Criteria criteria = PimUtil.buildCriteria(CollectionsUtil.toMap("categoryName", categoriesData.get(0).get("name")));
        Optional<Category> result = categoryService.findOne(criteria);
        Assert.assertEquals(categoriesData.get(0).get("name"), result.get().getCategoryName());
    }

    @Test
    public void validateTest() throws Exception {
        /* Create a valid new instance with id CATEGORY_TEST */
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("Category Test");
        categoryDTO.setCategoryId("CATEGORY_TEST");
        categoryDTO.setDescription("Category description");
        categoryDTO.setActive("Y");

        Map<String, Object> context = new HashMap<>();

        Class groups = ValidatableEntity.CreateGroup.class;
//        validate
        Assert.assertTrue(categoryService.validate(categoryDTO, context, groups).isEmpty());
//        insert the valid instance
        categoryDAO.insert(categoryDTO);

        /*Create a second instance with the same id CATEGORY_TEST to check the unique constraint violation of categoryId*/

        Category categoryDTO1 = new Category();
        categoryDTO1.setCategoryName("Category1 Test");
        categoryDTO1.setCategoryId("CATEGORY_TEST");
        categoryDTO1.setDescription("Category1 description");
        categoryDTO1.setActive("Y");

        Assert.assertEquals(categoryService.validate(categoryDTO1, context, groups).size(), 1);

        /*Testing forceUniqueId*/
        context.put("forceUniqueId", true);
        Assert.assertTrue(categoryService.validate(categoryDTO1, context, groups).isEmpty());
        Assert.assertEquals(categoryDTO1.getExternalId(), "CATEGORY_TEST_1");
        categoryDAO.insert(categoryDTO1);

        context.clear();

        /*Testing uniqueConstraint violation of categoryId with update operation*/
        Category category = categoryDAO.findById(categoryDTO.getCategoryId(), FindBy.EXTERNAL_ID).orElse(null);
        category.setCategoryName("Category1 Test");
        category.setCategoryId("CATEGORY_TEST_1");
        category.setDescription("Category1 description");
        category.setActive("Y");
        context.put("id", categoryDTO.getExternalId());

        groups = ValidatableEntity.DetailsGroup.class;
        Assert.assertEquals(categoryService.validate(category, context, groups).size(), 1);

        /*Testing forceUniqueId with update operation*/
        context.put("forceUniqueId", true);
        Assert.assertTrue(categoryService.validate(category, context, groups).isEmpty());
        Assert.assertEquals(category.getExternalId(), "CATEGORY_TEST_1_1");
    }

    @After
    public void tearDown() throws Exception {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
        relatedCategoryDAO.deleteAll();
        categoryProductDAO.deleteAll();
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        productDAO.getMongoTemplate().dropCollection(Product.class);
    }

}