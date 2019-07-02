package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
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

/**
 * Created by sanoop on 22/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class RootCategoryTest {
    @Autowired
    CatalogService catalogService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    CatalogDAO catalogDAO;
    private MongoTemplate mongoTemplate;
    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = ((GenericRepositoryImpl)catalogDAO).getMongoTemplate();
        }
        mongoTemplate.dropCollection(Catalog.class);
        mongoTemplate.dropCollection(Category.class);
    }
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
    @Test
    public void toMap() throws Exception {
    }

    @Test
    public void equals() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Catalog.class);
        mongoTemplate.dropCollection(Category.class);
    }

}