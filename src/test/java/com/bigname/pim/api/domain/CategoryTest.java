package com.bigname.pim.api.domain;

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

    @After
    public void tearDown() throws Exception {
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }

    @Test
    public void getCategoryId() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getCategoryId(), categoryDTO.getCategoryId());
    }

    @Test
    public void setCategoryId() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getCategoryId(), categoryDTO.getCategoryId());
    }

    @Test
    public void getCategoryName() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getCategoryName(), categoryDTO.getCategoryName());
    }

    @Test
    public void setCategoryName() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getCategoryName(), categoryDTO.getCategoryName());
    }

    @Test
    public void getDescription() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getDescription(), categoryDTO.getDescription());

    }

    @Test
    public void setDescription() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getDescription(), categoryDTO.getDescription());
    }

    @Test
    public void getLongDescription() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getLongDescription(), categoryDTO.getLongDescription());

    }

    @Test
    public void setLongDescription() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getLongDescription(), categoryDTO.getLongDescription());
    }

    @Test
    public void getMetaTitle() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getMetaTitle(), categoryDTO.getMetaTitle());
    }

    @Test
    public void setMetaTitle() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getMetaTitle(), categoryDTO.getMetaTitle());
    }

    @Test
    public void getMetaDescription() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getMetaDescription(), categoryDTO.getMetaDescription());
    }

    @Test
    public void setMetaDescription() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getMetaDescription(), categoryDTO.getMetaDescription());
    }

    @Test
    public void getMetaKeywords() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getMetaKeywords(), categoryDTO.getMetaKeywords());
    }

    @Test
    public void setMetaKeywords() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getMetaKeywords(), categoryDTO.getMetaKeywords());
    }

    @Test
    public void setExternalId() throws Exception {
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("TEST");
        categoryDTO.setActive("Y");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");

        categoryService.create(categoryDTO);
        Category newCategory = categoryService.get(categoryDTO.getCategoryId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newCategory != null);
        Assert.assertEquals(newCategory.getExternalId(), categoryDTO.getExternalId());
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
    }

    @Test
    public void toMap() throws Exception {
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

}