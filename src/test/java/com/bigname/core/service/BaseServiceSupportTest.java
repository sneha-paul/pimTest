package com.bigname.core.service;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.persistence.dao.mongo.WebsiteDAO;
import com.bigname.pim.api.service.WebsiteService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PimUtil;
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

    private MongoTemplate mongoTemplate;
    
    @Before
    public void setUp() {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) websiteDAO.getTemplate();
        }
        mongoTemplate.dropCollection(Website.class);
    }

    @Test
    public void createEntityTest() {
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

            Website newWebsite = websiteService.get(ID.EXTERNAL_ID(websiteDTO.getWebsiteId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newWebsite));
            Assert.assertTrue(newWebsite.diff(websiteDTO).isEmpty());
        });
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

        Assert.assertEquals(websiteDAO.findAll(PageRequest.of(0, websiteDTOs.size()), false).getTotalElements(), websitesData.size());

    }

    @Test
    public void toggleTest() {
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

        Website websiteDetails = websiteService.get(ID.EXTERNAL_ID(websitesData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(websiteDetails));
        websiteService.toggle(ID.EXTERNAL_ID(websiteDetails.getWebsiteId()), Toggle.get(websiteDetails.getActive()));

        Website updatedWebsite = websiteService.get(ID.EXTERNAL_ID(websiteDetails.getWebsiteId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedWebsite));
        Map<String, Object> diff = websiteDetails.diff(updatedWebsite);
        Assert.assertEquals(diff.size(), 1);
        Assert.assertEquals(diff.get("active"), "N");
    }

    @Test
    public void getTest() {
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

            Website websiteDetails = websiteService.get(ID.EXTERNAL_ID(websiteDTO.getWebsiteId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(websiteDetails));
            Map<String, Object> diff = websiteDTO.diff(websiteDetails);
            Assert.assertEquals(diff.size(), 0);
        });
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

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Page<Website> paginatedResult = websiteService.getAll(0, 10, null,false);
        Assert.assertEquals(paginatedResult.getContent().size(), websitesData.size());
    }

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

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        List<Website> result = websiteService.getAll(Sort.by("websiteName").ascending(), false);
        String[] actual = result.stream().map(website -> website.getWebsiteName()).collect(Collectors.toList()).toArray(new String[0]);
        String[] expected = websitesData.stream().map(websiteData -> (String)websiteData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertArrayEquals(expected, actual);

        mongoTemplate.dropCollection(Website.class);

        // sorting : Descending

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

        result = websiteService.getAll(Sort.by("websiteName").descending(), false);
        actual = result.stream().map(website -> website.getWebsiteName()).collect(Collectors.toList()).toArray(new String[0]);
        expected = websitesData.stream().map(websiteData -> (String)websiteData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertNotEquals(expected, actual);
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

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        String[] ids = {websitesData.get(0).get("externalId").toString(), websitesData.get(1).get("externalId").toString(), websitesData.get(2).get("externalId").toString()};

        Page<Website> paginatedResult = websiteService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), 0, 10, null, false);
        Map<String, Website> websitesMap = paginatedResult.getContent().stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == ids.length && websitesMap.containsKey(ids[0]) && websitesMap.containsKey(ids[1]) && websitesMap.containsKey(ids[2]));
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

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        String[] ids = {websitesData.get(0).get("externalId").toString(), websitesData.get(1).get("externalId").toString(), websitesData.get(2).get("externalId").toString()};

        List<Website> paginatedResult = websiteService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, Website> websitesMap = paginatedResult.stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == ids.length && websitesMap.containsKey(ids[0]) && websitesMap.containsKey(ids[1]) && websitesMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithExclusionsAsPageTest() {
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

        Page<Website> paginatedResult = websiteService.getAllWithExclusions(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), 0, 10, null, false);
        Map<String, Website> websitesMap = paginatedResult.getContent().stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == (websitesData.size() - ids.length) && !websitesMap.containsKey(ids[0]) && !websitesMap.containsKey(ids[1]) && !websitesMap.containsKey(ids[2]));
    }

    @Test
    public void getAllWithExclusionsAsListTest() {
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

        List<Website> paginatedResult = websiteService.getAllWithExclusions(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, Website> websitesMap = paginatedResult.stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == (websitesData.size() - ids.length) && !websitesMap.containsKey(ids[0]) && !websitesMap.containsKey(ids[1]) && !websitesMap.containsKey(ids[2]));
    }

    @Test
    public void findAllAtSearchTest() {
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

        Page<Website> paginatedResult = websiteService.findAll("name", "Test", PageRequest.of(0, websitesData.size()), false);
        Assert.assertEquals(paginatedResult.getContent().size(), websitesData.size());
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

        websitesData.forEach( websiteData -> {
            Website websiteDTO = new Website();
            websiteDTO.setWebsiteName((String) websiteData.get("name"));
            websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
            websiteDTO.setActive((String) websiteData.get("active"));
            websiteDTO.setUrl((String) websiteData.get("url"));
            websiteDAO.insert(websiteDTO);
        });

        Page<Website> paginatedResult = websiteService.findAll(PageRequest.of(0, websitesData.size()), false);
        Assert.assertEquals(paginatedResult.getContent().size(), websitesData.size());
    }

    @Test
    public void updateEntityTest() {
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
            websiteDetails.setUrl("https://www.test11.com");
            websiteDetails.setGroup("DETAILS");

            websiteService.update(ID.EXTERNAL_ID(websiteDetails.getWebsiteId()), websiteDetails);

            Website updatedWebsite = websiteService.get(ID.EXTERNAL_ID(websiteDetails.getWebsiteId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(updatedWebsite));
            Map<String, Object> diff = websiteDTO.diff(updatedWebsite);
            Assert.assertEquals(diff.size(), 1);
            Assert.assertEquals(diff.get("url"), "https://www.test11.com");
        });

    }

    @Test
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

        List<Website> websites = result.stream().map(result1 -> {
            result1.setActive("N");
            return result1;
        }).collect(Collectors.toList());

        websiteService.update(websites);

        result = websiteService.getAll(Sort.by("websiteName").descending(), true);
        websitesMap = result.stream().collect(Collectors.toMap(website -> website.getWebsiteId(), website -> website));
        Assert.assertTrue(websitesMap.size() == (websitesData.size() - ids.length) && !websitesMap.containsKey(ids[0]) && !websitesMap.containsKey(ids[1]) && !websitesMap.containsKey(ids[2]));
        Assert.assertFalse(websitesMap.size() == websitesData.size() && websitesMap.containsKey(ids[0]) && websitesMap.containsKey(ids[1]) && websitesMap.containsKey(ids[2]));
    }

    @Test
    public void cloneInstance() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));

        websitesData.forEach(websiteData -> {
                    Website websiteDTO = new Website();
                    websiteDTO.setWebsiteName((String) websiteData.get("name"));
                    websiteDTO.setWebsiteId((String) websiteData.get("externalId"));
                    websiteDTO.setActive((String) websiteData.get("active"));
                    websiteDTO.setUrl((String) websiteData.get("url"));
                    websiteDAO.insert(websiteDTO);

            Website newWebsite = websiteService.get(ID.EXTERNAL_ID(websiteDTO.getWebsiteId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newWebsite));
            Assert.assertTrue(newWebsite.diff(websiteDTO).isEmpty());

            Website websiteClone = websiteService.cloneInstance(ID.EXTERNAL_ID(newWebsite.getWebsiteId()), Entity.CloneType.LIGHT);
            Assert.assertTrue(websiteClone.getWebsiteId() .equals(newWebsite.getWebsiteId() + "_COPY") && websiteClone.getWebsiteName().equals(newWebsite.getWebsiteName() + "_COPY") && websiteClone.getUrl().equals(newWebsite.getUrl() + "_COPY") && websiteClone.getActive() != newWebsite.getActive());
        });
    }

    @Test
    public void findAll() {
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

        long size = websitesData.stream().filter(x -> x.get("active").equals("N")).count();

        List<Website> result = websiteService.findAll(CollectionsUtil.toMap("active", "N"));
        Assert.assertTrue(result.size() == size);
    }

    @Test
    public void findAll1() {
        List<Map<String, Object>> websitesData = new ArrayList<>();
        websitesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "url", "www.test1.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "url", "www.test2.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "url", "www.test3.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test4.com", "externalId", "TEST_4", "url", "www.test4.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test5.com", "externalId", "TEST_5", "url", "www.test5.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test6.com", "externalId", "TEST_6", "url", "www.test6.com", "active", "Y"));
        websitesData.add(CollectionsUtil.toMap("name", "Test7.com", "externalId", "TEST_7", "url", "www.test7.com", "active", "N"));
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

        long size = websitesData.stream().filter(x -> x.get("active").equals("N")).count();

        GenericCriteria criteria = PimUtil.buildCriteria(CollectionsUtil.toMap("active", "N"));
        List<Website> result = websiteService.findAll(criteria);
        Assert.assertTrue(result.size() == size);
    }

    @Test
    public void findOne() {
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

        Optional<Website> result = websiteService.findOne(CollectionsUtil.toMap("websiteName", websitesData.get(0).get("name")));
        Assert.assertEquals(websitesData.get(0).get("name"), result.get().getWebsiteName());
    }

    @Test
    public void findOne1() {
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

        GenericCriteria criteria = PimUtil.buildCriteria(CollectionsUtil.toMap("websiteName", websitesData.get(0).get("name")));
        Optional<Website> result = websiteService.findOne(criteria);
        Assert.assertEquals(websitesData.get(0).get("name"), result.get().getWebsiteName());
    }

    @Test
    public void getEntityName() throws Exception {
    }

    @Test
    public void validate() throws Exception {
    }

    @Test
    public void validate1() throws Exception {
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

        /*Create a second instance with the same id TEST to check the unique constraint violation of websiteId*/

        Website websiteDTO1 = new Website();
        websiteDTO1.setWebsiteName("Envelope");
        websiteDTO1.setUrl("www.envelope");
        websiteDTO1.setWebsiteId("TEST");
        websiteDTO1.setActive("Y");
        Assert.assertEquals(websiteService.validate(websiteDTO1, context, groups).size(), 1);


        /*Testing forceUniqueId*/
        context.put("forceUniqueId", true);
        Assert.assertTrue(websiteService.validate(websiteDTO1, context, groups).isEmpty());
        Assert.assertEquals(websiteDTO1.getExternalId(), "TEST_1");
        websiteDAO.insert(websiteDTO1);

        context.clear();

        /*Testing uniqueConstraint violation of websiteId with update operation*/
        Website website = websiteDAO.findById(ID.EXTERNAL_ID(websiteDTO.getWebsiteId())).orElse(null);
        website.setWebsiteId("TEST_1");
        website.setUrl("www.test.com");
        website.setGroup("DETAILS");
        website.setActive("Y");
        context.put("id", websiteDTO.getExternalId());

        groups = ValidatableEntity.DetailsGroup.class;
        Assert.assertEquals(websiteService.validate(website, context, groups).size(), 1);

        /*Testing forceUniqueId with update operation*/
        context.put("forceUniqueId", true);
        Assert.assertTrue(websiteService.validate(website, context, groups).isEmpty());
        Assert.assertEquals(website.getExternalId(), "TEST_1_1");
    }

    @Test
    public void paginate() throws Exception {
    }

    @Test
    public void sort() throws Exception {
    }

    @Test
    public void paginate1() throws Exception {
    }

    @Test
    public void cloneInstance1() throws Exception {
    }

    @Test
    public void proxy() throws Exception {
    }

    @Test
    public void getCurrentUser() throws Exception {
    }

    @Test
    public void getExternalIdProperty() throws Exception {
    }

    @Test
    public void getExternalIdPropertyLabel() throws Exception {
    }

    @After
    public void tearDown() {
        mongoTemplate.dropCollection(Website.class);
    }

}
