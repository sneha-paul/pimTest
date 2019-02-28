package com.bigname.pim.api.service.impl;

import com.bigname.pim.PimApplication;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.RootCategoryDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by sruthi on 23-02-2019.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class CatalogServiceImplTest {

    @Autowired
    CatalogService catalogService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    RootCategoryDAO rootCategoryDAO;
    @Autowired
    CatalogDAO catalogDAO;
    @Autowired
    CategoryDAO categoryDAO;

    @Before
    public void setUp() throws Exception { catalogDAO.getMongoTemplate().dropCollection(Catalog.class); }

    @Test
    public void findAllRootCategories() throws Exception {

        /*List<Map<String, Object>> catalogsData = new ArrayList<>();
        catalogsData.add(CollectionsUtil.toMap("name", "TestCatalog 1", "externalId", "TESTCatalog_1", "description", "TESTCatalog_1description", "active", "Y"));
        catalogsData.forEach(catalogData -> {
            Catalog catalogDTO = new Catalog();
            catalogDTO.setCatalogName((String)catalogData.get("name"));
            catalogDTO.setCatalogId((String)catalogData.get("externalId"));
            catalogDTO.setActive((String)catalogData.get("active"));
            catalogDTO.setDescription((String)catalogData.get("description"));
            catalogService.create(catalogDTO);
        });

        Catalog catalog = catalogService.get(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID,false).orElse(null);

        List<Map<String, Object>> categoriesData = new ArrayList<>();
        categoriesData.add(CollectionsUtil.toMap("name", "Test1", "externalId", "TEST_1", "description", "TEST_1description", "active", "Y"));
        categoriesData.add(CollectionsUtil.toMap("name", "Test2", "externalId", "TEST_2", "description", "TEST_2description", "active", "Y"));

        categoriesData.forEach(categoryData -> {
            Category categoryDTO = new Category();
            categoryDTO.setCategoryName((String)categoryData.get("name"));
            categoryDTO.setCategoryId((String)categoryData.get("externalId"));
            categoryDTO.setActive((String)categoryData.get("active"));
            categoryDTO.setDescription((String)categoryData.get("description"));
            categoryService.create(categoryDTO);

            Category category = categoryService.get((String)categoryData.get("externalId"), FindBy.EXTERNAL_ID,false).orElse(null);

            RootCategory rootCategory = new RootCategory();
            rootCategory.setCatalogId(catalog.getId());
            rootCategory.setRootCategoryId(category.getId());
            rootCategory.setSequenceNum(0);
            rootCategory.setSubSequenceNum(0);
            rootCategory.setActive(category.getActive());
            rootCategoryDAO.insert(rootCategory);
        });

        Page<Map<String, Object>> rootCategories =  catalogService.findAllRootCategories(catalogsData.get(0).get("externalId").toString(), FindBy.EXTERNAL_ID, "categoryName", "Test", PageRequest.of(0,categoriesData.size(),null), false);
        Assert.assertEquals(rootCategories.getSize(),categoriesData.size());

        categoryDAO.getMongoTemplate().dropCollection(Category.class);*/

    }

    @Test
    public void getAllRootCategories() throws Exception {
    }

    @Test
    public void findAvailableRootCategoriesForCatalog() throws Exception {
    }

    @Test
    public void getAvailableRootCategoriesForCatalog() throws Exception {
    }

    @Test
    public void toggleRootCategory() throws Exception {
    }

    @Test
    public void getRootCategories() throws Exception {
    }

    @Test
    public void getCategoryHierarchy() throws Exception {
    }

    @Test
    public void setRootCategorySequence() throws Exception {
    }

    @Test
    public void addRootCategory() throws Exception {
    }

    @After
    public void tearDown() throws Exception { catalogDAO.getMongoTemplate().dropCollection(Catalog.class); }

}