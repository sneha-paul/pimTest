package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.WebsiteService;
import com.m7.common.util.CollectionsUtil;
import com.m7.common.util.ValidationUtil;
import com.m7.xcore.domain.Entity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.m7.xcore.util.FindBy.EXTERNAL_ID;


/**
 * Created by sanoop on 05/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class WebsiteTest {

    @Autowired
    WebsiteService websiteService;
    @Autowired
    WebsiteDAO websiteDAO;

    @Before
    public void setUp() throws Exception {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }
    @Test
    public void accessorsTest(){
        //Create new instance
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteId("test");
        websiteDTO.setWebsiteName("Test.com");
        websiteDTO.setUrl("www.test.com");
        websiteDTO.setExternalId("test");

        websiteDTO.orchestrate();

        //Testing equals unique id
        Assert.assertEquals(websiteDTO.getWebsiteId(), "TEST");
        Assert.assertEquals(websiteDTO.getWebsiteName(), "Test.com");
        Assert.assertEquals(websiteDTO.getUrl(), "www.test.com");
        Assert.assertEquals(websiteDTO.getExternalId(), "TEST");
        Assert.assertEquals(websiteDTO.getActive(), "N");

        //create
        websiteService.create(websiteDTO);
        Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newWebsite));
        Assert.assertEquals(newWebsite.getWebsiteId(), websiteDTO.getWebsiteId());
        Assert.assertEquals(newWebsite.getWebsiteName(), websiteDTO.getWebsiteName());
        Assert.assertEquals(newWebsite.getUrl(), websiteDTO.getUrl());
        Assert.assertEquals(newWebsite.getExternalId(), websiteDTO.getExternalId());
        Assert.assertEquals(newWebsite.getActive(), websiteDTO.getActive());
    }

    @Test
    public void cloneInstance() throws Exception {
        //Adding website
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));

        websitesData.forEach(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);

            //Clone website
            Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(newWebsite != null);
            Assert.assertTrue(newWebsite.diff(websiteDTO).isEmpty());

            Website websiteClone = websiteService.cloneInstance(newWebsite.getWebsiteId(), EXTERNAL_ID, Entity.CloneType.LIGHT);
            Assert.assertTrue(websiteClone.getWebsiteId() .equals(newWebsite.getWebsiteId() + "_COPY") && websiteClone.getWebsiteName().equals(newWebsite.getWebsiteName() + "_COPY") && websiteClone.getUrl().equals(newWebsite.getUrl() + "_COPY") && websiteClone.getActive() != newWebsite.getActive());
        });
    }

    @Test
    public void merge() throws Exception {
        //Create Website Original
        Website original = new Website();
        original.setWebsiteName("One");
        original.setWebsiteId("ONE");
        original.setExternalId("ONE");
        original.setUrl("www.one.com");

        //Add Details
        Website modified = new Website();
        modified.setGroup("DETAILS");
        modified.setWebsiteName("One-A");
        modified.setWebsiteId("ONE-A");
        modified.setExternalId("ONE-A");
        modified.setUrl("www.one.com");

        original = original.merge(modified);
        Assert.assertEquals(original.getWebsiteName(), "One-A");
        Assert.assertEquals(original.getWebsiteId(), "ONE-A");
        Assert.assertEquals(original.getExternalId(), "ONE-A");
        Assert.assertEquals(original.getUrl(), "www.one.com");

        //Without Details
        Website modified1 = new Website();
        modified1.setWebsiteName("One");
        modified1.setWebsiteId("ONE");
        modified1.setExternalId("ONE");
        modified1.setUrl("www.one.com");

        original = original.merge(modified1);
        Assert.assertEquals(original.getWebsiteName(), "One-A");
        Assert.assertEquals(original.getWebsiteId(), "ONE-A");
        Assert.assertEquals(original.getExternalId(), "ONE-A");
        Assert.assertEquals(original.getUrl(), "www.one.com");
    }

    @Test
    public void toMap() throws Exception {
        //Create new Instance
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteName("Test.com");
        websiteDTO.setUrl("www.test.com");
        websiteDTO.setExternalId("TEST");

        Map<String, String> map = new HashMap<>();
        map.put("websiteName", "Test.com");
        map.put("externalId", "TEST");
        map.put("url", "www.test.com");

        Map<String, String> map1 = websiteDTO.toMap();
        Assert.assertEquals(map1.get("websiteName"), map.get("websiteName"));
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));
        Assert.assertEquals(map1.get("url"), map.get("url"));
    }

    @Test
    public void equals() throws Exception {
    }

    @Test
    public void orchestrate() throws Exception {
        //Create id
        Website websiteDTO = new Website();
        websiteDTO.setExternalId("test");
        websiteDTO.orchestrate();

        //Check websiteId
        Assert.assertTrue(ValidationUtil.isNotEmpty(websiteDTO.getWebsiteId()));
        Assert.assertEquals(websiteDTO.getWebsiteId(), "TEST");
    }
    @Test
    public void diff() throws Exception {
        //Create first instance
        Website website1 = new Website();
        website1.setWebsiteId("test");
        website1.setWebsiteName("test.com");
        website1.setUrl("www.test");
        website1.setActive("N");

        //Create Second instance
        Website website2 = new Website();
        website2.setWebsiteId("test");
        website2.setWebsiteName("test.com2");
        website2.setUrl("www.test");
        website2.setActive("N");

        //Checking First instance and Second instance
        Map<String, Object> diff = website1.diff(website2);
        Assert.assertEquals(diff.size(), 2);
        Assert.assertEquals(diff.get("websiteName"), "test.com2");

        //Checking First instance and Second instance
        Map<String, Object> diff1 = website1.diff(website2,true);
        Assert.assertEquals(diff1.size(), 1);
        Assert.assertEquals(diff1.get("websiteName"), "test.com2");
    }
    @After
    public void tearDown() throws Exception {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }
}
