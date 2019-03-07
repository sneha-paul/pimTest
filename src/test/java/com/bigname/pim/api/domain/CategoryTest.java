package com.bigname.pim.api.domain;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.Entity;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.service.CategoryService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static org.junit.Assert.*;

/**
 * Created by sanoop on 05/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CategoryTest {

    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryDAO categoryDAO;

    @Before
    public void setUp() throws Exception {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }
    @Test
    public void accessorsTest(){
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("test");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryDTO.orchestrate();

        Assert.assertEquals(categoryDTO.getCategoryId(), "TEST");
        Assert.assertEquals(categoryDTO.getCategoryName(), "test");
        Assert.assertEquals(categoryDTO.getDescription(), "test");
        Assert.assertEquals(categoryDTO.getLongDescription(), "test");
        Assert.assertEquals(categoryDTO.getMetaTitle(), "test");
        Assert.assertEquals(categoryDTO.getMetaDescription(), "test");
        Assert.assertEquals(categoryDTO.getActive(), "N");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newCategory));

        Assert.assertEquals(newCategory.getCategoryId(), categoryDTO.getCategoryId());
        Assert.assertEquals(newCategory.getCategoryName(), categoryDTO.getCategoryName());
        Assert.assertEquals(newCategory.getDescription(), categoryDTO.getDescription());
        Assert.assertEquals(newCategory.getLongDescription(), categoryDTO.getLongDescription());
        Assert.assertEquals(newCategory.getMetaTitle(), categoryDTO.getMetaTitle());
        Assert.assertEquals(newCategory.getMetaDescription(), categoryDTO.getMetaDescription());
        Assert.assertEquals(newCategory.getActive(), categoryDTO.getActive());

    }
    @Test
    public void getSubCategories() throws Exception {
    }

    @Test
    public void setSubCategories() throws Exception {
    }

    @Test
    public void orchestrate() throws Exception {
    }

    @Test
    public void cloneInstance() throws Exception {

    }

    @Test
    public void merge() throws Exception {
        //Create Website Original
        Category original = new Category();
        original.setCategoryId("One");
        original.setCategoryName("ONE");
        original.setExternalId("ONE");
        original.setDescription("ONE");
        original.setLongDescription("ONE");

        //Add Details
        Category modified = new Category();
        modified.setGroup("DETAILS");
        modified.setCategoryName("One-A");
        modified.setCategoryId("ONE-A");
        modified.setExternalId("ONE-A");
        modified.setDescription("ONE-A");
        modified.setLongDescription("ONE-A");

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

        original = original.merge(modified2);
        Assert.assertEquals(original.getMetaTitle(), "One-A");
        Assert.assertEquals(original.getMetaDescription(), "ONE-A");
        Assert.assertEquals(original.getMetaKeywords(), "ONE-A");

    }

    @Test
    public void toMap() throws Exception {
        //Create new Instance
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setCategoryId("test");

        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryDTO.getCategoryId()));

        //Testing equals with id
        Assert.assertEquals(categoryDTO.getCategoryId(), "TEST");
        Assert.assertEquals(categoryDTO.getCategoryName(), "test");

        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryDTO.getCategoryId()));
        Assert.assertTrue(ValidationUtil.isNotEmpty(categoryDTO.getCategoryName()));
    }

    @Test
    public void equals() throws Exception {
    }

    @Test
    public void equals1() throws Exception {
    }

    @Test
    public void diff() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }

}