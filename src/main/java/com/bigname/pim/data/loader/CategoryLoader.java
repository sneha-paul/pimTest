package com.bigname.pim.data.loader;

import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.RelatedCategory;
import com.bigname.pim.api.domain.RootCategory;
import com.bigname.pim.api.persistence.dao.CategoryDAO;
import com.bigname.pim.api.persistence.dao.RelatedCategoryDAO;
import com.bigname.pim.api.persistence.dao.RootCategoryDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.api.service.CategoryService;
import com.bigname.pim.util.POIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Component
public class CategoryLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryLoader.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private RelatedCategoryDAO relatedCategoryDAO;

    @Autowired
    private RootCategoryDAO rootCategoryDAO;

    public boolean load(String filePath) {

        Set<Category> savableCategories = new LinkedHashSet<>();
        Set<RelatedCategory> savableRelatedCategories = new LinkedHashSet<>();
        Set<RootCategory> savableRootCategories = new LinkedHashSet<>();

        Map<String, Integer> sequenceMap = new HashMap<>();

        Map<Integer, List<String>> skippedItems = new LinkedHashMap<>();

        List<List<String>> data = POIUtil.readData(filePath);

        Map<String, Catalog> catalogsLookupMap = catalogService.getAll(null, false).stream().collect(Collectors.toMap(Catalog::getCatalogId, e -> e));
        Map<String, Category> categoriesLookupMap = categoryService.getAll(null, false).stream().collect(Collectors.toMap(Category::getCategoryId, e -> e));
        Map<String, RelatedCategory> relatedCategoriesLookupMap = relatedCategoryDAO.findAll().stream().collect(Collectors.toMap( e -> e.getCategoryId() + "|" + e.getSubCategoryId(), e -> e));

        LOGGER.info("Categories to process -------------->"+  (data.size() - 1));
        LOGGER.info("# of category attributes-------------->"+data.get(0).size());

        List<String> attributeNamesMetadata = data.remove(0);
        // Sort categories data by PARENT_ID and NAME
        data.sort((c1, c2) -> {
            String pc1 = c1.get(attributeNamesMetadata.indexOf("PARENT_CATEGORY_ID"));
            String pc2 = c2.get(attributeNamesMetadata.indexOf("PARENT_CATEGORY_ID"));
            int l1 = ValidationUtil.isNotEmpty(pc1) ? pc1.split("\\|").length : 0;
            int l2 = ValidationUtil.isNotEmpty(pc2) ? pc2.split("\\|").length : 0;

            return c1.get(attributeNamesMetadata.indexOf("PARENT_CATEGORY_ID")).equals(c2.get(attributeNamesMetadata.indexOf("PARENT_CATEGORY_ID"))) ?
                    c1.get(attributeNamesMetadata.indexOf("CATEGORY_NAME")).compareTo(c2.get(attributeNamesMetadata.indexOf("CATEGORY_NAME"))) : l1 == l2 ?
                    c1.get(attributeNamesMetadata.indexOf("PARENT_CATEGORY_ID")).compareTo(c2.get(attributeNamesMetadata.indexOf("PARENT_CATEGORY_ID"))) : l1 - l2;
        });

        //Skip the header row and process each category row.
        for(int i = 0; i < data.size(); i ++) {
            String categoryId = data.get(i).get(attributeNamesMetadata.indexOf("CATEGORY_ID")).toUpperCase();
            String name = data.get(i).get(attributeNamesMetadata.indexOf("CATEGORY_NAME"));
            String parentId = data.get(i).get(attributeNamesMetadata.indexOf("PARENT_CATEGORY_ID")).toUpperCase();
            if(ValidationUtil.isNotEmpty(parentId)) {
                parentId = parentId.contains("|") ? parentId.substring(parentId.lastIndexOf("|") + 1) : parentId;
            }
            String description = data.get(i).get(attributeNamesMetadata.indexOf("DESCRIPTION"));
            String discontinued = data.get(i).get(attributeNamesMetadata.indexOf("DISCONTINUED"));
            String longDescription = data.get(i).get(attributeNamesMetadata.indexOf("LONG_DESCRIPTION"));
            String metaTitle = data.get(i).get(attributeNamesMetadata.indexOf("META_TITLE"));
            String metaDescription = data.get(i).get(attributeNamesMetadata.indexOf("META_DESCRIPTION"));
            String metaKeywords = data.get(i).get(attributeNamesMetadata.indexOf("META_KEYWORD"));
            String catalogIds = data.get(i).get(attributeNamesMetadata.indexOf("CATALOG_IDS"));
            boolean skip = false;
            //Create the category if another one with the same CATEGORY_ID won't exists
            if(categoriesLookupMap.containsKey(categoryId)) {
                //SKIP without updating
                skip = true;
            } else {
                Category category = new Category();
                category.setCategoryId(categoryId);
                category.setCategoryName(name);
                category.setDescription(description);
                category.setLongDescription(longDescription);
                category.setMetaTitle(metaTitle);
                category.setMetaDescription(metaDescription);
                category.setMetaKeywords(metaKeywords);
                if("Y".equals(discontinued)) {
                    category.setActive("N");
                    category.setDiscontinued("Y");
                } else {
                    category.setActive("Y");
                    category.setDiscontinued("N");
                }
                //Add this for batch saving
                savableCategories.add(category);

                //Add this for checking duplicates in the feed
                categoriesLookupMap.put(categoryId, category);

                if(!parentId.isEmpty()) {
                    if(!sequenceMap.containsKey(parentId)) {
                        sequenceMap.put(parentId, 0);
                    }
                    if(categoriesLookupMap.containsKey(parentId)) {
                        Category parentCategory = categoriesLookupMap.get(parentId);
                        if(relatedCategoriesLookupMap.containsKey(parentCategory.getId() + "|" + category.getId())) {
                            //Skip, subCategory already exists
                            skip = true;
                        } else {
                            int sequenceNum = sequenceMap.get(parentId);
                            RelatedCategory subCategory = new RelatedCategory(parentCategory.getId(), category.getId(), "", sequenceNum, 0);
                            if("Y".equals(parentCategory.getActive()) && "Y".equals(category.getActive())) {
                                subCategory.setActive("Y");
                            } else {
                                subCategory.setActive("N");
                            }
                            //Add this for batch saving
                            savableRelatedCategories.add(subCategory);

                            //Add this for checking duplicates in the feed
                            relatedCategoriesLookupMap.put(parentCategory.getId() + "|" + category.getId(), subCategory);
                            sequenceMap.put(parentId, sequenceNum + 1);
                        }
                    } else {
                        //Parent ID invalid, skip the subCategory mapping
                        skip = true;
                    }
                } else {
                    if(ValidationUtil.isNotEmpty(catalogIds)) {
                        for(String catalogId : catalogIds.split("\\|")) {
                            if(catalogsLookupMap.containsKey(catalogId)) {
                                if(!sequenceMap.containsKey("ROOT")) {
                                    sequenceMap.put("ROOT", 0);
                                }
                                RootCategory rootCategory = new RootCategory(catalogsLookupMap.get(catalogId).getId(), category.getId(), sequenceMap.get("ROOT"));
                                rootCategory.setActive("Y");
                                savableRootCategories.add(rootCategory);
                                sequenceMap.put("ROOT", sequenceMap.get("ROOT") + 1);
                            }
                        }
                    }
                }
            }
            if(skip) {
                skippedItems.put(i, data.get(i));
            }
        }
        categoryDAO.saveAll(savableCategories);
        relatedCategoryDAO.saveAll(savableRelatedCategories);
        rootCategoryDAO.saveAll(savableRootCategories);
        LOGGER.info("skipped ---------->" + skippedItems.size());
        return true;
    }
}
