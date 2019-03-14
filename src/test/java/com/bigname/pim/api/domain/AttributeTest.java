package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
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

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static org.junit.Assert.*;

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
    @Before
    public void setUp() throws Exception {
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
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
        AttributeCollection newAttributeCollection = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), EXTERNAL_ID, false).orElse(null);
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

        AttributeCollection newAttributeCollection2 = attributeCollectionService.get(attributeCollectionDTO.getCollectionId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newAttributeCollection2));
        Assert.assertEquals(newAttributeCollection2.getAllAttributes().get(0).getName(), attributeDTO.getName());
        Assert.assertEquals(newAttributeCollection2.getAllAttributes().get(0).getActive(), attributeDTO.getActive());

    }
    @Test
    public void buildInstance() throws Exception {
    }

    @Test
    public void getName() throws Exception {
    }

    @Test
    public void setName() throws Exception {
    }

    @Test
    public void getLabel() throws Exception {
    }

    @Test
    public void setLabel() throws Exception {
    }

    @Test
    public void getUiType() throws Exception {
    }

    @Test
    public void setUiType() throws Exception {
    }

    @Test
    public void getDataType() throws Exception {
    }

    @Test
    public void setDataType() throws Exception {
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
    public void getRegEx() throws Exception {
    }

    @Test
    public void setRegEx() throws Exception {
    }

    @Test
    public void getSelectable() throws Exception {
    }

    @Test
    public void setSelectable() throws Exception {
    }

    @Test
    public void getActive() throws Exception {
    }

    @Test
    public void setActive() throws Exception {
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
    public void getAttributeGroup() throws Exception {
    }

    @Test
    public void setAttributeGroup() throws Exception {
    }

    @Test
    public void getOptions() throws Exception {
    }

    @Test
    public void setOptions() throws Exception {
    }

    @Test
    public void getParentBasedOptions() throws Exception {
    }

    @Test
    public void setParentBasedOptions() throws Exception {
    }

    @Test
    public void getReferenceMap() throws Exception {
    }

    @Test
    public void setReferenceMap() throws Exception {
    }

    @Test
    public void getParentAttributeId() throws Exception {
    }

    @Test
    public void setParentAttributeId() throws Exception {
    }

    @Test
    public void getTopLevelGroup() throws Exception {
    }

    @Test
    public void orchestrate() throws Exception {
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
        attributeCollectionDAO.getMongoTemplate().dropCollection(AttributeCollection.class);
    }


}