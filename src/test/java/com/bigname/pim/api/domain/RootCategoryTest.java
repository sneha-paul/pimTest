package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.mongo.CatalogDAO;
import com.bigname.pim.api.persistence.dao.mongo.CategoryDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.persistence.dao.mongo.UserDAO;
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
public class RootCategoryTest {
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private CatalogDAO catalogDAO;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) catalogDAO.getTemplate();
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
        mongoTemplate.dropCollection(Catalog.class);
        mongoTemplate.dropCollection(Category.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest() {
        //Create new instance
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("test");
        catalogDTO.setCatalogId("test");
        catalogDTO.setDescription("test");
        catalogService.create(catalogDTO);

        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogDTO.getCatalogId()), false).orElse(null);

        //Create New Instance
        Category categoryDTO = new Category();
        categoryDTO.setCategoryName("test");
        categoryDTO.setDescription("test");
        categoryDTO.setCategoryId("test");
        categoryDTO.setLongDescription("test");
        categoryDTO.setMetaTitle("test");
        categoryDTO.setMetaDescription("test");
        categoryService.create(categoryDTO);

        //Equals Checking With Cate
        RootCategory rootCategory = catalogService.addRootCategory(ID.EXTERNAL_ID(catalog.getCatalogId()), ID.EXTERNAL_ID(categoryDTO.getCategoryId()));
        Assert.assertEquals(rootCategory.getCatalogId(), catalog.getId());
        Assert.assertEquals(rootCategory.getRootCategoryId(), categoryDTO.getId());
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
        mongoTemplate.dropCollection(Catalog.class);
        mongoTemplate.dropCollection(Category.class);
    }

}