package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ConversionUtil;
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

    @Before
    public void setUp() {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

    @Test
    public void createWebsiteTest() {
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

        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

    @Test
    public void retrieveWebsiteTest() {
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
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
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

    @After
    public void tearDown() {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

}