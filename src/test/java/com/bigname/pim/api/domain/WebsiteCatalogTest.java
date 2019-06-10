package com.bigname.pim.api.domain;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
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


/**
 * Created by sanoop on 21/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class WebsiteCatalogTest {
    @Autowired
    WebsiteService websiteService;
    @Autowired
    CatalogService catalogService;
    @Autowired
    CatalogDAO catalogDAO;
    @Autowired
    WebsiteDAO websiteDAO;
    @Before
    public void setUp() throws Exception {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }
    @Test
    public void accessorsTest() {
        //Create Catalog
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("test");
        catalogDTO.setCatalogId("test");
        catalogDTO.setDescription("test");
        catalogService.create(catalogDTO);

        Catalog catalog = catalogService.get(catalogDTO.getCatalogId(), FindBy.EXTERNAL_ID, false).orElse(null);

        //Create Website
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteId("test");
        websiteDTO.setWebsiteName("Test.com");
        websiteDTO.setUrl("www.test.com");
        websiteDTO.setExternalId("TEST");
        websiteService.create(websiteDTO);

        //Equals Checking Catalog And Website
        WebsiteCatalog websiteCatalog = websiteService.addCatalog(websiteDTO.getWebsiteId(), FindBy.EXTERNAL_ID, catalog.getCatalogId(), FindBy.EXTERNAL_ID);
        Assert.assertEquals(websiteCatalog.getCatalogId(), catalog.getId());
        Assert.assertEquals(websiteCatalog.getWebsiteId(), websiteDTO.getId());
    }

    @Test
    public void init() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }

    @Test
    public void equals() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }
}