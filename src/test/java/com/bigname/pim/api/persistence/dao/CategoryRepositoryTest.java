package com.bigname.pim.api.persistence.dao;

import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Category;
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

        categoryDTO.setCategoryName("Test1Name");
        categoryDTO.setCategoryId("TEST1_ID");
        categoryDTO.setDescription("Test1 category description");
        categoryDTO.setGroup("DETAILS");
        categoryDTO.setActive("N");
        categoryDAO.save(categoryDTO);

        Optional<Category> category = categoryDAO.findByExternalId(categoryDTO.getCategoryId());
        Assert.assertTrue(category.isPresent());
        category = categoryDAO.findById(categoryDTO.getCategoryId(), FindBy.EXTERNAL_ID) ;
        Assert.assertTrue(category.isPresent());
        category = categoryDAO.findById(categoryDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(category.isPresent());
    }

    @After
    public void tearDown() {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }
}
