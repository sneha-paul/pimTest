package com.bigname.pim.api.service.impl;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
import com.bigname.pim.api.persistence.dao.mongo.CatalogDAO;
import com.bigname.pim.api.persistence.dao.mongo.WebsiteCatalogDAO;
import com.bigname.pim.api.persistence.dao.mongo.WebsiteDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.util.GenericCriteria;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;


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
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) websiteDAO.getTemplate();
        }
		mongoTemplate.dropCollection(Website.class);
		mongoTemplate.dropCollection(Catalog.class);
        websiteCatalogDAO.deleteAll();
    }

    @Test
    public void findAllWebsiteCatalogsTest() throws Exception {
        //creating websites
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

        websiteService.get(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()),false)
                .ifPresent(website -> {
                    //creating catalogs
                    List<Map<String, Object>> catalogsData = new ArrayList<>();
                    catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 1.com", "externalId", "TEST_CATALOG_1", "active", "Y"));
                    catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 2.com", "externalId", "TEST_CATALOG_2", "active", "Y"));
                    catalogsData.forEach(catalogData -> {
                        Catalog catalogDTO = new Catalog();
                        catalogDTO.setCatalogName((String)catalogData.get("name"));
                        catalogDTO.setCatalogId((String)catalogData.get("externalId"));
                        catalogDTO.setActive((String)catalogData.get("active"));
                        catalogService.create(catalogDTO);

                        //creating websiteCatalog
                        catalogService.get(ID.EXTERNAL_ID((String)catalogData.get("externalId")),false)
                                .ifPresent(catalog -> {
                                    WebsiteCatalog websiteCatalog = new WebsiteCatalog();
                                    websiteCatalog.setWebsiteId(website.getId());
                                    websiteCatalog.setCatalogId(catalog.getId());
                                    websiteCatalog.setActive(catalog.getActive());
                                    websiteCatalog.setSequenceNum(0);
                                    websiteCatalog.setSubSequenceNum(0);
                                    websiteCatalogDAO.insert(websiteCatalog);
                                });

                    });
                    ID<String> internalId = websiteService.getInternalId(ID.EXTERNAL_ID(website.getWebsiteId()));
                    //Getting websiteCatalogs
                    Page<Map<String, Object>> websiteCatalog =  websiteService.findAllWebsiteCatalogs(internalId, "catalogName","test", PageRequest.of(0, catalogsData.size(), null),false);
                    Assert.assertEquals(websiteCatalog.getSize(),catalogsData.size());
                });
    }

    @Test
    public void getAllWebsiteCatalogsTest() throws Exception {
        //creating websites
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

        websiteService.get(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()),false)
                .ifPresent(website -> {
                    //creating catalogs
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

                        //creating websiteCatalogs
                        catalogService.get(ID.EXTERNAL_ID((String)catalogData.get("externalId")),false)
                                .ifPresent(catalog -> {
                                    WebsiteCatalog websiteCatalog = new WebsiteCatalog();
                                    websiteCatalog.setWebsiteId(website.getId());
                                    websiteCatalog.setCatalogId(catalog.getId());
                                    websiteCatalog.setActive(catalog.getActive());
                                    websiteCatalog.setSequenceNum(0);
                                    websiteCatalog.setSubSequenceNum(0);
                                    websiteCatalogDAO.insert(websiteCatalog);
                                });
                    });

                    //Getting websiteCatalogs
                    List<WebsiteCatalog> websiteCatalogList = websiteService.getAllWebsiteCatalogs(website.getId());
                    Assert.assertTrue(ValidationUtil.isNotEmpty(websiteCatalogList));
                    Assert.assertEquals(catalogsData.size(), websiteCatalogList.size());
                });
    }

    @Test
    public void findAvailableCatalogsForWebsiteTest() throws Exception {
        //creating websites
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

        websiteService.get(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()),false)
                .ifPresent(website -> {
                    //creating catalogs
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

                    //Getting available catalogs
                    Page<Catalog> catalogPage = websiteService.findAvailableCatalogsForWebsite(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()),"catalogName","Test", PageRequest.of(0, catalogsData.size()),false);
                    Assert.assertEquals(catalogPage.getContent().size(), catalogsData.size());

                    //creating websiteCatalogs
                    catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),false)
                            .ifPresent(catalog -> {
                                WebsiteCatalog websiteCatalog = new WebsiteCatalog();
                                websiteCatalog.setWebsiteId(website.getId());
                                websiteCatalog.setCatalogId(catalog.getId());
                                websiteCatalog.setActive(catalog.getActive());
                                websiteCatalog.setSequenceNum(0);
                                websiteCatalog.setSubSequenceNum(0);
                                websiteCatalogDAO.insert(websiteCatalog);
                            });

                    ID<String> internalId = websiteService.getInternalId(ID.EXTERNAL_ID(website.getWebsiteId()));
                    //Getting available catalogs
                    Page<Catalog> availableCatalogPage = websiteService.findAvailableCatalogsForWebsite(internalId,"catalogName","Test", PageRequest.of(0, catalogsData.size() - 1),false);
                    Assert.assertEquals(availableCatalogPage.getContent().size(), catalogsData.size() - 1);
                });
    }

    @Test
    public void getAvailableCatalogsForWebsiteTest() throws Exception {
        //creating websites
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

        websiteService.get(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()),false)
                .ifPresent(website -> {
                    //creating catalogs
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

                    //Getting available catalogs
                    Page<Catalog> catalogPage = websiteService.getAvailableCatalogsForWebsite(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()), 0, catalogsData.size(), null, false);
                    Assert.assertEquals(catalogPage.getContent().size(), catalogsData.size());

                    //creating websiteCatalogs
                    catalogService.get(ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()),false)
                            .ifPresent(catalog -> {
                                WebsiteCatalog websiteCatalog = new WebsiteCatalog();
                                websiteCatalog.setWebsiteId(website.getId());
                                websiteCatalog.setCatalogId(catalog.getId());
                                websiteCatalog.setActive(catalog.getActive());
                                websiteCatalog.setSequenceNum(0);
                                websiteCatalog.setSubSequenceNum(0);
                                websiteCatalogDAO.insert(websiteCatalog);
                            });

                    ID<String> internalId = websiteService.getInternalId(ID.EXTERNAL_ID(website.getWebsiteId()));
                    //Getting available catalogs
                    Page<Catalog> availableCatalogPage = websiteService.getAvailableCatalogsForWebsite(internalId, 0, catalogsData.size(), null, false);
                    Assert.assertEquals(availableCatalogPage.getContent().size(), catalogsData.size() - 1);
                });
    }

    @Test
    public void getWebsiteCatalogsTest() throws Exception {
        //creating websites
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

        websiteService.get(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()),false)
                .ifPresent(website -> {
                    //creating catalogs
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

                        //creating websiteCatalogs
                        catalogService.get(ID.EXTERNAL_ID((String)catalogData.get("externalId")),false)
                                .ifPresent(catalog -> {
                                    WebsiteCatalog websiteCatalog = new WebsiteCatalog();
                                    websiteCatalog.setWebsiteId(website.getId());
                                    websiteCatalog.setCatalogId(catalog.getId());
                                    websiteCatalog.setActive(catalog.getActive());
                                    websiteCatalog.setSequenceNum(0);
                                    websiteCatalog.setSubSequenceNum(0);
                                    websiteCatalogDAO.insert(websiteCatalog);
                                });
                    });

                    ID<String> internalId = websiteService.getInternalId(ID.EXTERNAL_ID(website.getWebsiteId()));
                    //Getting websiteCatalogs
                    Page<Map<String, Object>> websiteCatalogMap =  websiteService.getWebsiteCatalogs(internalId, PageRequest.of(0, catalogsData.size(), null), false);// TODO pagination check
                    Assert.assertEquals(websiteCatalogMap.getSize(), catalogsData.size());
                });
    }

    @Test
    public void addCatalogTest() throws Exception {
        //creating websites
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

        //creating catalogs
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

        //Adding websiteCatalogs
        WebsiteCatalog websiteCatalog = websiteService.addCatalog(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()), ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()));
        websiteCatalogDAO.findById(websiteCatalog.getId())
                .ifPresent(websiteCatalog1 ->
                    Assert.assertEquals(websiteCatalog.getCatalogId(), websiteCatalog1.getCatalogId())
                );
    }

    @Test
    public void getWebsiteByNameTest() throws Exception {
        //creating websites
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

        //Getting website by name
        Optional<Website> website = websiteService.getWebsiteByName(websitesData.get(0).get("name").toString());
        Assert.assertTrue(website.isPresent());
        websiteService.getWebsiteByName(websitesData.get(0).get("name").toString())
                .ifPresent(website1 ->
                        Assert.assertEquals(website1.getWebsiteName(), websitesData.get(0).get("name").toString())
                );
    }

    @Test
    public void getWebsiteByUrlTest() throws Exception {
        //creating websites
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

        //Getting website by url
        Optional<Website> website = websiteService.getWebsiteByUrl(websitesData.get(0).get("url").toString());
        Assert.assertTrue(website.isPresent());
        websiteService.getWebsiteByUrl(websitesData.get(0).get("url").toString())
                .ifPresent(website1 ->
                        Assert.assertEquals(website1.getUrl(), websitesData.get(0).get("url").toString())
                );
    }

    @Test
    public void createEntityTest() {
        //creating websites
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

            //Getting website by websiteId
            websiteService.get(ID.EXTERNAL_ID(websiteDTO.getWebsiteId()), false)
                    .ifPresent(website -> {
                        Assert.assertTrue(ValidationUtil.isNotEmpty(website));
                        Assert.assertTrue(website.diff(websiteDTO).isEmpty());
                    });
        });
    }

    @Test
    public void createEntitiesTest(){
        //creating websites
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
        //Getting websites
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size()), false).getTotalElements(), websitesData.size());

    }

    @Test
    public void toggleTest() {
        //creating websites
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

        //Getting website
        Website websiteDetails = websiteService.get(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(websiteDetails != null);

        //toggle
        websiteService.toggle(ID.EXTERNAL_ID(websiteDetails.getWebsiteId()), Toggle.get(websiteDetails.getActive()));
        Website updatedWebsite = websiteService.get(ID.EXTERNAL_ID(websiteDetails.getWebsiteId()), false).orElse(null);
        Assert.assertTrue(updatedWebsite != null);
        Map<String, Object> diff = websiteDetails.diff(updatedWebsite);
        Assert.assertEquals(diff.size(), 1);
        Assert.assertEquals(diff.get("active"), "N");

        websiteService.toggle(ID.EXTERNAL_ID(websiteDetails.getWebsiteId()), Toggle.get(updatedWebsite.getActive()));
        Website updatedWebsite1 = websiteService.get(ID.EXTERNAL_ID(websiteDetails.getWebsiteId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedWebsite1));
        Assert.assertEquals(updatedWebsite1.getActive(), "Y");
    }

    @Test
    public void getTest() {
        //creating websites
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

            //Getting website
            Website websiteDetails = websiteService.get(ID.EXTERNAL_ID(websiteDTO.getWebsiteId()), false).orElse(null);
            Assert.assertTrue(websiteDetails != null);
            Map<String, Object> diff = websiteDTO.diff(websiteDetails);
            Assert.assertEquals(diff.size(), 0);
        });
    }

    @Test
    public void getAllAsPageTest() {
        //creating websites
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

        //Getting websites as Page
        Page<Website> paginatedResult = websiteService.getAll(0, 10, null,false);
        Assert.assertEquals(paginatedResult.getContent().size(), websitesData.size());
    }

    @Test
    public void getAllAsListTest() {
        //creating websites
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

        // sorting : ascending
        List<Website> result = websiteService.getAll(Sort.by("websiteName").ascending(), false);
        String[] actual = result.stream().map(website -> website.getWebsiteName()).collect(Collectors.toList()).toArray(new String[0]);
        String[] expected = websitesData.stream().map(websiteData -> (String)websiteData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertArrayEquals(expected, actual);

		mongoTemplate.dropCollection(Website.class);


        //creating website
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

        // sorting : Descending
        result = websiteService.getAll(Sort.by("websiteName").descending(), false);
        actual = result.stream().map(website -> website.getWebsiteName()).collect(Collectors.toList()).toArray(new String[0]);
        expected = websitesData.stream().map(websiteData -> (String)websiteData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertNotEquals(expected, actual);
    }

    @Test
    public void getAllWithIdsAsPageTest() {
        //creating websites
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
        //Getting websites by ids
        Page<Website> paginatedResult = websiteService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), 0, 10, null, false);
        Map<String, Website> websitesMap = paginatedResult.getContent().stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == ids.length && websitesMap.containsKey(ids[0]) && websitesMap.containsKey(ids[1]) && websitesMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithIdsAsListTest() {
        //creating websites
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
        //Getting websites by ids
        List<Website> paginatedResult = websiteService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, Website> websitesMap = paginatedResult.stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == ids.length && websitesMap.containsKey(ids[0]) && websitesMap.containsKey(ids[1]) && websitesMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithExclusionsAsPageTest() {
        //creating websites
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
        //Getting websites with exclude Ids
        Page<Website> paginatedResult = websiteService.getAllWithExclusions(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), 0, 10, null, false);
        Map<String, Website> websitesMap = paginatedResult.getContent().stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == (websitesData.size() - ids.length) && !websitesMap.containsKey(ids[0]) && !websitesMap.containsKey(ids[1]) && !websitesMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithExclusionsAsListTest() {
        //creating websites
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
        //Getting websites with exclude Ids
        List<Website> paginatedResult = websiteService.getAllWithExclusions(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, Website> websitesMap = paginatedResult.stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == (websitesData.size() - ids.length) && !websitesMap.containsKey(ids[0]) && !websitesMap.containsKey(ids[1]) && !websitesMap.containsKey(ids[2]));
    }

    @Test
    public void findAllAtSearchTest() {
        //creating websites
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
        //Getting websites by searchField
        long size = websitesData.stream().filter(x -> x.get("active").equals("Y")).count();
        Page<Website> paginatedResult = websiteService.findAll("name", "Test", PageRequest.of(0, websitesData.size()), true);
        Assert.assertEquals(paginatedResult.getContent().size(), size);
    }

    @Test
    public void findAllTest() {
        //creating websites
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

        //Getting websites
        long size = websitesData.stream().filter(x -> x.get("active").equals("Y")).count();
        Page<Website> paginatedResult = websiteService.findAll(PageRequest.of(0, websitesData.size()), true);
        Assert.assertEquals(paginatedResult.getContent().size(), size);
    }

    @Test
    public void updateEntityTest() {
        //creating websites
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));

        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);

            Website websiteDetails = websiteService.get(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(websiteDetails));

           //updating website
            websiteDetails.setUrl("https://www.test11.com");
            websiteDetails.setGroup("DETAILS");

            websiteService.update(ID.EXTERNAL_ID(websiteDetails.getWebsiteId()), websiteDetails);
            //Getting updated website
            Website updatedWebsite = websiteService.get(ID.EXTERNAL_ID(websiteDetails.getWebsiteId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(updatedWebsite));
            Map<String, Object> diff = websiteDTO.diff(updatedWebsite);
            Assert.assertEquals(diff.size(), 1);
            Assert.assertEquals(diff.get("url"), "https://www.test11.com");
        });

    }

    @Test
    public void updateEntitiesTest(){
        //creating websites
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

        List<Website> result = websiteService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, Website> websitesMap = result.stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == ids.length && websitesMap.containsKey(ids[0]) && websitesMap.containsKey(ids[1]) && websitesMap.containsKey(ids[2]));

        //updating websites
        List<Website> websites = result.stream().map(result1 -> {
            result1.setActive("N");
            return result1;
        }).collect(Collectors.toList());
        websiteService.update(websites);

        //Getting updated websites
        result = websiteService.getAll(Sort.by("websiteName").descending(), true);
        websitesMap = result.stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == (websitesData.size() - ids.length) && !websitesMap.containsKey(ids[0]) && !websitesMap.containsKey(ids[1]) && !websitesMap.containsKey(ids[2]));
        Assert.assertFalse(websitesMap.size() == websitesData.size() && websitesMap.containsKey(ids[0]) && websitesMap.containsKey(ids[1]) && websitesMap.containsKey(ids[2]));
    }

    @Test
    public void cloneInstanceTest() {
        //creating websites
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));

        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);

            //Getting website
            Website newWebsite = websiteService.get(ID.EXTERNAL_ID(websiteDTO.getWebsiteId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newWebsite));
            Assert.assertTrue(newWebsite.diff(websiteDTO).isEmpty());
            //cloning website instance
            Website websiteClone = websiteService.cloneInstance(ID.EXTERNAL_ID(newWebsite.getWebsiteId()), Entity.CloneType.LIGHT);
            Assert.assertTrue(websiteClone.getWebsiteId() .equals(newWebsite.getWebsiteId() + "_COPY") && websiteClone.getWebsiteName().equals(newWebsite.getWebsiteName() + "_COPY") && websiteClone.getUrl().equals(newWebsite.getUrl() + "_COPY") && websiteClone.getActive() != newWebsite.getActive());
        });
    }

    @Test
    public void findAll2Test() {
        //creating websites
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
        //Getting websites
        long size = websitesData.stream().filter(x -> x.get("active").equals("N")).count();
        List<Website> result = websiteService.findAll(CollectionsUtil.toMap("active", "N"), false);
        Assert.assertTrue(result.size() == size);
    }

    @Test
    public void findAll1Test() {
        //creating websites
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
        //Getting websites
        long size = websitesData.stream().filter(x -> x.get("active").equals("N")).count();
        GenericCriteria criteria = PlatformUtil.buildCriteria(CollectionsUtil.toMap("active", "N"));
        List<Website> result = websiteService.findAll(criteria, false);
        Assert.assertTrue(result.size() == size);
    }

    @Test
    public void findOneTest() {
        //creating websites
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
        //Getting website
        Optional<Website> result = websiteService.findOne(CollectionsUtil.toMap("websiteName", websitesData.get(0).get("name")));
        Assert.assertEquals(websitesData.get(0).get("name"), result.get().getWebsiteName());
    }

    @Test
    public void findOne1Test() {
        //creating websites
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

        //Getting website
        GenericCriteria criteria = PlatformUtil.buildCriteria(CollectionsUtil.toMap("websiteName", websitesData.get(0).get("name")));
        Optional<Website> result = websiteService.findOne(criteria);
        Assert.assertEquals(websitesData.get(0).get("name"), result.get().getWebsiteName());
    }


    @Test
    public void validateTest() throws Exception {
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
        Website website = websiteDAO.findById(ID.EXTERNAL_ID(websiteDTO.getWebsiteId())).orElse(null);
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
		mongoTemplate.dropCollection(Website.class);
		mongoTemplate.dropCollection(Catalog.class);
        websiteCatalogDAO.deleteAll();
    }

}