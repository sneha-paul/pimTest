package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Website;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Before
    public void setUp() {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

    @Test
    public void createWebsiteTest() {
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteName("Test1.com");
        websiteDTO.setWebsiteId("TEST1");
        websiteDTO.setActive("Y");
        websiteDTO.setUrl("https://www.test1.com");
        Website website = websiteDAO.insert(websiteDTO);
        Assert.assertFalse(website.diff(websiteDTO).isEmpty());

    }

    @Test
    public void retrieveWebsiteTest() {
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteName("Test1.com");
        websiteDTO.setWebsiteId("TEST1");
        websiteDTO.setActive("Y");
        websiteDTO.setUrl("https://www.test1.com");
        websiteDAO.insert(websiteDTO);
        Optional<Website> website = websiteDAO.findByExternalId(websiteDTO.getWebsiteId());
        Assert.assertTrue(website.isPresent());
        website = websiteDAO.findById(websiteDTO.getWebsiteId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(website.isPresent());
        website = websiteDAO.findById(websiteDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(website.isPresent());

    }

    @Test
    public void retrieveWebsitesTest() {
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

        websiteDAO.insert(websiteDTOs);

        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size()), false).getTotalElements(), websiteDTOs.size());
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size() - 1), false).getTotalElements(), websiteDTOs.size());
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size() - 1), false).getContent().size(), websiteDTOs.size() - 1);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(1, 1), false).getContent().size(), 1);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(1, websiteDTOs.size() - 1), false).getContent().size(), 1);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size() - 1), false).getTotalPages(), 2);

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
        websiteDTOs = websitesData.stream().map(websiteData -> {
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
            return websiteDTO;
        }).collect(Collectors.toList());

        websiteDAO.insert(websiteDTOs);

//        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size())).getTotalElements(), activeCount[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size()), true).getTotalElements(), activeCount[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size()), false, true).getTotalElements(), inactiveCount[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size()), false).getTotalElements(), activeCount[0] + inactiveCount[0]);
        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size()), false, false).getTotalElements(), 0);


    }

    @After
    public void tearDown() {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }



}