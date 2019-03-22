package com.bigname.pim.api.domain;

import com.bigname.core.util.FindBy;
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

import static org.junit.Assert.*;

/**
 * Created by sanoop on 22/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class RelatedCategoryTest {
    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryDAO categoryDAO;
    @Before
    public void setUp() throws Exception {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }
    @Test
    public void accessorsTest() {
        //Create Child Category Instance
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");
        categoryService.create(categoryDTO);

        Category category = categoryService.get(categoryDTO.getCategoryId(), FindBy.EXTERNAL_ID, false).orElse(null);

        //Create Parent Category Instance
        Category categoryDTO1 = new Category();
        categoryDTO1.setCategoryName("test1");
        categoryDTO1.setDescription("test1");
        categoryDTO1.setCategoryId("TEST1");
        categoryDTO1.setLongDescription("test1");
        categoryDTO1.setMetaTitle("test1");
        categoryDTO1.setMetaDescription("test1");
        categoryService.create(categoryDTO1);

        //Checking Parent id and Child id
        RelatedCategory relatedCategory = categoryService.addSubCategory(categoryDTO1.getCategoryId(), FindBy.EXTERNAL_ID, category.getCategoryId(), FindBy.EXTERNAL_ID);
        Assert.assertEquals(relatedCategory.getCategoryId(), categoryDTO1.getId());
        Assert.assertEquals(relatedCategory.getSubCategoryId(), categoryDTO.getId());
    }
    @Test
    public void init() throws Exception {
    }

    @Test
    public void getCategoryId() throws Exception {
    }

    @Test
    public void setCategoryId() throws Exception {
    }

    @Test
    public void getSubCategoryId() throws Exception {
    }

    @Test
    public void setSubCategoryId() throws Exception {
    }

    @Test
    public void getFullSubCategoryId() throws Exception {
    }

    @Test
    public void setFullSubCategoryId() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }

    @Test
    public void equals() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }


}