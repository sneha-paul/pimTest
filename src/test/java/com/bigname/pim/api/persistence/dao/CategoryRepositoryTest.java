package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.util.PimUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by dona on 19-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CategoryRepositoryTest {
    @Autowired
    CategoryDAO categoryDAO;

    @Before
    public void setUp() {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }

    @Test
    public void createCategoryTest() {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("Test1");
        categoryDTO.setCategoryId("TEST1");
        categoryDTO.setDescription("Test category");
        categoryDTO.setActive("Y");
        Category category = categoryDAO.insert(categoryDTO);
        Assert.assertTrue(category.diff(categoryDTO).isEmpty());
    }

    @Test
    public void retrieveCategoryTest() {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("Test1");
        categoryDTO.setCategoryId("TEST1");
        categoryDTO.setDescription("Test category");
        categoryDTO.setActive("Y");
        categoryDAO.insert(categoryDTO);
        Optional<Category> category = categoryDAO.findByExternalId(categoryDTO.getCategoryId());
        Assert.assertTrue(category.isPresent());
        category = categoryDAO.findById(categoryDTO.getCategoryId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(category.isPresent());
        category = categoryDAO.findById(categoryDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(category.isPresent());
    }

    @Test
    public void updateCategoryTest() {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("Test1");
        categoryDTO.setCategoryId("TEST1");
        categoryDTO.setDescription("Test category");
        categoryDTO.setActive("Y");
        categoryDTO.setMetaTitle("Meta Title");
        categoryDAO.insert(categoryDTO);

        Category categoryDetails = categoryDAO.findByExternalId(categoryDTO.getCategoryId()).orElse(null);
        categoryDetails.setCategoryName("Test1Name");
        categoryDetails.setMetaTitle("New Meta title");
        categoryDetails.setMetaDescription("Meta description");
        categoryDetails.setActive("N");
        categoryDetails.setGroup("DETAILS", "SEO");
        categoryDAO.save(categoryDetails);

        Optional<Category> category = categoryDAO.findByExternalId(categoryDetails.getCategoryId());
        Assert.assertTrue(category.isPresent());
        category = categoryDAO.findById(categoryDetails.getCategoryId(), FindBy.EXTERNAL_ID) ;
        Assert.assertTrue(category.isPresent());
        category = categoryDAO.findById(categoryDetails.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(category.isPresent());
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

        List<Category> categoryDTOs = categoriesData.stream().map(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDiscontinued((String)categoryData.get("discontinued"));
            return categoryDTO;
        }).collect(Collectors.toList());

        categoryDAO.insert(categoryDTOs);

        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), false).getTotalElements(), categoryDTOs.size());
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size() - 1), false).getTotalElements(), categoryDTOs.size());
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size() - 1), false).getContent().size(), categoryDTOs.size() - 1);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(1, categoryDTOs.size() - 1), false).getContent().size(), 1);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size() - 1), false).getTotalPages(), 2);

        categoryDAO.getMongoTemplate().dropCollection(Category.class);

        categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "N", "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "N", "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "description", "TEST_3description", "active", "N", "discontinued", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "description", "TEST_4description", "active", "N", "discontinued", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "description", "TEST_5description", "active", "Y", "discontinued", "N"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "description", "TEST_6description", "active", "Y", "discontinued", "N"));

        int[] activeCount = {0}, inactiveCount = {0};
        int[] discontinued = {0};
        categoryDTOs = categoriesData.stream().map(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDTO.setActive((String)categoryData.get("active"));
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
            return categoryDTO;
        }).collect(Collectors.toList());

        categoryDAO.insert(categoryDTOs);

        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), true).getTotalElements(), activeCount[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), false, true).getTotalElements(), inactiveCount[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), false).getTotalElements(), activeCount[0] + inactiveCount[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), false, false, true).getTotalElements(), discontinued[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), false, true, true).getTotalElements(), inactiveCount[0] + discontinued[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), true, true, true).getTotalElements(), activeCount[0] + inactiveCount[0] + discontinued[0]);

        categoryDAO.getMongoTemplate().dropCollection(Category.class);

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

        categoryDTOs = categoriesData.stream().map(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryDTO.setDiscontinued((String)categoryData.get("discontinued"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setActiveFrom((LocalDateTime) categoryData.get("activeFrom"));
            categoryDTO.setActiveTo((LocalDateTime) categoryData.get("activeTo"));

            if(PimUtil.hasDiscontinued(categoryDTO.getDiscontinued(), categoryDTO.getDiscontinuedFrom(), categoryDTO.getDiscontinuedTo())) {
                discontinued1[0]++;
            } else if(PimUtil.isActive(categoryDTO.getActive(), categoryDTO.getActiveFrom(), categoryDTO.getActiveTo())) {
                activeCount1[0] ++;
            } else {
                inactiveCount1[0] ++;
            }
            return categoryDTO;
        }).collect(Collectors.toList());

        categoryDAO.insert(categoryDTOs);

        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), true).getTotalElements(), activeCount1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), false, true).getTotalElements(), inactiveCount1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), true, true, true).getTotalElements(), activeCount1[0] + inactiveCount1[0] + discontinued1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), true, true, false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), true, false, true).getTotalElements(), activeCount1[0] + discontinued1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), false, true, true).getTotalElements(), inactiveCount1[0] + discontinued1[0]);
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), false, false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        /*Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), false).getTotalElements(), activeCount1[0] + inactiveCount1[0] + discontinued1[0]);*/
        Assert.assertEquals(categoryDAO.findAll(PageRequest.of(0, categoryDTOs.size()), false, false, true).getTotalElements(), discontinued1[0]);
    }

    @After
    public void tearDown() {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }
}
