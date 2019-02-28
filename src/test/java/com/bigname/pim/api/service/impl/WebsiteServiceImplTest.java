package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.WebsiteCatalogDAO;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class WebsiteServiceImplTest {

    @Autowired
    WebsiteService websiteService;
    @Autowired
    CatalogService catalogService;
    @Autowired
    WebsiteDAO websiteDAO;
    @Autowired
    WebsiteCatalogDAO websiteCatalogDAO;
    @Autowired
    CatalogDAO catalogDAO;

    @Before
    public void setUp() {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

    @Test
    public void findAllWebsiteCatalogs() throws Exception {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        Website website = websiteService.get(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 1.com", "externalId", "TEST_CATALOG_1", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 2.com", "externalId", "TEST_CATALOG_2", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogService.create(catalogDTO);

            Catalog catalog = catalogService.get((String)catalogData.get("externalId"), FindBy.EXTERNAL_ID,false).orElse(null);

            WebsiteCatalog websiteCatalog = new WebsiteCatalog();
            websiteCatalog.setWebsiteId(website.getId());
            websiteCatalog.setCatalogId(catalog.getId());
            websiteCatalog.setActive(catalog.getActive());
            websiteCatalog.setSequenceNum(0);
            websiteCatalog.setSubSequenceNum(0);
            websiteCatalogDAO.insert(websiteCatalog);
        });

        Page<Map<String, Object>> websiteCatalog =  websiteService.findAllWebsiteCatalogs(website.getWebsiteId(), FindBy.EXTERNAL_ID, "catalogName","test", PageRequest.of(0, catalogsData.size(), null),false);
        Assert.assertEquals(websiteCatalog.getSize(),catalogsData.size());

        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }

    @Test
    public void getAllWebsiteCatalogs() throws Exception {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        Website website = websiteService.get(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 1.com", "externalId", "TEST_CATALOG_1", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 2.com", "externalId", "TEST_CATALOG_2", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 3.com", "externalId", "TEST_CATALOG_3", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 4.com", "externalId", "TEST_CATALOG_4", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogService.create(catalogDTO);

            Catalog catalog = catalogService.get((String)catalogData.get("externalId"), FindBy.EXTERNAL_ID,false).orElse(null);

            WebsiteCatalog websiteCatalog = new WebsiteCatalog();
            websiteCatalog.setWebsiteId(website.getId());
            websiteCatalog.setCatalogId(catalog.getId());
            websiteCatalog.setActive(catalog.getActive());
            websiteCatalog.setSequenceNum(0);
            websiteCatalog.setSubSequenceNum(0);
            websiteCatalogDAO.insert(websiteCatalog);
        });

        List<WebsiteCatalog> websiteCatalogList = websiteService.getAllWebsiteCatalogs(website.getId());
        Assert.assertTrue(websiteCatalogList != null);

        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }

    @Test
    public void findAvailableCatalogsForWebsite() throws Exception {

        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 1.com", "externalId", "TEST_CATALOG_1", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 2.com", "externalId", "TEST_CATALOG_2", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 3.com", "externalId", "TEST_CATALOG_3", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 4.com", "externalId", "TEST_CATALOG_4", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogService.create(catalogDTO);
        });

        Page<Catalog> catalogPage = websiteService.findAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,"catalogName","Test", PageRequest.of(0, catalogsData.size()),false);
        Assert.assertEquals(catalogPage.getContent().size(), 4);

        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }

    @Test
    public void getAvailableCatalogsForWebsite() throws Exception {

        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 1.com", "externalId", "TEST_CATALOG_1", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 2.com", "externalId", "TEST_CATALOG_2", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogService.create(catalogDTO);
        });

        Page<Catalog> catalogPage = websiteService.getAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, 0, catalogsData.size(), null, false);
        Assert.assertEquals(catalogPage.getContent().size(), 2);

        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }

    @Test
    public void getWebsiteCatalogs() throws Exception {

        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        Website website = websiteService.get(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 1.com", "externalId", "TEST_CATALOG_1", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 2.com", "externalId", "TEST_CATALOG_2", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 3.com", "externalId", "TEST_CATALOG_3", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 4.com", "externalId", "TEST_CATALOG_4", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogService.create(catalogDTO);

            Catalog catalog = catalogService.get((String)catalogData.get("externalId"), FindBy.EXTERNAL_ID,false).orElse(null);

            WebsiteCatalog websiteCatalog = new WebsiteCatalog();
            websiteCatalog.setWebsiteId(website.getId());
            websiteCatalog.setCatalogId(catalog.getId());
            websiteCatalog.setActive(catalog.getActive());
            websiteCatalog.setSequenceNum(0);
            websiteCatalog.setSubSequenceNum(0);
            websiteCatalogDAO.insert(websiteCatalog);
        });

        Page<Map<String, Object>> websiteCatalogMap =  websiteService.getWebsiteCatalogs(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, PageRequest.of(0, catalogsData.size(), null), false);// TODO pagination check
        Assert.assertEquals(websiteCatalogMap.getSize(), catalogsData.size());

        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }

    @Test
    public void addCatalog() throws Exception {

        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        Website website = websiteService.get(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 1.com", "externalId", "TEST_CATALOG_1", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 2.com", "externalId", "TEST_CATALOG_2", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 3.com", "externalId", "TEST_CATALOG_3", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 4.com", "externalId", "TEST_CATALOG_4", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogService.create(catalogDTO);
        });

        WebsiteCatalog websiteCatalog = websiteService.addCatalog(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID);
        WebsiteCatalog websiteCatalog1 =  websiteCatalogDAO.findById(websiteCatalog.getId()).orElse(null);
        Assert.assertEquals(websiteCatalog.getCatalogId(), websiteCatalog1.getCatalogId());

        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);

    }

    @Test
    public void getWebsiteByName() throws Exception {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        Optional<Website> website = websiteService.getWebsiteByName(websitesData.get(0).get("name").toString());
        Assert.assertTrue(website.isPresent());
        Website website1 = websiteService.getWebsiteByName(websitesData.get(0).get("name").toString()).orElse(null);
        Assert.assertEquals(website1.getWebsiteName(), websitesData.get(0).get("name").toString());

    }

    @Test
    public void getWebsiteByUrl() throws Exception {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        Optional<Website> website = websiteService.getWebsiteByUrl(websitesData.get(0).get("url").toString());
        Assert.assertTrue(website.isPresent());
        Website website1 = websiteService.getWebsiteByUrl(websitesData.get(0).get("url").toString()).orElse(null);
        Assert.assertEquals(website1.getUrl(), websitesData.get(0).get("url").toString());
    }

    @Test
    public void validate() throws Exception {
    }

    @After
    public void tearDown() throws Exception {websiteDAO.getMongoTemplate().dropCollection(Website.class);}

}