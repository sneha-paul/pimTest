package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.api.persistence.dao.ProductCategoryDAO;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
import com.m7.xtreme.common.util.CollectionsUtil;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sanoop on 22/03/2019.
 */@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class ProductCategoryTest {
    @Autowired
    ProductService productService;
    @Autowired
    ProductDAO productDAO;
    @Autowired
    FamilyService familyService;
    @Autowired
    FamilyDAO familyDAO;
    @Autowired
    ProductCategoryDAO productCategoryDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryDAO categoryDAO;
    private MongoTemplate mongoTemplate;
    
    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = ((GenericRepositoryImpl)productDAO).getMongoTemplate();
        }
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Family.class);
        mongoTemplate.dropCollection(Category.class);
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

        Family familyDetails = familyService.get(ID.EXTERNAL_ID(familiesData.get(0).get("externalId").toString()), false).orElse(null);

        //creating products
        List<Map<String, Object>> productsData = new ArrayList<>();
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId","PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
        productsData.forEach(productData -> {
            Product productDTO = new Product();
            productDTO.setProductName((String) productData.get("name"));
            productDTO.setProductId((String) productData.get("externalId"));
            productDTO.setProductFamilyId((String) productData.get("productFamilyId"));
            productDTO.setActive((String) productData.get("active"));
            productService.create(productDTO);
        });

        Product product = productService.get(ID.EXTERNAL_ID(productsData.get(0).get("externalId").toString()), false).orElse(null);

        //Create Product Category
        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Category Test 1", "externalId", "CATEGORY_TEST_1", "description", "Test", "active", "Y"));
        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);
            Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), false).orElse(null);

          ProductCategory productCategory = productService.addCategory(ID.EXTERNAL_ID(product.getProductId()), ID.EXTERNAL_ID(categoryDTO.getCategoryId()));

            Assert.assertEquals(productCategory.getProductId(), product.getId());
            Assert.assertEquals(productCategory.getCategoryId(), category.getId());
        });
    }

    @Test
    public void init() throws Exception {
    }

    @Test
    public void getProductId() throws Exception {
    }

    @Test
    public void setProductId() throws Exception {
    }

    @Test
    public void getCategoryId() throws Exception {
    }

    @Test
    public void setCategoryId() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Family.class);
        mongoTemplate.dropCollection(Category.class);
    }

}