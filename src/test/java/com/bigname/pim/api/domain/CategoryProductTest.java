package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.mongo.CategoryDAO;
import com.bigname.pim.api.persistence.dao.mongo.FamilyDAO;
import com.bigname.pim.api.persistence.dao.mongo.ProductDAO;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ValidationUtil;
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
    private ProductService productService;
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private FamilyService familyService;
    @Autowired
    private FamilyDAO familyDAO;
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
            mongoTemplate = (MongoTemplate) productDAO.getTemplate();
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

        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Family.class);
        mongoTemplate.dropCollection(Category.class);
    }

    @WithUserDetails("manu@blacwood.com")
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
        productsData.add(CollectionsUtil.toMap("name", "Product Test 1", "externalId", "PRODUCT_TEST_1", "productFamilyId", familyDetails.getFamilyId(), "active", "Y"));
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
            categoryDTO.setCategoryName((String) categoryData.get("name"));
            categoryDTO.setCategoryId((String) categoryData.get("externalId"));
            categoryDTO.setActive((String) categoryData.get("active"));
            categoryDTO.setDescription((String) categoryData.get("description"));
            categoryService.create(categoryDTO);
            Category category = categoryService.get(ID.EXTERNAL_ID(categoriesData.get(0).get("externalId").toString()), false).orElse(null);

            CategoryProduct categoryProduct = categoryService.addProduct(ID.EXTERNAL_ID(categoryDTO.getCategoryId()), ID.EXTERNAL_ID(product.getProductId()));

            Assert.assertEquals(categoryProduct.getProductId(), product.getId());
            Assert.assertEquals(categoryProduct.getCategoryId(), category.getId());
        });
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void init() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getCategoryId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setCategoryId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getProductId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setProductId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toMap() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void equals() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Family.class);
        mongoTemplate.dropCollection(Category.class);
    }

}