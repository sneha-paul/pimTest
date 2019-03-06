package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.api.service.AttributeCollectionService;
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
public class AttributeCollectionTest {
    @Autowired
    AttributeCollectionService attributeCollectionService;
    @Autowired
    AttributeCollectionDAO attributeCollectionDAO;
    @Before
    public void setUp() throws Exception {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }
    @Test
    public void accessorsTest() {
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionId("test");
        attributeCollectionDTO.setCollectionName("test");

        attributeCollectionDTO.orchestrate();

        Assert.assertEquals(attributeCollectionDTO.getCollectionId(), "TEST");
        Assert.assertEquals(attributeCollectionDTO.getCollectionName(), "test");
        Assert.assertEquals(attributeCollectionDTO.getActive(), "N");

        attributeCollectionService.create(attributeCollectionDTO);
        AttributeCollection newAttributeCollection = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newAttributeCollection));
        Assert.assertEquals(newAttributeCollection.getCollectionId(), attributeCollectionDTO.getCollectionId());
        Assert.assertEquals(newAttributeCollection.getCollectionName(), attributeCollectionDTO.getCollectionName());
        Assert.assertEquals(newAttributeCollection.getActive(), attributeCollectionDTO.getActive());
    }

    @Test
    public void updateAttribute() throws Exception {
    }

    @Test
    public void addAttributeOption() throws Exception {
    }

    @Test
    public void updateAttributeOption() throws Exception {
    }

    @Test
    public void getAttributeFullId() throws Exception {
    }

    @Test
    public void getAttributeOptionFullId() throws Exception {
    }

    @Test
    public void getAttributeFullId1() throws Exception {
    }

    @Test
    public void getAttribute() throws Exception {
    }

    @Test
    public void getAttributeOption() throws Exception {
    }

    @Test
    public void getAllAttributes() throws Exception {
    }

    @Test
    public void setAllAttributes() throws Exception {
    }

    @Test
    public void setExternalId() throws Exception {
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

    @Test
    public void getAvailableParentAttributes() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }


}