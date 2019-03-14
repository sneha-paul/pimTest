package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.domain.WebsiteCatalog;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class WebsiteRepositoryTest {
    @Autowired
    private WebsiteDAO websiteDAO;
    @Autowired
    private CatalogDAO catalogDAO;
    @Autowired
    private WebsiteCatalogDAO websiteCatalogDAO;

    @Before
    public void setUp() {

        websiteDAO.getMongoTemplate().dropCollection(Website.class);
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }

    @Test
    public void createWebsiteTest() {

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

            Website website = websiteDAO.insert(websiteDTO);
            Assert.assertTrue(website.diff(websiteDTO).isEmpty());
        });
    }

    @Test
    public void retrieveWebsiteTest() {

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
            websiteDAO.insert(websiteDTO);

            Optional<Website> website = websiteDAO.findByExternalId(websiteDTO.getWebsiteId());
            Assert.assertTrue(website.isPresent());
            website = websiteDAO.findById(websiteDTO.getWebsiteId(), FindBy.EXTERNAL_ID);
            Assert.assertTrue(website.isPresent());
            website = websiteDAO.findById(websiteDTO.getId(), FindBy.INTERNAL_ID);
            Assert.assertTrue(website.isPresent());
        });
    }

    @Test
    public void updateWebsiteTest() {

        //creating websites
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);

            //updating website
            Website websiteDetails = websiteDAO.findByExternalId(websitesData.get(0).get("externalId").toString()).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(websiteDetails));
            websiteDetails.setUrl("www.newtest1.com");
            websiteDetails.setGroup("DETAILS");
            websiteDAO.save(websiteDetails);

            Website website = websiteDAO.findByExternalId(websiteDetails.getWebsiteId()).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(website));
            Map<String, Object> diff = websiteDTO.diff(website);
            Assert.assertEquals(diff.size(), 1);
            Assert.assertEquals(diff.get("url"), "www.newtest1.com");
        });
    }


    @Test
    public void retrieveWebsitesTest() {

        //creating website
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

        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size()), false).getTotalElements(), websitesData.size());
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size() - 1), false).getTotalElements(), websitesData.size());
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size() - 1), false).getContent().size(), websitesData.size() - 1);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(1, websitesData.size() - 1), false).getContent().size(), 1);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size() - 1), false).getTotalPages(), 2);

        websiteDAO.getMongoTemplate().dropCollection(Website.class);

        websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "N"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "N"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "N"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        int[] activeCount = {0}, inactiveCount = {0};

        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            if("Y".equals(websiteData.get("active"))) {
                activeCount[0] ++;
            } else {
                inactiveCount[0] ++;
            }
            websiteDAO.insert(websiteDTO);
        });

//        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size())).getTotalElements(), activeCount[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size()), true).getTotalElements(), activeCount[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size()), false, true).getTotalElements(), inactiveCount[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size()), false).getTotalElements(), activeCount[0] + inactiveCount[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size()), false, false, true).getTotalElements(), 0);

        websiteDAO.getMongoTemplate().dropCollection(Website.class);

        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime todayEOD = ConversionUtil.getEOD(LocalDate.now());
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime yesterdayEOD = todayEOD.minusDays(1);
        LocalDateTime tomorrow = today.plusDays(1);
        LocalDateTime tomorrowEOD = todayEOD.plusDays(1);

        websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "activeFrom", yesterday, "activeTo", todayEOD));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "activeFrom", null, "activeTo", todayEOD));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "activeFrom", tomorrow));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "N", "activeFrom", null, "activeTo", null));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "activeFrom", yesterday, "activeTo", tomorrowEOD));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "activeFrom", yesterday, "activeTo", null));
        websitesData.add(CollectionsUtil.toMap("name", "Test8.com", "externalId", "TEST_8", "url", "www.test8.com", "activeFrom", null, "activeTo", null));
        websitesData.add(CollectionsUtil.toMap("name", "Test9.com", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y", "activeFrom", null, "activeTo", null));

        int[] activeCount1 = {0}, inactiveCount1 = {0};
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            if("Y".equals(websiteData.get("active"))) {
                activeCount1[0] ++;
            } else {
                inactiveCount1[0] ++;
            }
            websiteDAO.insert(websiteDTO);
        });
