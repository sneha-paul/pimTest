package com.bigname.pim.api.domain;

import com.bigname.common.util.CollectionsUtil;
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

    @After
    public void tearDown() throws Exception {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

    @Test
    public void getWebsiteId() throws Exception {
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteName("test");
        websiteDTO.setUrl("www.test");
        websiteDTO.setWebsiteId("TEST");
        websiteDTO.setActive("Y");

        websiteService.create(websiteDTO);
        Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newWebsite != null);
        Assert.assertEquals(newWebsite.getWebsiteId(), websiteDTO.getWebsiteId());
    }

        @Test
        public void setWebsiteId () throws Exception {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName("test");
            websiteDTO.setWebsiteId("TEST");
            websiteDTO.setUrl("www.test");
            websiteDTO.setActive("Y");

            websiteService.create(websiteDTO);
            Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(newWebsite != null);
            Assert.assertEquals(newWebsite.getWebsiteId(),websiteDTO.getWebsiteId());
        }

        @Test
        public void getWebsiteName () throws Exception {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName("test");
            websiteDTO.setWebsiteId("TEST");
            websiteDTO.setUrl("www.test");
            websiteDTO.setActive("Y");

            websiteService.create(websiteDTO);
            Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(newWebsite != null);
            Assert.assertEquals(newWebsite.getWebsiteName(),websiteDTO.getWebsiteName());
        }

        @Test
        public void setWebsiteName () throws Exception {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName("test");
            websiteDTO.setWebsiteId("TEST");
            websiteDTO.setUrl("www.test");
            websiteDTO.setActive("Y");

            websiteService.create(websiteDTO);
            Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(newWebsite != null);
            Assert.assertEquals(newWebsite.getWebsiteName(),websiteDTO.getWebsiteName());
        }

        @Test
        public void getUrl () throws Exception {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName("test");
            websiteDTO.setWebsiteId("TEST");
            websiteDTO.setUrl("www.test");
            websiteDTO.setActive("Y");

            websiteService.create(websiteDTO);
            Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(newWebsite != null);
            Assert.assertEquals(newWebsite.getUrl(),websiteDTO.getUrl());
        }

        @Test
        public void setUrl () throws Exception {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName("test");
            websiteDTO.setWebsiteId("TEST");
            websiteDTO.setUrl("www.test");
            websiteDTO.setActive("Y");

            websiteService.create(websiteDTO);
            Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(newWebsite != null);
            Assert.assertEquals(newWebsite.getUrl(),websiteDTO.getUrl());
        }

        @Test
        public void setExternalId () throws Exception {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName("test");
            websiteDTO.setWebsiteId("TEST");
            websiteDTO.setUrl("www.test");
            websiteDTO.setActive("Y");

            websiteService.create(websiteDTO);
            Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(newWebsite != null);
            Assert.assertEquals(newWebsite.getExternalId(),websiteDTO.getExternalId());
        }

        @Test
        public void cloneInstance () throws Exception {

        }


        @Test
        public void merge () throws Exception {
        }

        @Test
        public void toMap () throws Exception {
        }

        @Test
        public void equals () throws Exception {
        }

        @Test
        public void orchestrate () throws Exception {
        }

        @Test
        public void diff () throws Exception {
        }

    }
