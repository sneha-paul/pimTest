package com.bigname.pim.api.domain;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.Entity;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.service.CatalogService;
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
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("test");
        catalogDTO.setCatalogId("test");
        catalogDTO.setDescription("test");

        catalogDTO.orchestrate();

        Assert.assertEquals(catalogDTO.getCatalogId(), "TEST");
        Assert.assertEquals(catalogDTO.getCatalogName(), "test");
        Assert.assertEquals(catalogDTO.getDescription(), "test");
        Assert.assertEquals(catalogDTO.getActive(), "N");

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
    public void equals() throws Exception {
    }

    @Test
    public void diff() throws Exception {
    }
    @After
    public void tearDown() throws Exception {
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }
}