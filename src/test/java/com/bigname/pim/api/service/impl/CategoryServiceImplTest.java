package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        Page<Map<String, Object>> paginatedResult = categoryService.findAllSubCategories(category.getExternalId(), FindBy.EXTERNAL_ID, "description", "Test", PageRequest.of(0, newRelatedCategories.size(), null), false);
        Assert.assertEquals(paginatedResult.getSize(), newRelatedCategories.size());
    }

    @Test
    public void findAvailableSubCategoriesForCategoryTest() throws Exception {
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

        Page<Category> paginatedResult = categoryService.findAvailableSubCategoriesForCategory(category.getExternalId(), FindBy.EXTERNAL_ID, "description", "Test", PageRequest.of(0, 2, Sort.Direction.ASC, category.getCategoryName()), false);
        Assert.assertEquals(paginatedResult.getSize(), 2);
    }

    @Test
    public void getCategoryHierarchyTest() throws Exception {
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

        List<Category> categories = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);

        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());

        List<Map<String, Object>> subCategories = categoryService.getCategoryHierarchy(false);
        Assert.assertTrue(ValidationUtil.isNotEmpty(subCategories));
        Assert.assertEquals(subCategories.get(0).get("parent"), categoriesData.get(0).get("parent"));
        Assert.assertEquals(subCategories.get(0).get("level").toString(), categoriesData.get(0).get("level"));
        Assert.assertEquals(subCategories.get(1).get("parent"), categoriesData.get(1).get("parent"));
        Assert.assertEquals(subCategories.get(1).get("parentChain"), categoriesData.get(1).get("parentChain"));
    }

    @Test
    public void getAvailableSubCategoriesForCategoryTest() throws Exception {
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

        Page<Category> paginatedResult = categoryService.getAvailableSubCategoriesForCategory(category.getExternalId(), FindBy.EXTERNAL_ID, 0, 2, null, false);
        Assert.assertEquals(paginatedResult.getSize(), 2);
    }

    @Test
    public void getSubCategoriesTest() throws Exception {
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

        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());

        Page<Map<String, Object>> subCategories = categoryService.getSubCategories(category.getExternalId(), EXTERNAL_ID, PageRequest.of(0, newRelatedCategories.size(), null), false);
        Assert.assertEquals(subCategories.getSize(), newRelatedCategories.size());
    }

    @Test
    public void setSubCategorySequenceTest() throws Exception {
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

        List<Category> categories = categoryService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);

        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());

        boolean success = categoryService.setSubCategorySequence(category.getExternalId(), FindBy.EXTERNAL_ID, categoriesData.get(1).get("externalId").toString(), FindBy.EXTERNAL_ID, categoriesData.get(4).get("externalId").toString(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(success);
    }

    @Test
    public void addSubCategoryTest() throws Exception {
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

        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());
    }

    @Test
    public void toggleSubCategoryTest() throws Exception {
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

        categories.forEach(relatedCategory -> {
            RelatedCategory newRelatedCategory = categoryService.addSubCategory(category.getExternalId(), FindBy.EXTERNAL_ID, relatedCategory.getExternalId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newRelatedCategory));
        });
        List<RelatedCategory> newRelatedCategories = relatedCategoryDAO.findByCategoryId(category.getId());
        Assert.assertEquals(categories.size(), newRelatedCategories.size());
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

    @After
    public void tearDown() throws Exception {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
        relatedCategoryDAO.deleteAll();
    }

}