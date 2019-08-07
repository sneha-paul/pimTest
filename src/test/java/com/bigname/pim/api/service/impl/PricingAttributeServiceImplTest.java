package com.bigname.pim.api.service.impl;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.PricingAttribute;
import com.bigname.pim.api.persistence.dao.mongo.PricingAttributeDAO;
import com.bigname.pim.api.service.PricingAttributeService;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.domain.ValidatableEntity;
import com.m7.xtreme.xcore.util.GenericCriteria;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import com.m7.xtreme.xplatform.domain.User;
import com.m7.xtreme.xplatform.service.UserService;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by aswathy on 11-03-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class PricingAttributeServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    PricingAttributeService pricingAttributeService;

    @Autowired
    PricingAttributeDAO pricingAttributeDAO;

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
            mongoTemplate = (MongoTemplate) pricingAttributeDAO.getTemplate();
        }
        mongoTemplate.dropCollection(PricingAttribute.class);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createEntityTest() {
        //creating pricingAttribute
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));

            pricingAttributeService.create(pricingAttributeDTO);

            PricingAttribute newPricingAttribute = pricingAttributeService.get(ID.EXTERNAL_ID(pricingAttributeDTO.getPricingAttributeId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newPricingAttribute));
            Assert.assertTrue(newPricingAttribute.diff(pricingAttributeDTO).isEmpty());
        });
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void createEntitiesTest(){
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        List<PricingAttribute> pricingAttributeDTOs = pricingAttributesData.stream().map(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            return pricingAttributeDTO;
        }).collect(Collectors.toList());

        pricingAttributeService.create(pricingAttributeDTOs);
        Assert.assertEquals(pricingAttributeDAO.findAll(PageRequest.of(0, pricingAttributeDTOs.size()), false).getTotalElements(), pricingAttributesData.size());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void toggleTest() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeService.create(pricingAttributeDTO);
        });

        PricingAttribute pricingAttributeDetails = pricingAttributeService.get(ID.EXTERNAL_ID(pricingAttributesData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(pricingAttributeDetails));
        //toggle
        pricingAttributeService.toggle(ID.EXTERNAL_ID(pricingAttributeDetails.getPricingAttributeId()), Toggle.get(pricingAttributeDetails.getActive()));
        PricingAttribute updatedPricingAttribute = pricingAttributeService.get(ID.EXTERNAL_ID(pricingAttributeDetails.getPricingAttributeId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedPricingAttribute));
        Assert.assertEquals(updatedPricingAttribute.getActive(), "N");

        pricingAttributeService.toggle(ID.EXTERNAL_ID(updatedPricingAttribute.getPricingAttributeId()), Toggle.get(updatedPricingAttribute.getActive()));
        PricingAttribute updatedPricingAttribute1 = pricingAttributeService.get(ID.EXTERNAL_ID(updatedPricingAttribute.getPricingAttributeId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedPricingAttribute1));
        Assert.assertEquals(updatedPricingAttribute1.getActive(), "Y");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getTest() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));

            pricingAttributeDAO.insert(pricingAttributeDTO);

            //Getting pricingAttributes
            PricingAttribute pricingAttributeDetails = pricingAttributeService.get(ID.EXTERNAL_ID(pricingAttributeDTO.getPricingAttributeId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(pricingAttributeDetails));
            Map<String, Object> diff = pricingAttributeDTO.diff(pricingAttributeDetails);
            Assert.assertEquals(diff.size(), 0);
        });
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllAsPageTest() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeService.create(pricingAttributeDTO);
        });

        //Getting pricingAttributes
        Page<PricingAttribute> paginatedResult = pricingAttributeService.getAll(0, 10, null,false);
        Assert.assertEquals(paginatedResult.getContent().size(), pricingAttributesData.size());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllAsListTest() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeService.create(pricingAttributeDTO);
        });

        List<PricingAttribute> result = pricingAttributeService.getAll(Sort.by("pricingAttributeName").ascending(), false);
        String[] actual = result.stream().map(pricingAttribute -> pricingAttribute.getPricingAttributeName()).collect(Collectors.toList()).toArray(new String[0]);
        String[] expected = pricingAttributesData.stream().map(pricingAttributeData -> (String)pricingAttributeData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertArrayEquals(expected, actual);

        mongoTemplate.dropCollection(PricingAttribute.class);

        // sorting : Descending

        pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeService.create(pricingAttributeDTO);
        });

        result = pricingAttributeService.getAll(Sort.by("pricingAttributeName").descending(), false);
        actual = result.stream().map(pricingAttribute -> pricingAttribute.getPricingAttributeName()).collect(Collectors.toList()).toArray(new String[0]);
        expected = pricingAttributesData.stream().map(pricingAttributeData -> (String)pricingAttributeData.get("name")).sorted(String::compareTo).collect(Collectors.toList()).toArray(new String[0]);
        Assert.assertNotEquals(expected, actual);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllWithIdsAsPageTest() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeService.create(pricingAttributeDTO);
        });

        String[] ids = {pricingAttributesData.get(0).get("externalId").toString(), pricingAttributesData.get(1).get("externalId").toString(), pricingAttributesData.get(2).get("externalId").toString()};

        //Getting pricingAttributes as page
        Page<PricingAttribute> paginatedResult = pricingAttributeService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), 0, 10, null, false);
        Map<String, PricingAttribute> pricingAttributesMap = paginatedResult.getContent().stream().collect(Collectors.toMap(pricingAttribute -> pricingAttribute.getPricingAttributeId(), pricingAttribute -> pricingAttribute));
        Assert.assertTrue(pricingAttributesMap.size() == ids.length && pricingAttributesMap.containsKey(ids[0]) && pricingAttributesMap.containsKey(ids[1]) && pricingAttributesMap.containsKey(ids[2]));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllWithIdsAsListTest() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeService.create(pricingAttributeDTO);
        });

        String[] ids = {pricingAttributesData.get(0).get("externalId").toString(), pricingAttributesData.get(1).get("externalId").toString(), pricingAttributesData.get(2).get("externalId").toString()};

        //Getting pricingAttributes as list
        List<PricingAttribute> listedResult = pricingAttributeService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, PricingAttribute> pricingAttributesMap = listedResult.stream().collect(Collectors.toMap(pricingAttribute -> pricingAttribute.getPricingAttributeId(), pricingAttribute -> pricingAttribute));
        Assert.assertTrue(pricingAttributesMap.size() == ids.length && pricingAttributesMap.containsKey(ids[0]) && pricingAttributesMap.containsKey(ids[1]) && pricingAttributesMap.containsKey(ids[2]));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllWithExclusionsAsPageTest() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeService.create(pricingAttributeDTO);
        });

        String[] ids = {pricingAttributesData.get(0).get("externalId").toString(), pricingAttributesData.get(1).get("externalId").toString(), pricingAttributesData.get(2).get("externalId").toString()};

        //Getting pricingAttributes with exclude Ids
        Page<PricingAttribute> paginatedResult = pricingAttributeService.getAllWithExclusions(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), 0, 10, null, false);
        Map<String, PricingAttribute> pricingAttributesMap = paginatedResult.getContent().stream().collect(Collectors.toMap(pricingAttribute -> pricingAttribute.getPricingAttributeId(), pricingAttribute -> pricingAttribute));
        Assert.assertTrue(pricingAttributesMap.size() == (pricingAttributesData.size() - ids.length) && !pricingAttributesMap.containsKey(ids[0]) && !pricingAttributesMap.containsKey(ids[1]) && !pricingAttributesMap.containsKey(ids[2]));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void getAllWithExclusionsAsListTest() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeService.create(pricingAttributeDTO);
        });

        String[] ids = {pricingAttributesData.get(0).get("externalId").toString(), pricingAttributesData.get(1).get("externalId").toString(), pricingAttributesData.get(2).get("externalId").toString()};

        //Getting pricingAttributes with exclude Ids
        List<PricingAttribute> listedResult = pricingAttributeService.getAllWithExclusions(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, PricingAttribute> pricingAttributesMap = listedResult.stream().collect(Collectors.toMap(pricingAttribute -> pricingAttribute.getPricingAttributeId(), pricingAttribute -> pricingAttribute));
        Assert.assertTrue(pricingAttributesMap.size() == (pricingAttributesData.size() - ids.length) && !pricingAttributesMap.containsKey(ids[0]) && !pricingAttributesMap.containsKey(ids[1]) && !pricingAttributesMap.containsKey(ids[2]));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAllAtSearchTest() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeService.create(pricingAttributeDTO);
        });

        Page<PricingAttribute> paginatedResult = pricingAttributeService.findAll("name", "Test", PageRequest.of(0, pricingAttributesData.size()), false);
        Assert.assertEquals(paginatedResult.getContent().size(), 3);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAllTest() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeService.create(pricingAttributeDTO);
        });

        Page<PricingAttribute> paginatedResult = pricingAttributeService.findAll(PageRequest.of(0, pricingAttributesData.size()), false);
        Assert.assertEquals(paginatedResult.getContent().size(), 3);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void updateEntityTest() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeDAO.insert(pricingAttributeDTO);
        });

        PricingAttribute pricingAttributeDetails = pricingAttributeService.get(ID.EXTERNAL_ID(pricingAttributesData.get(0).get("externalId").toString()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(pricingAttributeDetails));
        pricingAttributeDetails.setPricingAttributeName("Test");
        pricingAttributeDetails.setGroup("DETAILS");

        //Updating pricingAttributes
        pricingAttributeService.update(ID.EXTERNAL_ID(pricingAttributeDetails.getPricingAttributeId()), pricingAttributeDetails);
        PricingAttribute updatedPricingAttribute = pricingAttributeService.get(ID.EXTERNAL_ID(pricingAttributeDetails.getPricingAttributeId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedPricingAttribute));
        Assert.assertEquals(updatedPricingAttribute.getPricingAttributeName(), "Test");
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void updateEntitiesTest(){
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeDAO.insert(pricingAttributeDTO);
        });

        String[] ids = {pricingAttributesData.get(0).get("externalId").toString(), pricingAttributesData.get(1).get("externalId").toString(), pricingAttributesData.get(2).get("externalId").toString()};

        List<PricingAttribute> result = pricingAttributeService.getAll(Arrays.stream(ids).map(ID::EXTERNAL_ID).collect(Collectors.toList()), null, false);
        Map<String, PricingAttribute> pricingAttributesMap = result.stream().collect(Collectors.toMap(pricingAttribute -> pricingAttribute.getPricingAttributeId(), pricingAttribute -> pricingAttribute));
        Assert.assertTrue(pricingAttributesMap.size() == ids.length && pricingAttributesMap.containsKey(ids[0]) && pricingAttributesMap.containsKey(ids[1]) && pricingAttributesMap.containsKey(ids[2]));

        List<PricingAttribute> pricingAttributes = result.stream().map(result1 -> {
            result1.setActive("N");
            return result1;
        }).collect(Collectors.toList());

        //updating pricingAttribute
        pricingAttributeService.update(pricingAttributes);
        result = pricingAttributeService.getAll(Sort.by("websiteName").descending(), true);
        pricingAttributesMap = result.stream().collect(Collectors.toMap(pricingAttribute -> pricingAttribute.getPricingAttributeId(), pricingAttribute -> pricingAttribute));
        Assert.assertTrue(pricingAttributesMap.size() == (pricingAttributesData.size() - ids.length) && !pricingAttributesMap.containsKey(ids[0]) && !pricingAttributesMap.containsKey(ids[1]) && !pricingAttributesMap.containsKey(ids[2]));
        Assert.assertFalse(pricingAttributesMap.size() == pricingAttributesData.size() && pricingAttributesMap.containsKey(ids[0]) && pricingAttributesMap.containsKey(ids[1]) && pricingAttributesMap.containsKey(ids[2]));
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void cloneInstance() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeDAO.insert(pricingAttributeDTO);

            PricingAttribute newPricingAttribute = pricingAttributeService.get(ID.EXTERNAL_ID(pricingAttributeDTO.getPricingAttributeId()), false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newPricingAttribute));
            Assert.assertTrue(newPricingAttribute.diff(pricingAttributeDTO).isEmpty());
            // cloning pricingAttribute
            PricingAttribute pricingAttributeClone = pricingAttributeService.cloneInstance(ID.EXTERNAL_ID(newPricingAttribute.getPricingAttributeId()), Entity.CloneType.LIGHT);
            Assert.assertTrue(pricingAttributeClone.getPricingAttributeId() .equals(newPricingAttribute.getPricingAttributeId() + "_COPY") && pricingAttributeClone.getPricingAttributeName().equals(newPricingAttribute.getPricingAttributeName() + "_COPY") && pricingAttributeClone.getActive() != newPricingAttribute.getActive());
        });
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAll() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "N"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeDAO.insert(pricingAttributeDTO);
        });

        //Getting pricingAttributes
        List<PricingAttribute> result = pricingAttributeService.findAll(CollectionsUtil.toMap("active", "N"), false);
        Assert.assertTrue(result.size() == 1);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findAll1() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "N"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeDAO.insert(pricingAttributeDTO);
        });

        //Getting pricingAttributes
        GenericCriteria criteria = PlatformUtil.buildCriteria(CollectionsUtil.toMap("active", "N"));
        List<PricingAttribute> result = pricingAttributeService.findAll(criteria, false);
        Assert.assertTrue(result.size() == 1);
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findOne() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "N"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeDAO.insert(pricingAttributeDTO);
        });

        //Getting pricingAttribute
        PricingAttribute result = pricingAttributeService.findOne(CollectionsUtil.toMap("pricingAttributeName", pricingAttributesData.get(0).get("name"))).orElse(null);
        Assert.assertEquals(pricingAttributesData.get(0).get("name"), result.getPricingAttributeName());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void findOne1() {
        //creating pricingAttributes
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test2.com", "externalId", "TEST_2", "active", "Y"));
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test3.com", "externalId", "TEST_3", "active", "N"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeDAO.insert(pricingAttributeDTO);
        });
        //Getting pricingAttribute
        GenericCriteria criteria = PlatformUtil.buildCriteria(CollectionsUtil.toMap("pricingAttributeName", pricingAttributesData.get(0).get("name")));
        PricingAttribute result = pricingAttributeService.findOne(criteria).orElse(null);
        Assert.assertEquals(pricingAttributesData.get(0).get("name"), result.getPricingAttributeName());
    }

    @WithUserDetails("manu@blacwood.com")
    @Test
    public void validate1() throws Exception {
        /* Create a valid new instance with id TEST */
        PricingAttribute pricingAttributeDTO = new PricingAttribute();
        pricingAttributeDTO.setPricingAttributeName("test");
        pricingAttributeDTO.setPricingAttributeId("TEST");
        pricingAttributeDTO.setActive("Y");

        Map<String, Object> context = new HashMap<>();

        Class groups = ValidatableEntity.CreateGroup.class;
//        validate
        Assert.assertTrue(pricingAttributeService.validate(pricingAttributeDTO, context, groups).isEmpty());
//        insert the valid instance
        pricingAttributeDAO.insert(pricingAttributeDTO);

        /*Create a second instance with the same id TEST to check the unique constraint violation of pricingAttributeId*/

        PricingAttribute pricingAttributeDTO1 = new PricingAttribute();
        pricingAttributeDTO1.setPricingAttributeName("Envelope");
        pricingAttributeDTO1.setPricingAttributeId("TEST");
        pricingAttributeDTO1.setActive("Y");

        Assert.assertEquals(pricingAttributeService.validate(pricingAttributeDTO1, context, groups).size(), 1);

        /*Testing forceUniqueId*/
        context.put("forceUniqueId", true);
        Assert.assertTrue(pricingAttributeService.validate(pricingAttributeDTO1, context, groups).isEmpty());
        Assert.assertEquals(pricingAttributeDTO1.getExternalId(), "TEST_1");
        pricingAttributeDAO.insert(pricingAttributeDTO1);

        context.clear();

        /*Testing uniqueConstraint violation of pricingAttributeId with update operation*/
        PricingAttribute pricingAttribute = pricingAttributeDAO.findById(ID.EXTERNAL_ID(pricingAttributeDTO.getPricingAttributeId())).orElse(null);
        pricingAttribute.setPricingAttributeId("TEST_1");
        pricingAttribute.setGroup("DETAILS");
        pricingAttribute.setActive("Y");
        context.put("id", pricingAttributeDTO.getExternalId());

        groups = ValidatableEntity.DetailsGroup.class;
        Assert.assertEquals(pricingAttributeService.validate(pricingAttribute, context, groups).size(), 1);

        /*Testing forceUniqueId with update operation*/
        context.put("forceUniqueId", true);
        Assert.assertTrue(pricingAttributeService.validate(pricingAttribute, context, groups).isEmpty());
        Assert.assertEquals(pricingAttribute.getExternalId(), "TEST_1_1");
    }


    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(PricingAttribute.class);

    }

}