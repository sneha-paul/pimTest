package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static org.junit.Assert.*;

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
    WebsiteCatalogDAO websiteCatalogDAO;
    @Autowired
    CatalogDAO catalogDAO;
    @Autowired
    WebsiteDAO websiteDAO;
    @Before
    public void setUp() throws Exception {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }
    @Test
    public void accessorsTest() {
        //TODO
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
    }
}