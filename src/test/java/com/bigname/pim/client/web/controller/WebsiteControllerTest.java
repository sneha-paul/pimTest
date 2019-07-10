package com.bigname.pim.client.web.controller;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.persistence.dao.mongo.CategoryDAO;
import com.bigname.pim.api.persistence.dao.mongo.WebsiteDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.WebsiteService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.service.UserService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes={PimApplication.class})
public class WebsiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private WebsiteController websiteController;

    @Autowired
    private WebsiteDAO websiteDAO;

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CategoryDAO categoryDAO;

    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        if(!userService.get(ID.EXTERNAL_ID("MANU@BLACWOOD.COM")).isPresent()) {
            User user = new User();
            user.setUserName("MANU@BLACWOOD.COm");
            user.setPassword("temppass");
            user.setEmail("manu@blacwood.com");
            user.setActive("Y");
            userService.create(user);
        }
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) websiteDAO.getTemplate();
        }
        mongoTemplate.dropCollection(Website.class);
        mongoTemplate.dropCollection(Catalog.class);
    }

    @Test
    public void contexLoads() throws Exception {
        Assert.assertNotNull(websiteController);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createTest() throws Exception {

        //Creating website
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("websiteName", ConversionUtil.toList("TestSite.com"));
        params.put("websiteId", ConversionUtil.toList("TEST"));
        params.put("url", ConversionUtil.toList("https://www.testsite.com"));
        ResultActions result = mockMvc.perform(
            post("/pim/websites")
                .params(params)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8));


                result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(3));
        result.andExpect(jsonPath("$.success").value(true));
        result.andExpect(jsonPath("$.group.length()").value(1));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void updateTest() throws Exception {

        //Creating website
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("group", ConversionUtil.toList("CREATE"));
        params.put("websiteName", ConversionUtil.toList("TestSite.com"));
        params.put("websiteId", ConversionUtil.toList("TEST"));
        params.put("url", ConversionUtil.toList("https://www.testsite.com"));
        ResultActions result = mockMvc.perform(
                post("/pim/websites")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());

        //Updating website
        params.put("group", ConversionUtil.toList("DETAILS"));
        params.put("url", ConversionUtil.toList("https://www.testsites.com"));
        ResultActions result1 = mockMvc.perform(
                put("/pim/websites/TEST")
                        .params(params)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.size()").value(3));
        result1.andExpect(jsonPath("$.success").value(true));
        result1.andExpect(jsonPath("$.group.length()").value(1));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void detailsTest() throws Exception {
        //Create mode
        mockMvc.perform(
                get("/pim/websites/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("website/website"))
                .andExpect(forwardedUrl("/website/website.jsp"))
                .andExpect(model().attribute("mode", is("CREATE")))
                .andExpect(model().attribute("active", is("WEBSITES")));

        //Details mode, with non=existing websiteID - TODO
       /* mockMvc.perform(
                get("/pim/websites/TEST"))
                .andExpect(view().name("error"))
                .andExpect(forwardedUrl("/error.jsp"));*/

        //Add a website instance
        List<Website> createdWebsiteInstances = addWebsiteInstances();
        Assert.assertFalse(createdWebsiteInstances.isEmpty());

        //Details mode with valid websiteID
        String websiteId = createdWebsiteInstances.get(0).getWebsiteId();
        mockMvc.perform(
                get("/pim/websites/" + websiteId))
                .andExpect(status().isOk())
                .andExpect(view().name("website/website"))
                .andExpect(forwardedUrl("/website/website.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("WEBSITES")))
                .andExpect(model().attribute("website", hasProperty("externalId", is(websiteId))));

        //Details mode with reload true
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("reload", ConversionUtil.toList("true"));

        mockMvc.perform(
                get("/pim/websites/" + websiteId).params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("website/website_body"))
                .andExpect(forwardedUrl("/website/website_body.jsp"))
                .andExpect(model().attribute("mode", is("DETAILS")))
                .andExpect(model().attribute("active", is("WEBSITES")))
                .andExpect(model().attribute("website", hasProperty("externalId", is(websiteId))));

    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void allTest() throws Exception {
        mockMvc.perform(
                get("/pim/websites"))
                .andExpect(status().isOk())
                .andExpect(view().name("website/websites"))
                .andExpect(forwardedUrl("/website/websites.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void all1Test() throws Exception {

        //Creating websites
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7", "externalId", "TEST_7", "url", "www.test7.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test8", "externalId", "TEST_8", "url", "www.test8.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test9", "externalId", "TEST_9", "url", "www.test9.com", "active", "Y"));

        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        //Getting entries as page
        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("start", ConversionUtil.toList("0"));
        detailsParams.put("length", ConversionUtil.toList("5"));
        detailsParams.put("draw", ConversionUtil.toList("1"));
        ResultActions result = mockMvc.perform(
                get("/pim/websites/data")
                        .params(detailsParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.data.size()").value(5));
        result.andExpect(jsonPath("$.draw").value(1));
        result.andExpect(jsonPath("$.recordsFiltered").value(9));
        result.andExpect(jsonPath("$.recordsTotal").value(9));

        MultiValueMap<String, String> detailsParams1 = new LinkedMultiValueMap<>();
        detailsParams1.put("start", ConversionUtil.toList("5"));
        detailsParams1.put("length", ConversionUtil.toList("5"));
        detailsParams1.put("draw", ConversionUtil.toList("2"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/websites/data")
                        .params(detailsParams1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(4));
        result1.andExpect(jsonPath("$.draw").value(2));
        result1.andExpect(jsonPath("$.recordsFiltered").value(9));
        result1.andExpect(jsonPath("$.recordsTotal").value(9));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getWebsiteCatalogsTest() throws Exception {

        //creating website
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        //Adding websiteCatalogs
        Website website = websiteService.get(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()), false).orElse(null);

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

            Catalog catalog = catalogService.get(ID.EXTERNAL_ID((String) catalogData.get("externalId")), false).orElse(null);
            websiteService.addCatalog(ID.EXTERNAL_ID(website.getExternalId()), ID.EXTERNAL_ID(catalog.getExternalId()));
        });

        //Getting websites data
        MultiValueMap<String, String> detailsParams1 = new LinkedMultiValueMap<>();
        detailsParams1.put("start", ConversionUtil.toList("0"));
        detailsParams1.put("length", ConversionUtil.toList("5"));
        detailsParams1.put("draw", ConversionUtil.toList("1"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/websites/TEST_WEBSITE_1/catalogs/data")
                        .params(detailsParams1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(4));
        result1.andExpect(jsonPath("$.draw").value(1));
        result1.andExpect(jsonPath("$.recordsFiltered").value(4));
        result1.andExpect(jsonPath("$.recordsTotal").value(4));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void availableCatalogsTest() throws Exception {

        //Add a website instance
        List<Website> createdWebsiteInstances = addWebsiteInstances();
        Assert.assertFalse(createdWebsiteInstances.isEmpty());

        //AvailableCatalogs with valid websiteID
        String websiteId = createdWebsiteInstances.get(0).getWebsiteId();
        mockMvc.perform(
                get("/pim/websites/" + websiteId + "/catalogs/available"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog/availableCatalogs"))
                .andExpect(forwardedUrl("/catalog/availableCatalogs.jsp"));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAvailableCatalogsTest() throws Exception {

        //Creating website
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        //Creating Catalogs
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

        //Adding websiteCatalog
        websiteService.addCatalog(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()), ID.EXTERNAL_ID(catalogsData.get(0).get("externalId").toString()));

        //Getting availableCatalogs
        MultiValueMap<String, String> detailsParams1 = new LinkedMultiValueMap<>();
        detailsParams1.put("start", ConversionUtil.toList("0"));
        detailsParams1.put("length", ConversionUtil.toList("4"));
        detailsParams1.put("draw", ConversionUtil.toList("1"));
        ResultActions result1 = mockMvc.perform(
                get("/pim/websites/TEST_WEBSITE_1/catalogs/available/list")
                        .params(detailsParams1)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result1.andExpect(status().isOk());
        result1.andExpect(jsonPath("$.data.size()").value(3));
        result1.andExpect(jsonPath("$.draw").value(1));
        result1.andExpect(jsonPath("$.recordsFiltered").value(3));
        result1.andExpect(jsonPath("$.recordsTotal").value(3));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void addCatalogTest() throws Exception {

        //Creating website
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            websiteService.create(websiteDTO);
        });

        //Creating catalog
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 1", "externalId", "TEST_CATALOG_1", "active", "Y"));
        catalogsData.add(CollectionsUtil.toMap("name", "Test Catalog 2", "externalId", "TEST_CATALOG_2", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogService.create(catalogDTO);
        });

        //Adding websiteCatalog
        Website website = websiteService.get(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()), false).orElse(null);
        Page<Catalog> availableCatalogs = websiteService.getAvailableCatalogsForWebsite(ID.EXTERNAL_ID(website.getExternalId()), 1, 1, null, false);

        MultiValueMap<String, String> detailsParams = new LinkedMultiValueMap<>();
        detailsParams.put("id", ConversionUtil.toList("TEST_WEBSITE_1"));
        detailsParams.put("catalogId", ConversionUtil.toList("TEST_CATALOG_1"));
        ResultActions result = mockMvc.perform(
                post("/pim/websites/TEST_WEBSITE_1/catalogs/TEST_CATALOG_1")
                        .params(detailsParams)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.size()").value(1));
        result.andExpect(jsonPath("$.success").value(true));

        Page<Catalog> availableCatalogs1 = websiteService.getAvailableCatalogsForWebsite(ID.EXTERNAL_ID(website.getExternalId()), 1, 1, null, false);
        Assert.assertEquals(availableCatalogs1.getTotalElements(), availableCatalogs.getTotalElements() - 1);

    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Website.class);
        mongoTemplate.dropCollection(Catalog.class);
    }

    private List<Website> addWebsiteInstances() {
        List<Website> createdWebsiteInstances = new ArrayList<>();
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test Website 1", "externalId", "TEST_WEBSITE_1", "url", "www.testwebsite1.com", "active", "Y"));
        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            createdWebsiteInstances.add(websiteService.create(websiteDTO));
        });
        return createdWebsiteInstances;
    }

}