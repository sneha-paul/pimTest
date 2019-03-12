package com.bigname.pim.api.domain;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.Entity;
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

import java.util.ArrayList;
import java.util.List;
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
        //Create Family Instance
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
        //Create Family Original Instance
        Family original = new Family();
        original.setFamilyId("TEST");
        original.setFamilyName("Test");
        original.setExternalId("TEST");
        original.setActive("N");

        //Create Family Modified Instance
        Family modified = new Family();
        modified.setGroup("DETAILS");
        modified.setFamilyId("TEST-A");
        modified.setFamilyName("Test-A");
        modified.setExternalId("TEST-A");
        modified.setActive("N");

        original = original.merge(modified);
        Assert.assertEquals(original.getFamilyName(), "Test-A");
        Assert.assertEquals(original.getFamilyId(), "TEST-A");
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getActive(), "N");

        //Without Details
        Family modified1 = new Family();
        modified1.setFamilyId("TEST");
        modified1.setFamilyName("Test");
        modified1.setExternalId("TEST");
        modified1.setActive("N");

        original = original.merge(modified1);
        Assert.assertEquals(original.getFamilyId(), "TEST-A");
        Assert.assertEquals(original.getFamilyName(), "Test-A");
        Assert.assertEquals(original.getExternalId(), "TEST-A");
        Assert.assertEquals(original.getActive(), "N");

        //Add ATTRIBUTES
        Attribute original1 = new Attribute();
        original1.setName("Test");
        original1.setActive("Y");

        Attribute modified2 = new Attribute();
        modified2.setGroup("ATTRIBUTES");
        modified2.setName("TEST-A");
        modified2.setActive("Y");

        original1 = original1.merge(modified2);
        Assert.assertEquals(original1.getName(), "TEST-A");
        Assert.assertEquals(original1.getActive(), "Y");


    }

    @Test
    public void cloneInstance() throws Exception {
        //Adding website
        List<Map<String, Object>> familiesData = new ArrayList<>();
        familiesData.add(CollectionsUtil.toMap("name", "Test1.com", "externalId", "TEST_1", "active", "Y"));

        familiesData.forEach(familyData -> {
            Family familyDTO = new Family();
            familyDTO.setFamilyName((String) familyData.get("name"));
            familyDTO.setFamilyId((String) familyData.get("externalId"));
            familyDTO.setActive((String) familyData.get("active"));
            familyDAO.insert(familyDTO);


            //Clone Family
            Family newFamily = familyService.get(familyDTO.getFamilyId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(newFamily != null);
            Assert.assertTrue(newFamily.diff(familyDTO).isEmpty());

            Family familyClone = familyService.cloneInstance(newFamily.getFamilyId(), EXTERNAL_ID, Entity.CloneType.LIGHT);
            Assert.assertTrue(familyClone.getFamilyId() .equals(newFamily.getFamilyId() + "_COPY") && familyClone.getFamilyName().equals(newFamily.getFamilyName() + "_COPY") && familyClone.getActive() != newFamily.getActive());
        });
    }


    @Test
    public void toMap() throws Exception {
        //Create new instance
        Family familyDTO = new Family();
        familyDTO.setFamilyId("test");
        familyDTO.setFamilyName("test");
        familyDTO.setExternalId("test");
        familyDTO.setActive("N");

        Assert.assertTrue(ValidationUtil.isNotEmpty(familyDTO.getFamilyId()));

        //Testing equals with id
        Assert.assertEquals(familyDTO.getFamilyId(), "TEST");
        Assert.assertEquals(familyDTO.getFamilyName(), "test");
        Assert.assertEquals(familyDTO.getExternalId(), "TEST");
        Assert.assertEquals(familyDTO.getActive(), "N");

        //Get websiteDTO
        Assert.assertTrue(ValidationUtil.isNotEmpty(familyDTO.getFamilyId()));
        Assert.assertTrue(ValidationUtil.isNotEmpty(familyDTO.getFamilyName()));
        Assert.assertTrue(ValidationUtil.isNotEmpty(familyDTO.getExternalId()));
        Assert.assertTrue(ValidationUtil.isNotEmpty(familyDTO.getActive()));

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
        //Create first Instance
        Family family1 = new Family();
        family1.setFamilyId("test");
        family1.setFamilyName("test");
        family1.setExternalId("test");

        //Create Second instance
        Family family2 = new Family();
        family2.setFamilyId("test");
        family2.setFamilyName("test.com");
        family2.setExternalId("test");

        //Checking First instance and Second instance
        Map<String, Object> diff = family1.diff(family2);
        Assert.assertEquals(diff.size(), 2);
        Assert.assertEquals(diff.get("familyName"), "test.com");
    }
    @After
    public void tearDown() throws Exception {
        familyDAO.getMongoTemplate().dropCollection(Family.class);
    }

}