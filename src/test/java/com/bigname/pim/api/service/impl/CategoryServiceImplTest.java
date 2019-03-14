package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.Entity;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.RelatedCategoryDAO;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.util.PimUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static org.junit.Assert.*;

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


    @Before
    public void setUp() throws Exception {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
        relatedCategoryDAO.deleteAll();
    }

    @Test
    public void findAllSubCategoriesTest() throws Exception {
        //Creating category
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
        //creating category
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
        Assert.assertEquals(paginatedResult.getSize(), 2);
    }

    @Test
    public void getCategoryHierarchyTest() throws Exception {
        //creating category
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
        //creating category
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
        Assert.assertEquals(paginatedResult.getSize(), 2);
    }

    @Test
    public void getSubCategoriesTest() throws Exception {
        //creating category
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
        //creating category
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
        //creating category
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
        //creating category
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
    }

    @Test
    public void findAvailableProductsForCategoryTest() throws Exception {
    }

    @Test
    public void setProductSequenceTest() throws Exception {
    }

    @Test
    public void toggleProductTest() throws Exception {
    }

    @Test
    public void getCategoryProductsTest() throws Exception {
    }

    @Test
    public void getCategoryProducts1Test() throws Exception {
    }

    @Test
    public void getAvailableProductsForCategoryTest() throws Exception {
    }

    @Test
    public void addProductTest() throws Exception {
    }

    @Test
    public void findAllCategoryProductsTest() throws Exception {
    }

    @Test
    public void getAllCategoryProductsTest() throws Exception {
    }

    @Test
    public void createEntityTest() {
        //creating category
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
        //creating category
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
        //creating category
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
    }

    @Test
    public void getTest() {
        //creating category
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
        //creating category
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
        //creating category
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


        //creating category
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
        //creating category
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
        //creating category
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
        //creating category
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
        //creating category
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
        //creating category
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
        Assert.assertEquals(paginatedResult.getContent().size(), 5);
    }

    @Test
    public void findAllTest() {
        //creating category
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
        Assert.assertEquals(paginatedResult.getContent().size(), 5);
    }

    @Test
    public void updateEntityTest() {
        //creating category
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
        //creating category
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
    public void cloneInstance() {
        //creating category
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
    public void findAll() {
        //creating category
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
        Assert.assertTrue(result.size() == 2);
    }

    @Test
    public void findAll1() {
        //creating category
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
        Assert.assertTrue(result.size() == 2);
    }

    @Test
    public void findOne() {
        //creating category
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
    public void findOne1() {
        //creating category
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
    public void validate1() throws Exception {
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
    }

}