package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.ValidatableEntity;
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

import java.util.*;

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
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
        websiteCatalogDAO.deleteAll();
    }

    @Test
    public void findAllWebsiteCatalogsTest() throws Exception {
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
    }

    @Test
    public void getAllWebsiteCatalogsTest() throws Exception {
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
        Assert.assertTrue(ValidationUtil.isNotEmpty(websiteCatalogList));
    }

    @Test
    public void findAvailableCatalogsForWebsiteTest() throws Exception {

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

        Page<Catalog> catalogPage = websiteService.findAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,"catalogName","Test", PageRequest.of(0, catalogsData.size()),false);
        Assert.assertEquals(catalogPage.getContent().size(), 4);

        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);

        catalogsData = new ArrayList<>();
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

        Catalog catalog = catalogService.get(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);
        WebsiteCatalog websiteCatalog = new WebsiteCatalog();
        websiteCatalog.setWebsiteId(website.getId());
        websiteCatalog.setCatalogId(catalog.getId());
        websiteCatalog.setActive(catalog.getActive());
        websiteCatalog.setSequenceNum(0);
        websiteCatalog.setSubSequenceNum(0);
        websiteCatalogDAO.insert(websiteCatalog);

        Page<Catalog> availableCatalogPage = websiteService.findAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,"catalogName","Test", PageRequest.of(0, catalogsData.size() - 1),false);
        Assert.assertEquals(availableCatalogPage.getContent().size(), 3);
       /* Assert.assertEquals(websiteService.findAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,"catalogName","Test", PageRequest.of(0, catalogsData.size() - 1),false).getTotalElements(), 3);
        Assert.assertEquals(websiteService.findAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,"catalogName","Test", PageRequest.of(0, catalogsData.size() - 1),false).getContent().size(), 3);
        Assert.assertEquals(websiteService.findAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,"catalogName","Test", PageRequest.of(1, 1),false).getContent().size(), 1);
        Assert.assertEquals(websiteService.findAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,"catalogName","Test", PageRequest.of(1, catalogsData.size() - 1),false).getContent().size(), 1);
        Assert.assertEquals(websiteService.findAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,"catalogName","Test", PageRequest.of(0, catalogsData.size() - 1),false).getTotalPages(), 2);*/

    }

    @Test
    public void getAvailableCatalogsForWebsiteTest() throws Exception {

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
        });

        Page<Catalog> catalogPage = websiteService.getAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, 0, catalogsData.size(), null, false);
        Assert.assertEquals(catalogPage.getContent().size(), 2);

        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);

        catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 1.com", "externalId", "TEST_CATALOG_1", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 2.com", "externalId", "TEST_CATALOG_2", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogService.create(catalogDTO);
        });

        Catalog catalog = catalogService.get(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);
        WebsiteCatalog websiteCatalog = new WebsiteCatalog();
        websiteCatalog.setWebsiteId(website.getId());
        websiteCatalog.setCatalogId(catalog.getId());
        websiteCatalog.setActive(catalog.getActive());
        websiteCatalog.setSequenceNum(0);
        websiteCatalog.setSubSequenceNum(0);
        websiteCatalogDAO.insert(websiteCatalog);

        Page<Catalog> availableCatalogPage = websiteService.getAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, 0, catalogsData.size(), null, false);
        Assert.assertEquals(availableCatalogPage.getContent().size(), 1);

    }

    @Test
    public void getWebsiteCatalogsTest() throws Exception {

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
    }

    @Test
    public void addCatalogTest() throws Exception {

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

        WebsiteCatalog websiteCatalog = websiteService.addCatalog(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID);
        WebsiteCatalog websiteCatalog1 =  websiteCatalogDAO.findById(websiteCatalog.getId()).orElse(null);
        Assert.assertEquals(websiteCatalog.getCatalogId(), websiteCatalog1.getCatalogId());



    }

    @Test
    public void getWebsiteByNameTest() throws Exception {
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
    public void getWebsiteByUrlTest() throws Exception {
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
        /* Create a valid new instance with id TEST */
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteName("test");
        websiteDTO.setUrl("www.test");
        websiteDTO.setWebsiteId("TEST");
        websiteDTO.setActive("Y");

        Map<String, Object> context = new HashMap<>();

        Class groups = ValidatableEntity.CreateGroup.class;
//        validate
        Assert.assertTrue(websiteService.validate(websiteDTO, context, groups).isEmpty());
//        insert the valid instance
        websiteDAO.insert(websiteDTO);

        /*Create a second instance with the same id TEST to check the unique constraint violation of websiteName*/

        Website websiteDTO1 = new Website();
        websiteDTO1.setWebsiteName("test");
        websiteDTO1.setUrl("www.envelope.com");
        websiteDTO1.setWebsiteId("ENVELOPE");
        websiteDTO1.setActive("Y");
        Assert.assertEquals(websiteService.validate(websiteDTO1, context, groups).size(), 1);


        //*Testing unique websiteName*//*
        context.put("forceUniqueId", true);
        websiteDTO1.setWebsiteName("Envelope");
        Assert.assertTrue(websiteService.validate(websiteDTO1, context, groups).isEmpty());
        Assert.assertEquals(websiteDTO1.getWebsiteName(), "Envelope");
        websiteDAO.insert(websiteDTO1);

        context.clear();

        //*Testing uniqueConstraint violation of websiteUrl with update operation*//*
        Website website = websiteDAO.findById(websiteDTO.getWebsiteId(), FindBy.EXTERNAL_ID).orElse(null);
        website.setUrl("www.envelope.com");
        website.setGroup("DETAILS");
        website.setActive("Y");
        context.put("id", websiteDTO.getExternalId());

        groups = ValidatableEntity.DetailsGroup.class;
        Assert.assertEquals(websiteService.validate(website, context, groups).size(), 1);

        //*Testing unique websiteUrl*//*
        context.put("forceUniqueId", true);
        website.setUrl("www.test.com");
        Assert.assertTrue(websiteService.validate(website, context, groups).isEmpty());
        Assert.assertEquals(website.getUrl(), "www.test.com");
        websiteDAO.save(website);

    }

    @After
    public void tearDown() throws Exception {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
        websiteCatalogDAO.deleteAll();
    }

}