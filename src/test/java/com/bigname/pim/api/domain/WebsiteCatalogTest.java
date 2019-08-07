package com.bigname.pim.api.domain;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.mongo.CatalogDAO;
import com.bigname.pim.api.persistence.dao.mongo.WebsiteDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
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
 * Created by sanoop on 21/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class WebsiteCatalogTest {
    @Autowired
    private WebsiteService websiteService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CatalogDAO catalogDAO;
    @Autowired
    private WebsiteDAO websiteDAO;
    @Autowired
    private UserDAO userDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) websiteDAO.getTemplate();
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
        mongoTemplate.dropCollection(Website.class);
        mongoTemplate.dropCollection(Catalog.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void accessorsTest() {
        //Create Catalog
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("test");
        catalogDTO.setCatalogId("test");
        catalogDTO.setDescription("test");
        catalogService.create(catalogDTO);

        Catalog catalog = catalogService.get(ID.EXTERNAL_ID(catalogDTO.getCatalogId()), false).orElse(null);

        //Create Website
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteId("test");
        websiteDTO.setWebsiteName("Test.com");
        websiteDTO.setUrl("www.test.com");
        websiteDTO.setExternalId("TEST");
        websiteService.create(websiteDTO);

        //Equals Checking Catalog And Website
        WebsiteCatalog websiteCatalog = websiteService.addCatalog(ID.EXTERNAL_ID(websiteDTO.getWebsiteId()), ID.EXTERNAL_ID(catalog.getCatalogId()));
        Assert.assertEquals(websiteCatalog.getCatalogId(), catalog.getId());
        Assert.assertEquals(websiteCatalog.getWebsiteId(), websiteDTO.getId());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void init() throws Exception {
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
        mongoTemplate.dropCollection(Website.class);
        mongoTemplate.dropCollection(Catalog.class);
    }
}