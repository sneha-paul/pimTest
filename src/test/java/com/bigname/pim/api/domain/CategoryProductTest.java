package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
import com.m7.common.util.CollectionsUtil;
import com.m7.xcore.util.FindBy;
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

/**
 * Created by sanoop on 22/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CategoryProductTest {
    @Autowired
    ProductService productService;
    @Autowired
    ProductDAO productDAO;
    @Autowired
    FamilyService familyService;
    @Autowired
    FamilyDAO familyDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryDAO categoryDAO;
    @Before
    public void setUp() throws Exception {
        productDAO.getMongoTemplate().dropCollection(Product.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }
    @Test
    public void accessorsTest() {
        //Creating families
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Family Test1", "externalId", "FAMILY_TEST_1", "active", "Y", "discontinue", "N"));

        familiesData.forEach((Map<String, Object> familyData) -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String) familyData.get("name"));
            familyDTO.setFamilyId((String) familyData.get("externalId"));
            familyDTO.setActive((String) familyData.get("active"));
            familyDTO.setDiscontinued((String) familyData.get("discontinue"));
            familyService.create(familyDTO);
        });

        Family familyDetails = familyService.get(familiesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String) productData.get("name"));
            productDTO.setProductId((String) productData.get("externalId"));
            productDTO.setProductFamilyId((String) productData.get("productFamilyId"));
            productDTO.setActive((String) productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(productsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

        //Create Product Category
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Category Test 1", "externalId", "CATEGORY_TEST_1", "description", "Test", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String) categoryData.get("name"));
            categoryDTO.setCategoryId((String) categoryData.get("externalId"));
            categoryDTO.setActive((String) categoryData.get("active"));
            categoryDTO.setDescription((String) categoryData.get("description"));
            categoryService.create(categoryDTO);
            Category category = categoryService.get(categoriesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, false).orElse(null);

            CategoryProduct categoryProduct = categoryService.addProduct(categoryDTO.getCategoryId(), FindBy.EXTERNAL_ID, product.getProductId(), FindBy.EXTERNAL_ID);

            Assert.assertEquals(categoryProduct.getProductId(), product.getId());
            Assert.assertEquals(categoryProduct.getCategoryId(), category.getId());
        });
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
    public void getProductId() throws Exception {
    }

    @Test
    public void setProductId() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }

    @Test
    public void equals() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        productDAO.getMongoTemplate().dropCollection(Product.class);
        familyDAO.getMongoTemplate().dropCollection(Family.class);
        categoryDAO.getMongoTemplate().dropCollection(Category.class);
    }

}