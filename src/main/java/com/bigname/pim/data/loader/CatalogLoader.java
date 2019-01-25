package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.util.POIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sruthi on 25-01-2019.
 */
@Component
public class CatalogLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogLoader.class);

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private CatalogDAO catalogDAO;


    public boolean load(String filePath) {

        Set<Catalog> savableCatalogs = new LinkedHashSet<>();

      //  Map<String, Integer> sequenceMap = new HashMap<>();

        Map<Integer, List<String>> skippedItems = new LinkedHashMap<>();

        List<List<String>> data = POIUtil.readData(filePath);

        Map<String, Catalog> categoriesLookupMap = catalogService.getAll(null, false).stream().collect(Collectors.toMap(Catalog::getCatalogId, e -> e));

        LOGGER.info("Catalogs to process -------------->"+  (data.size() - 1));
        LOGGER.info("# of catalog attributes-------------->"+data.get(0).size());

        List<String> attributeNamesMetadata = data.remove(0);
        // Sort categories data by PARENT_ID and NAME
       /* data.sort((c1, c2) ->
                c1.get(attributeNamesMetadata.indexOf("PARENT_ID")).equals(c2.get(attributeNamesMetadata.indexOf("PARENT_ID"))) ?
                        c1.get(attributeNamesMetadata.indexOf("NAME")).compareTo(c2.get(attributeNamesMetadata.indexOf("NAME"))) : c1.get(attributeNamesMetadata.indexOf("PARENT_ID")).compareTo(c2.get(attributeNamesMetadata.indexOf("PARENT_ID"))));
*/
        //Skip the header row and process each category row.
        for(int i = 0; i < data.size(); i ++) {
            LOGGER.info("----i---"+i);
            String catalogId = data.get(i).get(attributeNamesMetadata.indexOf("CATALOG_ID")).toUpperCase();
            String name = data.get(i).get(attributeNamesMetadata.indexOf("NAME"));
            //String parentId = data.get(i).get(attributeNamesMetadata.indexOf("PARENT_ID")).toUpperCase();
            String description = data.get(i).get(attributeNamesMetadata.indexOf("DESCRIPTION"));
            boolean skip = false;
            //Create the category is another one with the same CATEGORY_ID won't exists
            if(categoriesLookupMap.containsKey(catalogId)) {
                //SKIP without updating
                skip = true;
            } else {
                Catalog catalog = new Catalog();
                catalog.setCatalogId(catalogId);
                catalog.setCatalogName(name);
                catalog.setDescription(description);
                catalog.setActive("Y");
                catalog.setDiscontinued("N");
                //Add this for batch saving
                savableCatalogs.add(catalog);

                //Add this for checking duplicates in the feed
                categoriesLookupMap.put(catalogId, catalog);

                /*if(!parentId.isEmpty()) {
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
                            subCategory.setActive("Y");
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
                }*/
            }
            if(skip) {
                skippedItems.put(i, data.get(i));
            }
        }
        catalogDAO.saveAll(savableCatalogs);
        LOGGER.info("skipped ---------->" + skippedItems.size());
        return true;
    }

}
