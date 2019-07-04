package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.AttributeCollectionDAO;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import com.m7.xtreme.xcore.util.ID;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.m7.xtreme.xcore.util.FindBy.EXTERNAL_ID;


/**
 * Created by sanoop on 14/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class AttributeTest {
    @Autowired
    AttributeCollectionService attributeCollectionService;
    @Autowired
    AttributeCollectionDAO attributeCollectionDAO;
    private MongoTemplate mongoTemplate;
    @Before
    public void setUp() throws Exception {
        if(ValidationUtil.isEmpty(mongoTemplate)) {
            mongoTemplate = (MongoTemplate) attributeCollectionDAO.getTemplate();
        }
        mongoTemplate.dropCollection(AttributeCollection.class);
    }
    @Test
    public void accessorsTest() {
        //Create New Instance attributeCollection
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
        AttributeCollection newAttributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newAttributeCollection));
        Assert.assertEquals(newAttributeCollection.getCollectionId(), attributeCollectionDTO.getCollectionId());
        Assert.assertEquals(newAttributeCollection.getCollectionName(), attributeCollectionDTO.getCollectionName());
        Assert.assertEquals(newAttributeCollection.getActive(), attributeCollectionDTO.getActive());

        //Create new instance attribute
        Attribute attributeDTO = new Attribute();
        attributeDTO.setActive("Y");
        attributeDTO.setName("test.com");

        Assert.assertEquals(attributeDTO.getActive(), "Y");
        Assert.assertEquals(attributeDTO.getName(), "test.com");

        newAttributeCollection.addAttribute(attributeDTO);
        attributeCollectionDAO.save(newAttributeCollection);

        AttributeCollection newAttributeCollection2 = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionDTO.getCollectionId()), false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newAttributeCollection2));
        Assert.assertEquals(newAttributeCollection2.getAllAttributes().get(0).getName(), attributeDTO.getName());
        Assert.assertEquals(newAttributeCollection2.getAllAttributes().get(0).getActive(), attributeDTO.getActive());

    }

    @Test
    public void orchestrate() throws Exception {
        //Create id
        Attribute attributeDTO = new Attribute();
        attributeDTO.setId("test");
        attributeDTO.setName("test");
        attributeDTO.orchestrate();

        //Check websiteId
        Assert.assertTrue(ValidationUtil.isNotEmpty(attributeDTO.getId()));
        Assert.assertEquals(attributeDTO.getId(), "test");
    }
    @Test
    public void merge() throws Exception {
    }

    @Test
    public void toMap() throws Exception {

    }

    @Test
    public void compare() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(AttributeCollection.class);
    }


}