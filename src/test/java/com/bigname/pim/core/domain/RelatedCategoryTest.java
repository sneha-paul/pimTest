package com.bigname.pim.core.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.core.persistence.dao.mongo.CategoryDAO;
import com.bigname.pim.core.service.CategoryService;
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

/**
 * Created by sanoop on 22/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class RelatedCategoryTest {
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

        Category category = categoryService.get(ID.EXTERNAL_ID(categoryDTO.getCategoryId()), false).orElse(null);

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
        RelatedCategory relatedCategory = categoryService.addSubCategory(ID.EXTERNAL_ID(categoryDTO1.getCategoryId()), ID.EXTERNAL_ID(category.getCategoryId()));
        Assert.assertEquals(relatedCategory.getCategoryId(), categoryDTO1.getId());
        Assert.assertEquals(relatedCategory.getSubCategoryId(), categoryDTO.getId());
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
    public void getSubCategoryId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setSubCategoryId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getFullSubCategoryId() throws Exception {
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void setFullSubCategoryId() throws Exception {
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
        mongoTemplate.dropCollection(Category.class);
    }


}