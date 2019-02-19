package com.bigname.pim.api.persistence.dao;

import com.bigname.core.util.FindBy;
import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
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

import java.util.Optional;

/**
 * Created by dona on 19-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CatalogRepositoryTest {
    @Autowired
    CatalogDAO catalogDAO;

    @Before
    public void setUp() {
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }

    @Test
    public void createCatalogTest() {
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("Test1");
        catalogDTO.setCatalogId("TEST1");
        catalogDTO.setDescription("Test catalog");
        catalogDTO.setActive("Y");
        Catalog catalog = catalogDAO.insert(catalogDTO);
        Assert.assertTrue(catalog.diff(catalogDTO).isEmpty());

    }

    @Test
    public void retrieveCatalogTest() {
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("Test1");
        catalogDTO.setCatalogId("TEST1");
        catalogDTO.setDescription("Test catalog");
        catalogDTO.setActive("Y");
        catalogDAO.insert(catalogDTO);
        Optional<Catalog> catalog = catalogDAO.findByExternalId(catalogDTO.getCatalogId());
        Assert.assertTrue(catalog.isPresent());
        catalog = catalogDAO.findById(catalogDTO.getCatalogId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(catalog.isPresent());
        catalog = catalogDAO.findById(catalogDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(catalog.isPresent());
    }

    @Test
    public void updateCatalogTest() {
        Catalog catalogDTO = new Catalog();
        catalogDTO.setCatalogName("Test1");
        catalogDTO.setCatalogId("TEST1");
        catalogDTO.setDescription("Test catalog");
        catalogDTO.setActive("Y");
        catalogDAO.insert(catalogDTO);

        catalogDTO.setCatalogName("Test1Name");
        catalogDTO.setCatalogId("TEST1_ID");
        catalogDTO.setDescription("Test1 catalog description");
        catalogDTO.setGroup("DETAILS");
        catalogDTO.setActive("N");
        catalogDAO.save(catalogDTO);
        
        Optional<Catalog> catalog = catalogDAO.findByExternalId(catalogDTO.getCatalogId());
        Assert.assertTrue(catalog.isPresent());
        catalog = catalogDAO.findById(catalogDTO.getCatalogId(), FindBy.EXTERNAL_ID);
        Assert.assertTrue(catalog.isPresent());
        catalog = catalogDAO.findById(catalogDTO.getId(), FindBy.INTERNAL_ID);
        Assert.assertTrue(catalog.isPresent());
    }


    @After
    public void tearDown() {
        catalogDAO.getMongoTemplate().dropCollection(Catalog.class);
    }
}
