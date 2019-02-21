package com.bigname.core.service;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.WebsiteService;

import com.bigname.pim.util.PIMConstants;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static com.bigname.core.util.FindBy.INTERNAL_ID;

/**
 * Created by sruthi on 20-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class BaseServiceSupportTest {

    @Autowired
    private WebsiteDAO websiteDAO;

    @Autowired
    private WebsiteService websiteService;

    @Before
    public void setUp() {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

    @Test
    public void createEntityTest() {
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteName("Test1.com");
        websiteDTO.setWebsiteId("TEST1");
        websiteDTO.setActive("Y");
        websiteDTO.setUrl("https://www.test1.com");

        websiteService.create(websiteDTO);

        Website newWebsite = websiteService.get(websiteDTO.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(newWebsite != null);
        Assert.assertTrue(newWebsite.diff(websiteDTO).isEmpty());

        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

    @Test
    public void updateEntityTest() {
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteName("Test1.com");
        websiteDTO.setWebsiteId("TEST1");
        websiteDTO.setActive("Y");
        websiteDTO.setUrl("https://www.test1.com");
        websiteDAO.insert(websiteDTO);

        Website websiteDetails = websiteService.get("TEST1", EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(websiteDetails != null);
        websiteDetails.setUrl("https://www.test11.com");
        websiteDetails.setGroup("DETAILS");

        websiteService.update(websiteDetails.getWebsiteId(), EXTERNAL_ID, websiteDetails);

        Website updatedWebsite = websiteService.get(websiteDetails.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(updatedWebsite != null);
        Map<String, Object> diff = websiteDTO.diff(updatedWebsite);
        Assert.assertEquals(diff.size(), 1);
        Assert.assertEquals(diff.get("url"), "https://www.test11.com");

        websiteDAO.getMongoTemplate().dropCollection(Website.class);

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

        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size()), false).getTotalElements(), websiteDTOs.size());

        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

    /*@Test
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

        List<Website> websiteDTOs = websitesData.stream().map(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            return websiteDTO;
        }).collect(Collectors.toList());

        websiteDAO.insert(websiteDTOs);


    }*/

    @Test
    public void toggleTest() {
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteName("Test1.com");
        websiteDTO.setWebsiteId("TEST1");
        websiteDTO.setActive("Y");
        websiteDTO.setUrl("https://www.test1.com");
        websiteDAO.insert(websiteDTO);

        Website websiteDetails = websiteService.get("TEST1", EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(websiteDetails != null);
        websiteService.toggle(websiteDetails.getWebsiteId(), EXTERNAL_ID, Toggle.get(websiteDetails.getActive()));

        Website updatedWebsite = websiteService.get(websiteDetails.getWebsiteId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(updatedWebsite != null);
        Map<String, Object> diff = websiteDTO.diff(updatedWebsite);
        Assert.assertEquals(diff.size(), 1);
        Assert.assertEquals(diff.get("active"), "N");

        websiteDAO.getMongoTemplate().dropCollection(Website.class);

    }

    @Test
    public void getTest() {
        Website websiteDTO = new Website();
        websiteDTO.setWebsiteName("Test1.com");
        websiteDTO.setWebsiteId("TEST1");
        websiteDTO.setActive("Y");
        websiteDTO.setUrl("https://www.test1.com");
        websiteDAO.insert(websiteDTO);

        Website websiteDetails = websiteService.get("TEST1", EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(websiteDetails != null);
        Map<String, Object> diff = websiteDTO.diff(websiteDetails);
        Assert.assertEquals(diff.size(), 0);

        websiteDAO.getMongoTemplate().dropCollection(Website.class);

        websiteDTO = new Website();
        websiteDTO.setWebsiteName("Test2.com");
        websiteDTO.setWebsiteId("TEST2");
        websiteDTO.setActive("Y");
        websiteDTO.setUrl("https://www.test2.com");
        websiteDAO.insert(websiteDTO);

        websiteDetails = websiteService.get(websiteDTO.getId(), INTERNAL_ID, false).orElse(null);
        Assert.assertTrue(websiteDetails != null);
        diff = websiteDTO.diff(websiteDetails);
        Assert.assertEquals(diff.size(), 0);

        websiteDAO.getMongoTemplate().dropCollection(Website.class);

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

        List<Website> websiteDTOs = websitesData.stream().map(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            return websiteDTO;
        }).collect(Collectors.toList());

        websiteDAO.insert(websiteDTOs);

        Page<Website> paginatedResult = websiteService.findAll("name", "Test", PageRequest.of(0, websiteDTOs.size()), false);
        Assert.assertEquals(paginatedResult.getContent().size(), 9);

        websiteDAO.getMongoTemplate().dropCollection(Website.class);
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

        List<Website> websiteDTOs = websitesData.stream().map(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            return websiteDTO;
        }).collect(Collectors.toList());

        websiteDAO.insert(websiteDTOs);

        Page<Website> paginatedResult = websiteService.getAll(0, 10, null);
        Assert.assertEquals(paginatedResult.getContent().size(), websiteDTOs.size());

        websiteDAO.getMongoTemplate().dropCollection(Website.class);

    }

    //TODO : need to check the Assert.assert...
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

        List<Website> websiteDTOs = websitesData.stream().map(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            return websiteDTO;
        }).collect(Collectors.toList());

        websiteDAO.insert(websiteDTOs);

        List<Website> result = websiteService.getAll(Sort.by("name").ascending(), false);
        Assert.assertArrayEquals(result.toArray(), websiteDTOs.toArray());

        websiteDAO.getMongoTemplate().dropCollection(Website.class);

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


        websiteDTOs = websitesData.stream().map(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            return websiteDTO;
        }).collect(Collectors.toList());

        websiteDAO.insert(websiteDTOs);

        result = websiteService.getAll(Sort.by("name").ascending(), false);
        Assert.assertArrayEquals(result.toArray(), websiteDTOs.toArray());
       // Assert.assertSame(result,websiteDTOs);

        websiteDAO.getMongoTemplate().dropCollection(Website.class);

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

        List<Website> websiteDTOs = websitesData.stream().map(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            return websiteDTO;
        }).collect(Collectors.toList());

        websiteDAO.insert(websiteDTOs);

        String[] ids = {"TEST_1", "TEST_2", "TEST_3"};

        Page<Website> paginatedResult = websiteService.getAll(ids, EXTERNAL_ID, 0, 10, null, false);
        Assert.assertEquals(paginatedResult.getContent().size(), 3);

        websiteDAO.getMongoTemplate().dropCollection(Website.class);
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

        List<Website> websiteDTOs = websitesData.stream().map(websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String)websiteData.get("name"));
            websiteDTO.setWebsiteId((String)websiteData.get("externalId"));
            websiteDTO.setActive((String)websiteData.get("active"));
            websiteDTO.setUrl((String)websiteData.get("url"));
            return websiteDTO;
        }).collect(Collectors.toList());

        websiteDAO.insert(websiteDTOs);

        String[] ids = {"TEST_1", "TEST_2", "TEST_3"};

        List<Website> paginatedResult = websiteService.getAll(ids, EXTERNAL_ID, null, false);
        Assert.assertEquals(paginatedResult.size(), 3);

        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }


    @After
    public void tearDown() {
        websiteDAO.getMongoTemplate().dropCollection(Website.class);
    }

}
