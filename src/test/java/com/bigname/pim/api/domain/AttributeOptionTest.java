package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
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
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static org.junit.Assert.*;

/**
 * Created by sanoop on 14/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class AttributeOptionTest {
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
       //Create Attribute collection
        AttributeCollection attributeCollectionDTO = new AttributeCollection();
        attributeCollectionDTO.setCollectionName("Test");
        attributeCollectionDTO.setCollectionId("TEST");
        attributeCollectionDTO.setActive("Y");
        attributeCollectionDTO.setDiscontinued("N");
        attributeCollectionDAO.insert(attributeCollectionDTO);

        AttributeCollection attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

        //Create attribute
        Attribute attribute = new Attribute();
        attribute.setActive("Y");
        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
        attribute.setUiType(Attribute.UIType.DROPDOWN);
        attribute.setName("Test");
        attribute.setId("TEST");
        attributeCollectionDetails.addAttribute(attribute);

        attributeCollectionDAO.save(attributeCollectionDetails);

        attributeCollectionDetails = attributeCollectionDAO.findByExternalId(attributeCollectionDTO.getCollectionId()).orElse(null);

        //Create Attribute Option
        Optional<Attribute> attributeDetails = attributeCollectionDetails.getAttribute(attribute.getFullId());
        AttributeOption attributeOption = new AttributeOption();
        attributeOption.setCollectionId(attributeCollectionDTO.getCollectionId());
        attributeOption.setValue("TestOption");
        attributeOption.setAttributeId(attribute.getFullId());
        attributeOption.setActive("Y");
        attributeOption.orchestrate();
        attributeDetails.get().getOptions().put(ValidatableEntity.toId("TestOption"), attributeOption);

        attributeCollectionDAO.save(attributeCollectionDetails);

        Page<AttributeOption> result = attributeCollectionService.getAttributeOptions(attributeCollectionDTO.getCollectionId(), FindBy.EXTERNAL_ID, attributeCollectionDTO.getAttributeFullId(attributeOption),0,1,null);

        Assert.assertEquals(attributeOption.getActive(), "Y");
        Assert.assertEquals(attributeOption.getValue(), "TestOption");

        Assert.assertTrue(ValidationUtil.isNotEmpty(result));
        Assert.assertEquals(result.getContent().get(0).getActive(), attributeOption.getActive());
        Assert.assertEquals(result.getContent().get(0).getValue(), attributeOption.getValue());
        Assert.assertEquals(result.getContent().get(0).getCollectionId(), attributeOption.getCollectionId());


    }
    @Test
    public void getId() throws Exception {
    }

    @Test
    public void setId() throws Exception {
    }

    @Test
    public void getFullId() throws Exception {
    }

    @Test
    public void setFullId() throws Exception {
    }

    @Test
    public void getAttributeId() throws Exception {
    }

    @Test
    public void setAttributeId() throws Exception {
    }

    @Test
    public void getCollectionId() throws Exception {
    }

    @Test
    public void setCollectionId() throws Exception {
    }

    @Test
    public void getValue() throws Exception {
    }

    @Test
    public void setValue() throws Exception {
    }

    @Test
    public void getActive() throws Exception {
    }

    @Test
    public void setActive() throws Exception {
    }

    @Test
    public void getIndependent() throws Exception {
    }

    @Test
    public void setIndependent() throws Exception {
    }

    @Test
    public void getSequenceNum() throws Exception {
    }

    @Test
    public void setSequenceNum() throws Exception {
    }

    @Test
    public void getSubSequenceNum() throws Exception {
    }

    @Test
    public void setSubSequenceNum() throws Exception {
    }

    @Test
    public void getParentOptionFullId() throws Exception {
    }

    @Test
    public void setParentOptionFullId() throws Exception {
    }

    @Test
    public void getParentOptionValue() throws Exception {
    }

    @Test
    public void setParentOptionValue() throws Exception {
    }

    @Test
    public void getReferenceMap() throws Exception {
    }

    @Test
    public void setReferenceMap() throws Exception {
    }

    @Test
    public void merge() throws Exception {
    }

    @Test
    public void orchestrate() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }
}