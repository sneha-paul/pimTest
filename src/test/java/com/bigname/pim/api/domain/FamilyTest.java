package com.bigname.pim.api.domain;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.FamilyDAO;
import com.bigname.pim.api.service.FamilyService;
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
public class FamilyTest {
    @Autowired
    FamilyService familyService;
    @Autowired
    FamilyDAO familyDAO;
    @Before
    public void setUp() throws Exception {
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }
    @Test
    public void accessorsTest() {
        Family familyDTO = new Family();
        familyDTO.setFamilyId("test");
        familyDTO.setFamilyName("test");

        familyDTO.orchestrate();

        Assert.assertEquals(familyDTO.getFamilyId(), "TEST");
        Assert.assertEquals(familyDTO.getFamilyName(), "test");
        Assert.assertEquals(familyDTO.getActive(), "N");

        familyService.create(familyDTO);
        Family newFamily = familyService.get(familyDTO.getFamilyId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newFamily));
        Assert.assertEquals(newFamily.getFamilyId(), familyDTO.getFamilyId());
        Assert.assertEquals(newFamily.getFamilyName(), familyDTO.getFamilyName());
        Assert.assertEquals(newFamily.getActive(), familyDTO.getActive());

    }
    @Test
    public void addAttribute() throws Exception {
    }

    @Test
    public void updateAttribute() throws Exception {
    }

    @Test
    public void addAttributeOption() throws Exception {
    }

    @Test
    public void setExternalId() throws Exception {
    }

    @Test
    public void merge() throws Exception {
    }

    @Test
    public void cloneInstance() throws Exception {
    }

    @Test
    public void toMap() throws Exception {
    }

    @Test
    public void getAddonMasterGroups() throws Exception {
    }

    @Test
    public void getDetailsMasterGroup() throws Exception {
    }

    @Test
    public void getFeaturesMasterGroup() throws Exception {
    }

    @Test
    public void getMasterGroup() throws Exception {
    }

    @Test
    public void addVariantGroup() throws Exception {
    }

    @Test
    public void getVariantGroupAttributes() throws Exception {
    }

    @Test
    public void getVariantGroupAxisAttributes() throws Exception {
    }

    @Test
    public void updateVariantGroupAttributes() throws Exception {
    }

    @Test
    public void updateVariantGroupAxisAttributes() throws Exception {
    }

    @Test
    public void diff() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

}