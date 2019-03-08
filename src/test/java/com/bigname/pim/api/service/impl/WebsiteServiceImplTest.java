package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.Entity;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.WebsiteCatalogDAO;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.util.PimUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;

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
    public void createEntityTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));

        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));

            websiteService.create(websiteDTO);

            Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newWebsite));
            Assert.assertTrue(newWebsite.diff(websiteDTO).isEmpty());
        });
    }

    @Test
    public void createEntitiesTest(){
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        List<Website> websiteDTOs = websitesData.stream().map(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            return websiteDTO;
        }).collect(Collectors.toList());

        websiteService.create(websiteDTOs);

        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size()), false).getTotalElements(), websitesData.size());

    }

    @Test
    public void toggleTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));

        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Website websiteDetails = websiteService.get(websitesData.get(0).get("externalId").toString(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(websiteDetails != null);
        websiteService.toggle(websiteDetails.getWebsiteId(), EXTERNAL_ID, Toggle.get(websiteDetails.getActive()));

        Website updatedWebsite = websiteService.get(websiteDetails.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(updatedWebsite != null);
        Map<String, Object> diff = websiteDetails.diff(updatedWebsite);
        Assert.assertEquals(diff.size(), 1);
        Assert.assertEquals(diff.get("active"), "N");
    }

    @Test
    public void getTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test1.com", "active", "Y"));

        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);

            Website websiteDetails = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(websiteDetails != null);
            Map<String, Object> diff = websiteDTO.diff(websiteDetails);
            Assert.assertEquals(diff.size(), 0);
        });
    }

    @Test
    public void getAllAsPageTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Page<Website> paginatedResult = websiteService.getAll(0, 10, null,false);
        Assert.assertEquals(paginatedResult.getContent().size(), websitesData.size());
    }

    @Test
    public void getAllAsListTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));        ;
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        List<Website> result = websiteService.getAll(Sort.by("websiteName").ascending(), false);
        String[] actual = result.stream().map(website -> website.getWebsiteName()).collect(Collectors.toList()).toArray(new String[0]);
        String[] expected = websitesData.stream().map(websiteData -> (String)websiteData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertArrayEquals(expected, actual);

        websiteDAO.getMongoTemplate().dropCollection(Website.class);

        // sorting : Descending

        websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        result = websiteService.getAll(Sort.by("websiteName").descending(), false);
        actual = result.stream().map(website -> website.getWebsiteName()).collect(Collectors.toList()).toArray(new String[0]);
        expected = websitesData.stream().map(websiteData -> (String)websiteData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertNotEquals(expected, actual);
    }

    @Test
    public void getAllWithIdsAsPageTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        String[] ids = {websitesData.get(0).get("externalId").toString(), websitesData.get(1).get("externalId").toString(), websitesData.get(2).get("externalId").toString()};

        Page<Website> paginatedResult = websiteService.getAll(ids, EXTERNAL_ID, 0, 10, null, false);
        Map<String, Website> websitesMap = paginatedResult.getContent().stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == ids.length && websitesMap.containsKey(ids[0]) && websitesMap.containsKey(ids[1]) && websitesMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithIdsAsListTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        String[] ids = {websitesData.get(0).get("externalId").toString(), websitesData.get(1).get("externalId").toString(), websitesData.get(2).get("externalId").toString()};

        List<Website> paginatedResult = websiteService.getAll(ids, EXTERNAL_ID, null, false);
        Map<String, Website> websitesMap = paginatedResult.stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == ids.length && websitesMap.containsKey(ids[0]) && websitesMap.containsKey(ids[1]) && websitesMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithExclusionsAsPageTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        String[] ids = {websitesData.get(0).get("externalId").toString(), websitesData.get(1).get("externalId").toString(), websitesData.get(2).get("externalId").toString()};

        Page<Website> paginatedResult = websiteService.getAllWithExclusions(ids, EXTERNAL_ID, 0, 10, null, false);
        Map<String, Website> websitesMap = paginatedResult.getContent().stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == (websitesData.size() - ids.length) && !websitesMap.containsKey(ids[0]) && !websitesMap.containsKey(ids[1]) && !websitesMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithExclusionsAsListTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        String[] ids = {websitesData.get(0).get("externalId").toString(), websitesData.get(1).get("externalId").toString(), websitesData.get(2).get("externalId").toString()};

        List<Website> paginatedResult = websiteService.getAllWithExclusions(ids, EXTERNAL_ID, null, false);
        Map<String, Website> websitesMap = paginatedResult.stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == (websitesData.size() - ids.length) && !websitesMap.containsKey(ids[0]) && !websitesMap.containsKey(ids[1]) && !websitesMap.containsKey(ids[2]));
    }

    @Test
    public void findAllAtSearchTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Page<Website> paginatedResult = websiteService.findAll("name", "Test", PageRequest.of(0, websitesData.size()), false);
        Assert.assertEquals(paginatedResult.getContent().size(), 9);
    }

    @Test
    public void findAllTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Page<Website> paginatedResult = websiteService.findAll(PageRequest.of(0, websitesData.size()), false);
        Assert.assertEquals(paginatedResult.getContent().size(), 9);
    }

    @Test
    public void updateEntityTest() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));

        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);

            Website websiteDetails = websiteService.get(websitesData.get(0).get("externalId").toString(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(websiteDetails));
            websiteDetails.setUrl("https://www.test11.com");
            websiteDetails.setGroup("DETAILS");

            websiteService.update(websiteDetails.getWebsiteId(), EXTERNAL_ID, websiteDetails);

            Website updatedWebsite = websiteService.get(websiteDetails.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(updatedWebsite));
            Map<String, Object> diff = websiteDTO.diff(updatedWebsite);
            Assert.assertEquals(diff.size(), 1);
            Assert.assertEquals(diff.get("url"), "https://www.test11.com");
        });

    }

    @Test
    public void updateEntitiesTest(){
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        String[] ids = {websitesData.get(0).get("externalId").toString(), websitesData.get(1).get("externalId").toString(), websitesData.get(2).get("externalId").toString()};

        List<Website> result = websiteService.getAll(ids, EXTERNAL_ID, null, false);
        Map<String, Website> websitesMap = result.stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == ids.length && websitesMap.containsKey(ids[0]) && websitesMap.containsKey(ids[1]) && websitesMap.containsKey(ids[2]));

        List<Website> websites = result.stream().map(result1 -> {
            result1.setActive("N");
            return result1;
        }).collect(Collectors.toList());

        websiteService.update(websites);

        result = websiteService.getAll(Sort.by("websiteName").descending(), true);
        websitesMap = result.stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == (websitesData.size() - ids.length) && !websitesMap.containsKey(ids[0]) && !websitesMap.containsKey(ids[1]) && !websitesMap.containsKey(ids[2]));
        Assert.assertFalse(websitesMap.size() == websitesData.size() && websitesMap.containsKey(ids[0]) && websitesMap.containsKey(ids[1]) && websitesMap.containsKey(ids[2]));
    }

    @Test
    public void cloneInstance() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));

        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);

            Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newWebsite));
            Assert.assertTrue(newWebsite.diff(websiteDTO).isEmpty());

            Website websiteClone = websiteService.cloneInstance(newWebsite.getWebsiteId(), EXTERNAL_ID, Entity.CloneType.LIGHT);
            Assert.assertTrue(websiteClone.getWebsiteId() .equals(newWebsite.getWebsiteId() + "_COPY") && websiteClone.getWebsiteName().equals(newWebsite.getWebsiteName() + "_COPY") && websiteClone.getUrl().equals(newWebsite.getUrl() + "_COPY") && websiteClone.getActive() != newWebsite.getActive());
        });
    }

    @Test
    public void findAll() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "N"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "N"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        List<Website> result = websiteService.findAll(CollectionsUtil.toMap("active", "N"));
        Assert.assertTrue(result.size() == 2);
    }

    @Test
    public void findAll1() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "N"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "N"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Criteria criteria = PimUtil.buildCriteria(CollectionsUtil.toMap("active", "N"));
        List<Website> result = websiteService.findAll(criteria);
        Assert.assertTrue(result.size() == 2);
    }

    @Test
    public void findOne() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "N"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "N"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Optional<Website> result = websiteService.findOne(CollectionsUtil.toMap("websiteName", websitesData.get(0).get("name")));
        Assert.assertEquals(websitesData.get(0).get("name"), result.get().getWebsiteName());
    }

    @Test
    public void findOne1() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "N"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "N"));

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Criteria criteria = PimUtil.buildCriteria(CollectionsUtil.toMap("websiteName", websitesData.get(0).get("name")));
        Optional<Website> result = websiteService.findOne(criteria);
        Assert.assertEquals(websitesData.get(0).get("name"), result.get().getWebsiteName());
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