//        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size())).getTotalElements(), activeCount[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size()), true).getTotalElements(), activeCount1[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size()), false, true).getTotalElements(), inactiveCount1[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size()), false).getTotalElements(), activeCount1[0] + inactiveCount1[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websitesData.size()), false, false, true).getTotalElements(), 0);
    }

    @Test
    public void getWebsiteCatalogsTest() throws Exception {

        //creating website
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Website website = websiteDAO.findById(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);

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
            catalogDAO.insert(catalogDTO);

            //creating WebsiteCatalogs
            Catalog catalog = catalogDAO.findById((String)catalogData.get("externalId"), FindBy.EXTERNAL_ID).orElse(null);

            WebsiteCatalog websiteCatalog = new WebsiteCatalog();
            websiteCatalog.setWebsiteId(website.getId());
            websiteCatalog.setCatalogId(catalog.getId());
            websiteCatalog.setActive(catalog.getActive());
            websiteCatalog.setSequenceNum(0);
            websiteCatalog.setSubSequenceNum(0);
            websiteCatalogDAO.insert(websiteCatalog);
        });

        //Getting websiteCatalogs
        Page<Map<String, Object>> websiteCatalogMap = websiteDAO.getWebsiteCatalogs(website.getId(),PageRequest.of(0, catalogsData.size(), null));
        Assert.assertEquals(websiteCatalogMap.getSize(), catalogsData.size());
        Assert.assertEquals(websiteDAO.getWebsiteCatalogs(website.getId(),PageRequest.of(0, catalogsData.size(), null)).getTotalElements(), catalogsData.size());
        Assert.assertEquals(websiteDAO.getWebsiteCatalogs(website.getId(),PageRequest.of(0, catalogsData.size()-1, null)).getTotalElements(), catalogsData.size());
        Assert.assertEquals(websiteDAO.getWebsiteCatalogs(website.getId(),PageRequest.of(0, catalogsData.size()-1, null)).getContent().size(), catalogsData.size() - 1);
        Assert.assertEquals(websiteDAO.getWebsiteCatalogs(website.getId(), PageRequest.of(1, 1, null)).getContent().size(), 1);
        Assert.assertEquals(websiteDAO.getWebsiteCatalogs(website.getId(),PageRequest.of(1, catalogsData.size()-1, null)).getContent().size(), 1);
        Assert.assertEquals(websiteDAO.getWebsiteCatalogs(website.getId(),PageRequest.of(0, catalogsData.size()-1, null)).getTotalPages(), 2);
    }

    @Test
    public void findAllWebsiteCatalogsTest() throws Exception {

        //creating website
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Website website = websiteDAO.findById(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);

        //creating catalogs
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 1.com", "externalId", "TEST_CATALOG_1", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 2.com", "externalId", "TEST_CATALOG_2", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDAO.insert(catalogDTO);

            Catalog catalog = catalogDAO.findById((String)catalogData.get("externalId"), FindBy.EXTERNAL_ID).orElse(null);

            //creating WebsiteCatalogs
            WebsiteCatalog websiteCatalog = new WebsiteCatalog();
            websiteCatalog.setWebsiteId(website.getId());
            websiteCatalog.setCatalogId(catalog.getId());
            websiteCatalog.setActive(catalog.getActive());
            websiteCatalog.setSequenceNum(0);
            websiteCatalog.setSubSequenceNum(0);
            websiteCatalogDAO.insert(websiteCatalog);
        });

        //Getting websiteCatalogs with search elements
        Page<Map<String, Object>> websiteCatalog =  websiteDAO.findAllWebsiteCatalogs(website.getId(), "catalogName", "test", PageRequest.of(0, catalogsData.size(), null),false);
        Assert.assertEquals(websiteCatalog.getSize(),catalogsData.size());
        Assert.assertEquals(websiteDAO.findAllWebsiteCatalogs(website.getId(),"catalogName", "test", PageRequest.of(0, catalogsData.size(), null)).getTotalElements(), catalogsData.size());
        Assert.assertEquals(websiteDAO.findAllWebsiteCatalogs(website.getId(),"catalogName", "test", PageRequest.of(0, catalogsData.size()-1, null)).getTotalElements(), catalogsData.size());
        Assert.assertEquals(websiteDAO.findAllWebsiteCatalogs(website.getId(),"catalogName", "test", PageRequest.of(0, catalogsData.size()-1, null)).getContent().size(), catalogsData.size() - 1);
        Assert.assertEquals(websiteDAO.findAllWebsiteCatalogs(website.getId(), "catalogName", "test", PageRequest.of(1, 1, null)).getContent().size(), 1);
        Assert.assertEquals(websiteDAO.findAllWebsiteCatalogs(website.getId(),"catalogName", "test", PageRequest.of(1, catalogsData.size()-1, null)).getContent().size(), 1);
        Assert.assertEquals(websiteDAO.findAllWebsiteCatalogs(website.getId(),"catalogName", "test",PageRequest.of(0, catalogsData.size()-1, null)).getTotalPages(), 2);
    }

    @Test
    public void getAllWebsiteCatalogsTest() throws Exception {

        //creating website
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Website website = websiteDAO.findById(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);

        //creating Catalogs
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
            catalogDAO.insert(catalogDTO);

            //Creating websiteCatalogs
            Catalog catalog = catalogDAO.findById((String)catalogData.get("externalId"), FindBy.EXTERNAL_ID).orElse(null);

            WebsiteCatalog websiteCatalog = new WebsiteCatalog();
            websiteCatalog.setWebsiteId(website.getId());
            websiteCatalog.setCatalogId(catalog.getId());
            websiteCatalog.setActive(catalog.getActive());
            websiteCatalog.setSequenceNum(0);
            websiteCatalog.setSubSequenceNum(0);
            websiteCatalogDAO.insert(websiteCatalog);
        });

        //Getting websiteCatalogs
        List<WebsiteCatalog> websiteCatalogList = websiteDAO.getAllWebsiteCatalogs(website.getId());
        Assert.assertTrue(ValidationUtil.isNotEmpty(websiteCatalogList));
    }

    @Test
    public void findAvailableCatalogsForWebsiteTest() throws Exception {

        //Creating website
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1.com", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Website website = websiteDAO.findById(websitesData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);

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
            catalogDAO.insert(catalogDTO);
        });

        Page<Catalog> catalogPage = websiteDAO.findAvailableCatalogsForWebsite(websitesData.get(0).get("externalId").toString(),"catalogName","Test", PageRequest.of(0, catalogsData.size()),false);
        Assert.assertEquals(catalogPage.getContent().size(), 4);
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(),"catalogName", "test", PageRequest.of(0, catalogsData.size()), false).getTotalElements(), catalogsData.size());
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(),"catalogName", "test", PageRequest.of(0, catalogsData.size()-1), false).getTotalElements(), catalogsData.size());
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(),"catalogName", "test", PageRequest.of(0, catalogsData.size()-1), false).getContent().size(), catalogsData.size()-1 );
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(), "catalogName", "test", PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(),"catalogName", "test", PageRequest.of(1, catalogsData.size()-1), false).getContent().size(), 1);
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(),"catalogName", "test",PageRequest.of(0, catalogsData.size()-1), false).getTotalPages(), 2);


        //Creating websiteCatalogs
        Catalog catalog = catalogDAO.findById(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID).orElse(null);
        WebsiteCatalog websiteCatalog = new WebsiteCatalog();
        websiteCatalog.setWebsiteId(website.getId());
        websiteCatalog.setCatalogId(catalog.getId());
        websiteCatalog.setActive(catalog.getActive());
        websiteCatalog.setSequenceNum(0);
        websiteCatalog.setSubSequenceNum(0);
        websiteCatalogDAO.insert(websiteCatalog);

        //Getting available catalogs
        Page<Catalog> availableCatalogPage = websiteDAO.findAvailableCatalogsForWebsite(website.getId(),"catalogName","Test", PageRequest.of(0, catalogsData.size() - 1),false);
        Assert.assertEquals(availableCatalogPage.getContent().size(), 3);
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(),"catalogName", "test", PageRequest.of(0, catalogsData.size()), false).getTotalElements(), catalogsData.size()-1);
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(),"catalogName", "test", PageRequest.of(0, catalogsData.size()-1), false).getTotalElements(), catalogsData.size()-1);
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(),"catalogName", "test", PageRequest.of(0, catalogsData.size()-1), false).getContent().size(), catalogsData.size() - 1);
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(), "catalogName", "test", PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(),"catalogName", "test", PageRequest.of(1, catalogsData.size()-1), false).getContent().size(), 0);
        Assert.assertEquals(websiteDAO.findAvailableCatalogsForWebsite(website.getId(),"catalogName", "test",PageRequest.of(0, catalogsData.size()-1), false).getTotalPages(), 1);

    }

    @After
    public void tearDown() {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }

}