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

import java.util.Map;

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
        //Create New Instance
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionId("test");
        attributeCollectionDTO.setCollectionName("test");

        attributeCollectionDTO.orchestrate();

        //Testing equals unique id
        Assert.assertEquals(attributeCollectionDTO.getCollectionId(), "TEST");
        Assert.assertEquals(attributeCollectionDTO.getCollectionName(), "test");
        Assert.assertEquals(attributeCollectionDTO.getActive(), "N");

        //Create
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
        //Create Attribute collection original instance
        AttributeCollection original = new AttributeCollection();
        original.setExternalId("test");
        original.setCollectionId("test");
        original.setCollectionName("test");
        original.setActive("N");

        //Create Attribute collection modified instance
        AttributeCollection modified = new AttributeCollection();
        modified.setGroup("DETAILS");
        modified.setExternalId("TEST");
        modified.setCollectionName("TEST");
        modified.setCollectionId("TEST");
        modified.setActive("N");

        //Merge
        original = original.merge(modified);
        Assert.assertEquals(original.getCollectionId(), "TEST");
        Assert.assertEquals(original.getCollectionName(), "TEST");
        Assert.assertEquals(original.getExternalId(), "TEST");
        Assert.assertEquals(original.getActive(), "N");

        //Without DETAILS
        AttributeCollection modified1 = new AttributeCollection();
        modified1.setExternalId("test");
        modified1.setCollectionId("test");
        modified1.setCollectionName("test");
        modified1.setActive("N");

        original=original.merge(modified1);
        Assert.assertEquals(original.getCollectionName(), "TEST");
        Assert.assertEquals(original.getCollectionId(), "TEST");
        Assert.assertEquals(original.getExternalId(), "TEST");
        Assert.assertEquals(original.getActive(), "N");

        //Add ATTRIBUTES
    }

    @Test
    public void toMap() throws Exception {
        //Create new instance
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionId("test");
        attributeCollectionDTO.setCollectionName("test");
        attributeCollectionDTO.setExternalId("test");
        attributeCollectionDTO.setActive("N");

        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDTO.getCollectionId()));

        //Testing equals with id
        Assert.assertEquals(attributeCollectionDTO.getCollectionId(), "TEST");
        Assert.assertEquals(attributeCollectionDTO.getCollectionName(), "test");
        Assert.assertEquals(attributeCollectionDTO.getExternalId(), "TEST");
        Assert.assertEquals(attributeCollectionDTO.getActive(), "N");

        //Get attributeCollectionDTO
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDTO.getCollectionId()));
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDTO.getCollectionName()));
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDTO.getExternalId()));
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeCollectionDTO.getActive()));
    }

    @Test
    public void diff() throws Exception {
        //Create First Instance
        AttributeCollection attributeCollection1 = new AttributeCollection();
        attributeCollection1.setCollectionId("test");
        attributeCollection1.setCollectionName("test");
        //setAttributes

        //Create Second Instance
        AttributeCollection attributeCollection2 = new AttributeCollection();
        attributeCollection2.setCollectionId("test");
        attributeCollection2.setCollectionName("test.com");

        Map<String, Object> diff = attributeCollection1.diff(attributeCollection2);
        Assert.assertEquals(diff.size(), 2);
        Assert.assertEquals(diff.get("collectionName"), "test.com");


    }

    @Test
    public void getAvailableParentAttributes() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }


}