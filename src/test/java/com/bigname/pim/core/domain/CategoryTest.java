package com.bigname.pim.core.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.core.persistence.dao.mongo.CategoryDAO;
import com.bigname.pim.core.service.CategoryService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.persistence.dao.primary.mongo.UserDAO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by sanoop on 05/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CategoryTest {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) categoryDAO.getTemplate();
        }

        User user1 = userDAO.findByEmail("MANU@BLACWOOD.COM");
        if(ValidationUtil.isEmpty(user1)){
            User user = new User();
            user.setUserName("MANU@BLACWOOD.COM");
            user.setPassword("temppass");
            user.setEmail("manu@blacwood.com");
            user.setStatus("Active");
            user.setActive("Y");
            user.setTenantId("Blacwood");
            userDAO.save(user);
        }
        User user2 = userDAO.findByEmail("MANU@E-XPOSURE.COM");
        if(ValidationUtil.isEmpty(user2)) {
            User user = new User();
            user.setUserName("MANU@E-XPOSURE.COM");
            user.setPassword("temppass1");
            user.setEmail("manu@e-xposure.com");
            user.setStatus("Active");
            user.setActive("Y");
            user.setTenantId("Exposure");
            userDAO.save(user);
        }

        mongoTemplate.dropCollection(Category.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest(){
        //Create New Instance
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("test");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryDTO.orchestrate();

        //Testing equals unique id
        Assert.assertEquals(categoryDTO.getCategoryId(), "TEST");
        Assert.assertEquals(categoryDTO.getCategoryName(), "test");
        Assert.assertEquals(categoryDTO.getDescription(), "test");
        Assert.assertEquals(categoryDTO.getLongDescription(), "test");
        Assert.assertEquals(categoryDTO.getMetaTitle(), "test");
        Assert.assertEquals(categoryDTO.getMetaDescription(), "test");
        Assert.assertEquals(categoryDTO.getActive(), "N");

        //Create
        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(ID.EXTERNAL_ID(categoryDTO.getCategoryId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));

        Assert.assertEquals(newCategory.getCategoryId(), categoryDTO.getCategoryId());
        Assert.assertEquals(newCategory.getCategoryName(), categoryDTO.getCategoryName());
        Assert.assertEquals(newCategory.getDescription(), categoryDTO.getDescription());
        Assert.assertEquals(newCategory.getLongDescription(), categoryDTO.getLongDescription());
        Assert.assertEquals(newCategory.getMetaTitle(), categoryDTO.getMetaTitle());
        Assert.assertEquals(newCategory.getMetaDescription(), categoryDTO.getMetaDescription());
        Assert.assertEquals(newCategory.getActive(), categoryDTO.getActive());

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getSubCategories() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setSubCategories() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void orchestrate() throws Exception {
        //Create new instance
        Category categoryDTO = new Category();
        categoryDTO.setExternalId("test");
        categoryDTO.orchestrate();

        //Check CategoryId
        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryDTO.getCategoryId()));
        Assert.assertEquals(categoryDTO.getCategoryId(), "TEST");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void cloneInstance() throws Exception {
        //Adding website
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "description", "tes.com", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String) categoryData.get("name"));
            categoryDTO.setCategoryId((String) categoryData.get("externalId"));
            categoryDTO.setActive((String) categoryData.get("active"));
            categoryDTO.setDescription((String) categoryData.get("test.com"));
            categoryDAO.insert(categoryDTO);

            //Clone Category
            Category newCategory = categoryService.get(ID.EXTERNAL_ID(categoryDTO.getCategoryId()), false).orElse(null);
            Assert.assertTrue(newCategory != null);
         //   Assert.assertTrue(newCategory.diff(categoryDTO).isEmpty());

            Category categoryClone = categoryService.cloneInstance(ID.EXTERNAL_ID(newCategory.getCategoryId()), Entity.CloneType.LIGHT);
            Assert.assertTrue(categoryClone.getCategoryId() .equals(newCategory.getCategoryId() + "_COPY") && categoryClone.getCategoryName().equals(newCategory.getCategoryName() + "_COPY") && categoryClone.getDescription().equals(newCategory.getDescription() + "_COPY") && categoryClone.getActive() != newCategory.getActive());
        });
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void merge() throws Exception {
        //Create Category Original instance
        Category original = new Category();
        original.setCategoryId("One");
        original.setCategoryName("ONE");
        original.setExternalId("ONE");
        original.setDescription("ONE");
        original.setLongDescription("ONE");

        //Add Details or modified instance
        Category modified = new Category();
        modified.setGroup("DETAILS");
        modified.setCategoryName("One-A");
        modified.setCategoryId("ONE-A");
        modified.setExternalId("ONE-A");
        modified.setDescription("ONE-A");
        modified.setLongDescription("ONE-A");

        //Merge
        original = original.merge(modified);
        Assert.assertEquals(original.getCategoryName(), "One-A");
        Assert.assertEquals(original.getCategoryId(), "ONE-A");
        Assert.assertEquals(original.getExternalId(), "ONE-A");
        Assert.assertEquals(original.getDescription(), "ONE-A");
        Assert.assertEquals(original.getLongDescription(), "ONE-A");

        //Without Details
        Category modified1 = new Category();
        modified1.setCategoryName("One");
        modified1.setCategoryId("ONE");
        modified1.setExternalId("ONE");
        modified1.setDescription("ONE");
        modified1.setLongDescription("ONE");

        original = original.merge(modified1);
        Assert.assertEquals(original.getCategoryName(), "One-A");
        Assert.assertEquals(original.getCategoryId(), "ONE-A");
        Assert.assertEquals(original.getExternalId(), "ONE-A");
        Assert.assertEquals(original.getDescription(), "ONE-A");
        Assert.assertEquals(original.getLongDescription(), "ONE-A");

        //Add Seo
        Category modified2 = new Category();
        modified2.setGroup("SEO");
        modified2.setMetaTitle("One-A");
        modified2.setMetaDescription("ONE-A");
        modified2.setMetaKeywords("ONE-A");
        modified2.setCategoryName("One-A");

        original = original.merge(modified2);
        Assert.assertEquals(original.getMetaTitle(), "One-A");
        Assert.assertEquals(original.getMetaDescription(), "ONE-A");
        Assert.assertEquals(original.getMetaKeywords(), "ONE-A");
        Assert.assertEquals(original.getCategoryName(), "One-A");

        //Add Details or modified instance for update
        Category modified3 = new Category();
        modified.setGroup("DETAILS");
        modified.setCategoryName("Test-A");
        modified.setCategoryId("TEST-A");
        modified.setExternalId("TEST-A");
        modified.setDescription("TEST-A");
        modified.setLongDescription("TEST-A");

        //Merge for update
        original = original.merge(modified3);
        Assert.assertEquals(original.getCategoryName(), "One-A");
        Assert.assertEquals(original.getCategoryId(), "ONE-A");
        Assert.assertEquals(original.getExternalId(), "ONE-A");
        Assert.assertEquals(original.getDescription(), "ONE-A");
        Assert.assertEquals(original.getLongDescription(), "ONE-A");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
        //Create new Instance
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("Test");
        categoryDTO.setExternalId("TEST");

        //Create map for checking
        Map<String, String> map = new HashMap<>();
        map.put("categoryName", "Test");
        map.put("externalId", "TEST");

        //checking map1 and map2
        Map<String, String> map1 = categoryDTO.toMap();
        Assert.assertEquals(map1.get("categoryName"), map.get("categoryName"));
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void equals() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void equals1() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void diff() throws Exception {
        //Create first instance
        Category category1 = new Category();
        category1.setExternalId("test");
        category1.setCategoryName("test");
        category1.setDescription("test");

        //Create second instance
        Category category2 = new Category();
        category2.setExternalId("test");
        category2.setCategoryName("test.com");
        category2.setDescription("test");

        //Checking first instance and second instance
        Map<String, Object> diff = category1.diff(category2);
        Assert.assertEquals(diff.size(), 2);
        Assert.assertEquals(diff.get("categoryName"), "test.com");

        //Checking first instance and second instance
        Map<String, Object> diff1 = category1.diff(category2, true);
        Assert.assertEquals(diff1.size(), 1);
        Assert.assertEquals(diff1.get("categoryName"), "test.com");
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Category.class);
    }

}