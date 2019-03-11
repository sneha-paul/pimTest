package com.bigname.pim.api.service;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.util.Toggle;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.PricingAttribute;
import com.bigname.pim.api.persistence.dao.PricingAttributeDAO;
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
import java.util.stream.Collectors;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static org.junit.Assert.*;

/**
 * Created by aswathy on 11-03-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class PricingAttributeServiceTest {
    @Autowired
    PricingAttributeService pricingAttributeService;

    @Autowired
    PricingAttributeDAO pricingAttributeDAO;

    @Before
    public void setUp() throws Exception {
        pricingAttributeDAO.getMongoTemplate().dropCollection(PricingAttribute.class);
    }

    @Test
    public void createEntityTest() {
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

            PricingAttribute newPricingAttribute = pricingAttributeService.get(pricingAttributeDTO.getPricingAttributeId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(newPricingAttribute));
            Assert.assertTrue(newPricingAttribute.diff(pricingAttributeDTO).isEmpty());
        });
    }

    @Test
    public void createEntitiesTest(){
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

    @Test
    public void toggleTest() {
        List<Map<String, Object>> pricingAttributesData = new ArrayList<>();
        pricingAttributesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));

        pricingAttributesData.forEach(pricingAttributeData -> {
            PricingAttribute pricingAttributeDTO= new PricingAttribute();
            pricingAttributeDTO.setPricingAttributeName((String)pricingAttributeData.get("name"));
            pricingAttributeDTO.setPricingAttributeId((String)pricingAttributeData.get("externalId"));
            pricingAttributeDTO.setActive((String)pricingAttributeData.get("active"));
            pricingAttributeService.create(pricingAttributeDTO);
        });

        PricingAttribute pricingAttributeDetails = pricingAttributeService.get(pricingAttributesData.get(0).get("externalId").toString(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(pricingAttributeDetails));
        pricingAttributeService.toggle(pricingAttributeDetails.getPricingAttributeId(), EXTERNAL_ID, Toggle.get(pricingAttributeDetails.getActive()));

        PricingAttribute updatedPricingAttribute = pricingAttributeService.get(pricingAttributeDetails.getPricingAttributeId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(updatedPricingAttribute));
        Assert.assertEquals(updatedPricingAttribute.getActive(), "N");
    }

    @Test
    public void getTest() {
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

            PricingAttribute pricingAttributeDetails = pricingAttributeService.get(pricingAttributeDTO.getPricingAttributeId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(ValidationUtil.isNotEmpty(pricingAttributeDetails));
            Map<String, Object> diff = pricingAttributeDTO.diff(pricingAttributeDetails);
            Assert.assertEquals(diff.size(), 0);
        });
    }

    @After
    public void tearDown() throws Exception {
        pricingAttributeDAO.getMongoTemplate().dropCollection(PricingAttribute.class);

    }

}