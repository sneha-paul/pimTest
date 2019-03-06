package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.PricingAttributeDAO;
import com.bigname.pim.api.service.PricingAttributeService;
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
 * Created by sanoop on 06/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class PricingAttributeTest {
    @Autowired
    PricingAttributeService pricingAttributeService;
    @Autowired
    PricingAttributeDAO pricingAttributeDAO;
    @Before
    public void setUp() throws Exception {
        pricingAttributeDAO.getMongoTemplate().dropCollection(PricingAttribute.class);
    }
    @Test
    public void accessorsTest() {
        PricingAttribute pricingAttributeDTO = new PricingAttribute();
        pricingAttributeDTO.setPricingAttributeId("test");
        pricingAttributeDTO.setPricingAttributeName("test");

        pricingAttributeDTO.orchestrate();

        Assert.assertEquals(pricingAttributeDTO.getPricingAttributeId(), "TEST");
        Assert.assertEquals(pricingAttributeDTO.getPricingAttributeName(), "test");
        Assert.assertEquals(pricingAttributeDTO.getActive(), "N");

        pricingAttributeService.create(pricingAttributeDTO);
        PricingAttribute newPricingAttribute = pricingAttributeService.get(pricingAttributeDTO.getPricingAttributeId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newPricingAttribute));
        Assert.assertEquals(newPricingAttribute.getPricingAttributeId(), pricingAttributeDTO.getPricingAttributeId());
        Assert.assertEquals(newPricingAttribute.getPricingAttributeName(), pricingAttributeDTO.getPricingAttributeName());
        Assert.assertEquals(newPricingAttribute.getActive(), pricingAttributeDTO.getActive());
    }

    @Test
    public void cloneInstance() throws Exception {
    }

    @Test
    public void merge() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }

    @Test
    public void diff() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        pricingAttributeDAO.getMongoTemplate().dropCollection(PricingAttribute.class);
    }

}