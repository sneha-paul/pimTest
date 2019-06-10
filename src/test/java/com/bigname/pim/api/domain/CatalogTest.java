package com.bigname.pim.api.domain;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.service.CatalogService;
import com.m7.common.util.CollectionsUtil;
import com.m7.common.util.ValidationUtil;
import com.m7.xcore.domain.Entity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.m7.xcore.util.FindBy.EXTERNAL_ID;


/**
 * Created by sanoop on 05/03/2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CatalogTest {

    @Autowired
    CatalogService catalogService;
    @Autowired
    CatalogDAO catalogDAO;

    @Before
    public void setUp() throws Exception {
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }
    @Test
    public void accessorsTest(){
        //Create new instance
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("test");
        catalogDTO.setCatalogId("test");
        catalogDTO.setDescription("test");

        catalogDTO.orchestrate();

        //Testing equals with id
        Assert.assertEquals(catalogDTO.getCatalogId(), "TEST");
        Assert.assertEquals(catalogDTO.getCatalogName(), "test");
        Assert.assertEquals(catalogDTO.getDescription(), "test");
        Assert.assertEquals(catalogDTO.getActive(), "N");

        //create
        catalogService.create(catalogDTO);
        Catalog newCatalog = catalogService.get(catalogDTO.getCatalogId(), EXTERNAL_ID, false).orElse(null);
        Assert.assertTrue(ValidationUtil.isNotEmpty(newCatalog));

        Assert.assertEquals(newCatalog.getCatalogId(), catalogDTO.getCatalogId());
        Assert.assertEquals(newCatalog.getCatalogName(), catalogDTO.getCatalogName());
        Assert.assertEquals(newCatalog.getDescription(), catalogDTO.getDescription());
        Assert.assertEquals(newCatalog.getActive(), catalogDTO.getActive());
    }
    @Test
    public void getRootCategories() throws Exception {
    }

    @Test
    public void setRootCategories() throws Exception {
    }

    @Test
    public void orchestrate() throws Exception {
        //Create id
        Catalog catalogDTO = new Catalog();
        catalogDTO.setExternalId("test");
        catalogDTO.orchestrate();

        //Check CatalogId
        Assert.assertTrue(ValidationUtil.isNotEmpty(catalogDTO.getCatalogId()));
        Assert.assertEquals(catalogDTO.getCatalogId(), "TEST");
    }

    @Test
    public void merge() throws Exception {
        //Create Catalog Original
        Catalog original = new Catalog();
        original.setCatalogName("One");
        original.setCatalogId("ONE");
        original.setExternalId("ONE");
        original.setDescription("ONE");

        //Add Details
        Catalog modified = new Catalog();
        modified.setGroup("DETAILS");
        modified.setCatalogName("One-A");
        modified.setCatalogId("ONE-A");
        modified.setExternalId("ONE-A");
        modified.setDescription("ONE-A");

        original = original.merge(modified);
        Assert.assertEquals(original.getCatalogName(), "One-A");
        Assert.assertEquals(original.getCatalogId(), "ONE-A");
        Assert.assertEquals(original.getExternalId(), "ONE-A");
        Assert.assertEquals(original.getDescription(), "ONE-A");

        //Without Details
        Catalog modified1 = new Catalog();
        modified1.setCatalogName("One");
        modified1.setCatalogId("ONE");
        modified1.setExternalId("ONE");
        modified1.setDescription("ONE");

        original = original.merge(modified1);
        Assert.assertEquals(original.getCatalogName(), "One-A");
        Assert.assertEquals(original.getCatalogId(), "ONE-A");
        Assert.assertEquals(original.getExternalId(), "ONE-A");
        Assert.assertEquals(original.getDescription(), "ONE-A");    }

    @Test
    public void cloneInstance() throws Exception {
        //Create
        List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "Test", "externalId", "TEST_1", "discontinued", "Y", "description", "Test2","active", "Y"));

        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String) catalogData.get("name"));
            catalogDTO.setCatalogId((String) catalogData.get("externalId"));
            catalogDTO.setActive((String) catalogData.get("active"));
            catalogDTO.setDiscontinued((String) catalogData.get("discontinued"));
            catalogDTO.setDescription((String) catalogData.get("description"));
            catalogDAO.insert(catalogDTO);

            //Clone catalog
            Catalog newCatalog = catalogService.get(catalogDTO.getCatalogId(), EXTERNAL_ID, false).orElse(null);
            Assert.assertTrue(newCatalog != null);
            Assert.assertTrue(newCatalog.diff(catalogDTO).isEmpty());

            Catalog catalogClone = catalogService.cloneInstance(newCatalog.getCatalogId(), EXTERNAL_ID, Entity.CloneType.LIGHT);
            Assert.assertTrue(catalogClone.getCatalogId() .equals(newCatalog.getCatalogId() + "_COPY") && catalogClone.getCatalogName().equals(newCatalog.getCatalogName() + "_COPY") && catalogClone.getActive() != newCatalog.getActive());
        });

    }
    @Test
    public void toMap() throws Exception {
        //Create new Instance
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("Test");
        catalogDTO.setExternalId("TEST");

        //Create map for checking
        Map<String, String> map = new HashMap<>();
        map.put("catalogName", "Test");
        map.put("externalId", "TEST");

        //checking map1 and map2
        Map<String, String> map1 = catalogDTO.toMap();
        Assert.assertEquals(map1.get("catalogName"), map.get("catalogName"));
        Assert.assertEquals(map1.get("externalId"), map.get("externalId"));
    }

    @Test
    public void equals() throws Exception {
    }

    @Test
    public void diff() throws Exception {
        //Create first instance
        Catalog catalog1 = new Catalog();
        catalog1.setCatalogName("test");
        catalog1.setCatalogId("test_1");
        catalog1.setDescription("test");

        //Create second instance
        Catalog catalog2 = new Catalog();
        catalog2.setCatalogName("test.com");
        catalog2.setCatalogId("test_1");
        catalog2.setDescription("test");

        //Checking first instance and second instance
        Map<String, Object> diff = catalog1.diff(catalog2);
        Assert.assertEquals(diff.size(), 2);
        Assert.assertEquals(diff.get("catalogName"), "test.com");

        //Checking first instance and second instance
        Map<String, Object> diff1 = catalog1.diff(catalog2, true);
        Assert.assertEquals(diff1.size(), 1);
        Assert.assertEquals(diff1.get("catalogName"), "test.com");
    }
    @After
    public void tearDown() throws Exception {
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }
}