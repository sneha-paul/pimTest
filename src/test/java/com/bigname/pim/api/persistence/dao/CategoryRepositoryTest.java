package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.*;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import com.m7.xtreme.xcore.util.FindBy;
import com.m7.xtreme.xcore.util.ID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by dona on 19-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CategoryRepositoryTest {
    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private RelatedCategoryDAO relatedCategoryDAO;
    @Autowired
    private FamilyDAO familyDAO;
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private CategoryProductDAO categoryProductDAO;
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) categoryDAO.getTemplate();
        }
        mongoTemplate.dropCollection(Category.class);
        relatedCategoryDAO.deleteAll();
        mongoTemplate.dropCollection(Family.class);
        mongoTemplate.dropCollection(Product.class);
        categoryProductDAO.deleteAll();
    }

    @Test
    public void createCategoryTest() {
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));

            Category category = categoryDAO.insert(categoryDTO);
            Assert.assertTrue(category.diff(categoryDTO).isEmpty());
        });
    }

    @Test
    public void retrieveCategoryTest() {
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);

            Optional<Category> category = categoryDAO.findByExternalId(categoryDTO.getCategoryId());
            Assert.assertTrue(category.isPresent());
            category = categoryDAO.findById(ID.EXTERNAL_ID(categoryDTO.getCategoryId()));
            Assert.assertTrue(category.isPresent());
            category = categoryDAO.findById(ID.INTERNAL_ID(categoryDTO.getId()));
            Assert.assertTrue(category.isPresent());
        });
    }

    @Test
    public void updateCategoryTest() {
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });

        Category category = categoryDAO.findByExternalId(categoriesData.get(0).get("externalId").toString()).orElse(null);

        Category categoryDetails = categoryDAO.findByExternalId(categoriesData.get(0).get("externalId").toString()).orElse(null);
        Assert.assertTrue(categoryDetails != null);
        categoryDetails.setDescription("Test1 catalog description");
        categoryDetails.setGroup("DETAILS");
        categoryDAO.save(categoryDetails);

        Category updatedCategory = categoryDAO.findByExternalId(categoryDetails.getCategoryId()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedCategory));
        Map<String, Object> diff = category.diff(updatedCategory);
        Assert.assertEquals(diff.size(), 1);
        Assert.assertEquals(diff.get("description"), "Test1 catalog description");

        Category categoryDetails1 = categoryDAO.findByExternalId(categoriesData.get(1).get("externalId").toString()).orElse(null);
        categoryDetails1.setMetaTitle("New Meta title");
        categoryDetails1.setGroup("SEO");
        categoryDAO.save(categoryDetails1);

        Category updatedCategory1 = categoryDAO.findByExternalId(categoryDetails1.getCategoryId()).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedCategory1));

    }


    @Test
    public void retrieveCategoriesTest() {

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "description", "TEST_3description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "description", "TEST_4description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "description", "TEST_5description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "description", "TEST_6description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "description", "TEST_7description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "description", "TEST_8description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test9", "externalId", "TEST_9", "description", "TEST_9description", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDAO.insert(categoryDTO);
        });

        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), false).getTotalElements(), categoriesData.size());
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size() - 1), false).getTotalElements(), categoriesData.size());
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size() - 1), false).getContent().size(), categoriesData.size() - 1);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(1, categoriesData.size() - 1), false).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size() - 1), false).getTotalPages(), 2);

        mongoTemplate.dropCollection(Category.class);

        categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "N", "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "N", "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "description", "TEST_3description", "active", "N", "discontinued", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "description", "TEST_4description", "active", "N", "discontinued", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "description", "TEST_5description", "active", "Y", "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "description", "TEST_6description", "active", "Y", "discontinued", "N"));

        int[] activeCount = {0}, inactiveCount = {0};
        int[] discontinued = {0};
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDTO.setDiscontinued((String)categoryData.get("discontinued"));
            if("Y".equals(categoryData.get("discontinued"))){
                discontinued[0] ++;
            } else {
                if("Y".equals(categoryData.get("active"))) {
                    activeCount[0] ++;
                } else {
                    inactiveCount[0] ++;
                }
            }
            categoryDAO.insert(categoryDTO);
        });

        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), true).getTotalElements(), activeCount[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), false, true).getTotalElements(), inactiveCount[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), false).getTotalElements(), activeCount[0] + inactiveCount[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), false, false, true).getTotalElements(), discontinued[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), false, true, true).getTotalElements(), inactiveCount[0] + discontinued[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), true, true, true).getTotalElements(), activeCount[0] + inactiveCount[0] + discontinued[0]);

        mongoTemplate.dropCollection(Category.class);

        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        categoriesData = new ArrayList<>();

        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "activeFrom", yesterday, "activeTo", todayEOD, "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "activeFrom", null, "activeTo", todayEOD, "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "activeFrom", tomorrow, "discontinued", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "N", "activeFrom", null, "activeTo", null, "discontinued", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "activeFrom", yesterday, "activeTo", tomorrowEOD, "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "activeFrom", yesterday, "activeTo", null, "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "activeFrom", null, "activeTo", null, "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y", "activeFrom", null, "activeTo", null, "discontinued", "N"));

        int[] activeCount1 = {0}, inactiveCount1 = {0}, discontinued1 = {0};

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDTO.setDiscontinued((String)categoryData.get("discontinued"));
            if("Y".equals(categoryData.get("discontinued"))){
                discontinued1[0] ++;
            } else {
                if("Y".equals(categoryData.get("active"))) {
                    activeCount1[0] ++;
                } else {
                    inactiveCount1[0] ++;
                }
            }
            categoryDAO.insert(categoryDTO);
        });

        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), true).getTotalElements(), activeCount1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), false, true).getTotalElements(), inactiveCount1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), true, true, true).getTotalElements(), activeCount1[0] + inactiveCount1[0] + discontinued1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), true, true, false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), true, false, true).getTotalElements(), activeCount1[0] + discontinued1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), false, true, true).getTotalElements(), inactiveCount1[0] + discontinued1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), false, false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoriesData.size()), false, false, true).getTotalElements(), discontinued1[0]);
    }

    @Test
    public void getSubCategoriesTest() throws Exception {

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

        Category category = categoryDAO.findById(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString())).orElse(null);

        categoriesData.stream().skip(1).forEach(categoryData -> {
            Category category1 = categoryDAO.findById(ID.EXTERNAL_ID(categoryData.get("externalId").toString())).orElse(null);
            RelatedCategory relatedCategory = new RelatedCategory();
            relatedCategory.setCategoryId(category.getId());
            relatedCategory.setSubCategoryId(category1.getId());
            relatedCategory.setActive("Y");
            relatedCategoryDAO.insert(relatedCategory);
        });

        Page<Map<String, Object>> subCategoriesMap = categoryDAO.getSubCategories(category.getId(), PageRequest.of(0, categoriesData.size(), null));
        Assert.assertEquals(subCategoriesMap.getTotalElements(), categoriesData.size() - 1);
        Assert.assertEquals(categoryDAO.getSubCategories(category.getId(), PageRequest.of(0, categoriesData.size() - 1, null)).getTotalElements(), categoriesData.size() - 1);
        Assert.assertEquals(categoryDAO.getSubCategories(category.getId(), PageRequest.of(0, categoriesData.size()-2, null)).getTotalElements(), categoriesData.size() - 1);
        Assert.assertEquals(categoryDAO.getSubCategories(category.getId(), PageRequest.of(0, categoriesData.size()-2, null)).getContent().size(), categoriesData.size() - 2);
        Assert.assertEquals(categoryDAO.getSubCategories(category.getId(), PageRequest.of(1, 1, null)).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.getSubCategories(category.getId(), PageRequest.of(1, categoriesData.size()-2, null)).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.getSubCategories(category.getId(), PageRequest.of(0, categoriesData.size()-2, null)).getTotalPages(), 2);
    }

    @Test
    public void getAllSubCategoriesTest() throws Exception {
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
            categoryDAO.insert(categoryDTO);
        });

        Category category = categoryDAO.findById(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString())).orElse(null);

        categoriesData.stream().skip(1).forEach(categoryData -> {
            Category category1 = categoryDAO.findById(ID.EXTERNAL_ID(categoryData.get("externalId").toString())).orElse(null);
            RelatedCategory relatedCategory = new RelatedCategory();
            relatedCategory.setCategoryId(category.getId());
            relatedCategory.setSubCategoryId(category1.getId());
            relatedCategory.setActive("Y");
            relatedCategoryDAO.insert(relatedCategory);
        });

        boolean[] activeRequired = {false};

        List<RelatedCategory> subCategoryList = categoryDAO.getAllSubCategories(category.getId(), activeRequired);
        Assert.assertTrue(ValidationUtil.isNotEmpty(subCategoryList));
    }

    @Test
    public void findAvailableSubCategoriesForCategory() throws Exception {
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

        Category category = categoryDAO.findById(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString())).orElse(null);
        Category category1 = categoryDAO.findById(ID.EXTERNAL_ID(categoriesData.get(2).get("externalId").toString())).orElse(null);

        RelatedCategory relatedCategory = new RelatedCategory();
        relatedCategory.setCategoryId(category.getId());
        relatedCategory.setSubCategoryId(category1.getId());
        relatedCategory.setActive("Y");
        relatedCategoryDAO.insert(relatedCategory);

        Page<Category> availableCategoriesPage = categoryDAO.findAvailableSubCategoriesForCategory(category.getId(), "categoryName", "Test", PageRequest.of(0, categoriesData.size() - 2), false);
        Assert.assertEquals(availableCategoriesPage.getContent().size(), 3);
        Assert.assertEquals(categoryDAO.findAvailableSubCategoriesForCategory(category.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-2), false).getTotalElements(), categoriesData.size()-1);
        Assert.assertEquals(categoryDAO.findAvailableSubCategoriesForCategory(category.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-2), false).getContent().size(), categoriesData.size() - 2);
        Assert.assertEquals(categoryDAO.findAvailableSubCategoriesForCategory(category.getId(), "categoryName", "Test", PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.findAvailableSubCategoriesForCategory(category.getId(), "categoryName", "Test", PageRequest.of(1,categoriesData.size()-2), false).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.findAvailableSubCategoriesForCategory(category.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-2), false).getTotalPages(), 2);

    }

    @Test
    public void findAllSubCategoriesTest() throws Exception {
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

        Category category = categoryDAO.findById(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString())).orElse(null);

        categoriesData.stream().skip(1).forEach(categoryData -> {
            Category category1 = categoryDAO.findById(ID.EXTERNAL_ID(categoryData.get("externalId").toString())).orElse(null);
            RelatedCategory relatedCategory = new RelatedCategory();
            relatedCategory.setCategoryId(category.getId());
            relatedCategory.setSubCategoryId(category1.getId());
            relatedCategory.setActive("Y");
            relatedCategoryDAO.insert(relatedCategory);
        });

        boolean[] activeRequired = {false};

        Page<Map<String, Object>> relatedCategories =  categoryDAO.findAllSubCategories(category.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size() - 1,null), activeRequired);
        Assert.assertEquals(relatedCategories.getSize(), categoriesData.size() - 1);
        Assert.assertEquals(categoryDAO.findAllSubCategories(category.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size() - 1,null), activeRequired).getTotalElements(), categoriesData.size() - 1);
        Assert.assertEquals(categoryDAO.findAllSubCategories(category.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-2,null), activeRequired).getTotalElements(), categoriesData.size()-1);
        Assert.assertEquals(categoryDAO.findAllSubCategories(category.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-2,null), activeRequired).getContent().size(), categoriesData.size() - 2);
        Assert.assertEquals(categoryDAO.findAllSubCategories(category.getId(), "categoryName", "Test", PageRequest.of(1, 1, null), activeRequired).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.findAllSubCategories(category.getId(), "categoryName", "Test", PageRequest.of(1,categoriesData.size()-2,null), activeRequired).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.findAllSubCategories(category.getId(), "categoryName", "Test", PageRequest.of(0,categoriesData.size()-2,null), activeRequired).getTotalPages(), 2);
    }

    @Test
    public void getProductsTest() throws Exception {
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDAO.insert(familyDTO);
        });

        Family family1 = familyDAO.findById(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString())).orElse(null);

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

        Category category = categoryDAO.findById(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString())).orElse(null);

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
            productDAO.insert(productDTO);

            Product product = productDAO.findById(ID.EXTERNAL_ID(productData.get("externalId").toString())).orElse(null);

            CategoryProduct categoryProduct = new CategoryProduct();
            categoryProduct.setCategoryId(category.getId());
            categoryProduct.setProductId(product.getId());
            categoryProduct.setSequenceNum(0);
            categoryProduct.setSubSequenceNum(0);
            categoryProduct.setActive(product.getActive());
            categoryProductDAO.insert(categoryProduct);
        });
        Page<Map<String, Object>> categoryProductMap = categoryDAO.getProducts(category.getId(), PageRequest.of(0, productsData.size(), null));
        Assert.assertEquals(categoryProductMap.getSize(), productsData.size());
        Assert.assertEquals(categoryDAO.getProducts(category.getId(), PageRequest.of(0, productsData.size(), null)).getTotalElements(), productsData.size());
        Assert.assertEquals(categoryDAO.getProducts(category.getId(), PageRequest.of(0, productsData.size()-1, null)).getTotalElements(), productsData.size());
        Assert.assertEquals(categoryDAO.getProducts(category.getId(), PageRequest.of(0, productsData.size()-1, null)).getContent().size(), productsData.size() - 1);
        Assert.assertEquals(categoryDAO.getProducts(category.getId(), PageRequest.of(1, 1, null)).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.getProducts(category.getId(), PageRequest.of(1, productsData.size()-1, null)).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.getProducts(category.getId(), PageRequest.of(0, productsData.size()-1, null)).getTotalPages(), 2);
    }

    @Test
    public void findAllCategoryProductsTest() throws Exception {
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDAO.insert(familyDTO);
        });

        Family family1 = familyDAO.findById(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString())).orElse(null);

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

        Category category = categoryDAO.findById(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString())).orElse(null);

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
            productDAO.insert(productDTO);

            Product product = productDAO.findById(ID.EXTERNAL_ID(productData.get("externalId").toString())).orElse(null);

            CategoryProduct categoryProduct = new CategoryProduct();
            categoryProduct.setCategoryId(category.getId());
            categoryProduct.setProductId(product.getId());
            categoryProduct.setSequenceNum(0);
            categoryProduct.setSubSequenceNum(0);
            categoryProduct.setActive(product.getActive());
            categoryProductDAO.insert(categoryProduct);
        });
        boolean[] activeRequired = {false};

        Page<Map<String, Object>> categoryProductMap = categoryDAO.findAllCategoryProducts(category.getId(), "productName", "Test", PageRequest.of(0,productsData.size(),null), activeRequired);
        Assert.assertEquals(categoryProductMap.getSize(), productsData.size());
        Assert.assertEquals(categoryDAO.findAllCategoryProducts(category.getId(), "productName", "Test", PageRequest.of(0,productsData.size(),null), activeRequired).getTotalElements(), productsData.size());
        Assert.assertEquals(categoryDAO.findAllCategoryProducts(category.getId(), "productName", "Test", PageRequest.of(0,productsData.size()-1,null), activeRequired).getTotalElements(), productsData.size());
        Assert.assertEquals(categoryDAO.findAllCategoryProducts(category.getId(), "productName", "Test", PageRequest.of(0,productsData.size()-1,null), activeRequired).getContent().size(), productsData.size() - 1);
        Assert.assertEquals(categoryDAO.findAllCategoryProducts(category.getId(), "productName", "Test", PageRequest.of(1, 1, null), activeRequired).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.findAllCategoryProducts(category.getId(), "productName", "Test", PageRequest.of(1,productsData.size()-1,null), activeRequired).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.findAllCategoryProducts(category.getId(), "productName", "Test", PageRequest.of(0,productsData.size()-1,null), activeRequired).getTotalPages(), 2);
    }

    @Test
    public void getAllCategoryProductsTest() throws Exception {
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDAO.insert(familyDTO);
        });

        Family family1 = familyDAO.findById(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString())).orElse(null);

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

        Category category = categoryDAO.findById(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString())).orElse(null);

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
            productDAO.insert(productDTO);

            Product product = productDAO.findById(ID.EXTERNAL_ID(productData.get("externalId").toString())).orElse(null);

            CategoryProduct categoryProduct = new CategoryProduct();
            categoryProduct.setCategoryId(category.getId());
            categoryProduct.setProductId(product.getId());
            categoryProduct.setSequenceNum(0);
            categoryProduct.setSubSequenceNum(0);
            categoryProduct.setActive(product.getActive());
            categoryProductDAO.insert(categoryProduct);
        });

        List<CategoryProduct> categoryProductsList = categoryDAO.getAllCategoryProducts(category.getId());
        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryProductsList));
    }

    @Test
    public void findAvailableProductsForCategoryTest() throws Exception {
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test Family 1", "externalId", "TEST_FAMILY_1", "active", "Y"));
        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String)familyData.get("name"));
            familyDTO.setFamilyId((String)familyData.get("externalId"));
            familyDTO.setActive((String)familyData.get("active"));
            familyDAO.insert(familyDTO);
        });

        Family family1 = familyDAO.findById(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString())).orElse(null);

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

        Category category = categoryDAO.findById(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString())).orElse(null);

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
            productDAO.insert(productDTO);
        });

        Product product = productDAO.findById(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString())).orElse(null);

        CategoryProduct categoryProduct = new CategoryProduct();
        categoryProduct.setCategoryId(category.getId());
        categoryProduct.setProductId(product.getId());
        categoryProduct.setSequenceNum(0);
        categoryProduct.setSubSequenceNum(0);
        categoryProduct.setActive(product.getActive());
        categoryProductDAO.insert(categoryProduct);

        Page<Product> availableProducts = categoryDAO.findAvailableProductsForCategory(category.getId(), "productName", "Test", PageRequest.of(0, productsData.size() - 1), false);
        Assert.assertEquals(availableProducts.getContent().size(), productsData.size() - 1);
        Assert.assertEquals(categoryDAO.findAvailableProductsForCategory(category.getId(), "productName", "Test", PageRequest.of(0,productsData.size()-1), false).getTotalElements(), productsData.size()-1);
        Assert.assertEquals(categoryDAO.findAvailableProductsForCategory(category.getId(), "productName", "Test", PageRequest.of(0,productsData.size()-1), false).getTotalElements(), productsData.size()-1);
        Assert.assertEquals(categoryDAO.findAvailableProductsForCategory(category.getId(), "productName", "Test", PageRequest.of(0,productsData.size()-1), false).getContent().size(), productsData.size() - 1);
        Assert.assertEquals(categoryDAO.findAvailableProductsForCategory(category.getId(), "productName", "Test", PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.findAvailableProductsForCategory(category.getId(), "productName", "Test", PageRequest.of(1,productsData.size()-1), false).getContent().size(), 0);
        Assert.assertEquals(categoryDAO.findAvailableProductsForCategory(category.getId(), "productName", "Test", PageRequest.of(0,productsData.size()-1), false).getTotalPages(), 1);
    }

    @After
    public void tearDown() {
        mongoTemplate.dropCollection(Category.class);
        relatedCategoryDAO.deleteAll();
        mongoTemplate.dropCollection(Family.class);
        mongoTemplate.dropCollection(Product.class);
        categoryProductDAO.deleteAll();
    }
}
