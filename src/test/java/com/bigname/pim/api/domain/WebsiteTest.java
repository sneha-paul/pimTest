package com.bigname.pim.api.domain;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.Entity;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.WebsiteService;
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
import java.util.List;
import java.util.Map;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static org.junit.Assert.*;

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
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteId("test");
        websiteDTO.setWebsiteName("Test.com");
        websiteDTO.setUrl("www.test.com");

        websiteDTO.orchestrate();

        Assert.assertEquals(websiteDTO.getWebsiteId(), "TEST");
        Assert.assertEquals(websiteDTO.getWebsiteName(), "Test.com");
        Assert.assertEquals(websiteDTO.getUrl(), "www.test.com");
        Assert.assertEquals(websiteDTO.getActive(), "N");

        websiteService.create(websiteDTO);
        Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newWebsite));
        Assert.assertEquals(newWebsite.getWebsiteId(), websiteDTO.getWebsiteId());
        Assert.assertEquals(newWebsite.getWebsiteName(), websiteDTO.getWebsiteName());
        Assert.assertEquals(newWebsite.getUrl(), websiteDTO.getUrl());
        Assert.assertEquals(newWebsite.getActive(), websiteDTO.getActive());
    }

    @Test
    public void cloneInstance() throws Exception {
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
            Assert.assertTrue(newWebsite != null);
            Assert.assertTrue(newWebsite.diff(websiteDTO).isEmpty());

            Website websiteClone = websiteService.cloneInstance(newWebsite.getWebsiteId(), EXTERNAL_ID, Entity.CloneType.LIGHT);
            Assert.assertTrue(websiteClone.getWebsiteId() .equals(newWebsite.getWebsiteId() + "_COPY") && websiteClone.getWebsiteName().equals(newWebsite.getWebsiteName() + "_COPY") && websiteClone.getUrl().equals(newWebsite.getUrl() + "_COPY") && websiteClone.getActive() != newWebsite.getActive());
        });

        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

    @Test
    public void merge() throws Exception {
        Website original = new Website();
        original.setWebsiteName("One");
        original.setWebsiteId("ONE");
        original.setUrl("www.one.com");

        Website modified = new Website();
//        modified.setGroup("DETAILS");
        modified.setWebsiteName("One-A");

        original = original.merge(modified);

        Assert.assertEquals(original.getWebsiteName(), "One");
    }

    @Test
    public void toMap() throws Exception {
    }

    @Test
    public void equals() throws Exception {
    }

    @Test
    public void orchestrate() throws Exception {
    }

    @Test
    public void diff() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }
}
