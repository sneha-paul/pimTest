package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.domain.User;
import com.bigname.pim.api.persistence.dao.CatalogDAO;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.util.POIUtil;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.common.util.ValidationUtil.isEmpty;

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

        Map<String, Catalog> catalogsLookupMap = catalogService.getAll(null, false).stream().collect(Collectors.toMap(Catalog::getId, e -> e));

        LOGGER.info("Catalogs to process -------------->"+  (data.size() - 1));
        LOGGER.info("# of catalog attributes-------------->"+data.get(0).size());

        List<String> attributeNamesMetadata = data.remove(0);

        for(int i = 0; i < data.size(); i ++) {
            String catalogId = data.get(i).get(attributeNamesMetadata.indexOf("CATALOG_ID")).toUpperCase();
            String name = data.get(i).get(attributeNamesMetadata.indexOf("NAME"));
            String internalId = data.get(i).get(attributeNamesMetadata.indexOf("Id"));
            String description = data.get(i).get(attributeNamesMetadata.indexOf("DESCRIPTION"));
            boolean skip = false;

            //Create the catalog is another one with the same CATALOG_ID won't exists
            if(catalogsLookupMap.containsKey(internalId)) {
                Catalog catalog1 = catalogsLookupMap.get(internalId);
                Catalog catalog = new Catalog();
                catalog.setCatalogId(catalogId);
                catalog.setCatalogName(name);
                catalog.setDescription(description);
                catalog.setActive("Y");
                catalog.setDiscontinued("N");

                if(!catalog1.equals(catalog)){

                }
                //SKIP without updating
                // skip = true;
            } else {

            }

        }

        //Skip the header row and process each category row.
        for(int i = 0; i < data.size(); i ++) {
            String catalogId = data.get(i).get(attributeNamesMetadata.indexOf("CATALOG_ID")).toUpperCase();
            String name = data.get(i).get(attributeNamesMetadata.indexOf("NAME"));
            String internalId = data.get(i).get(attributeNamesMetadata.indexOf("Id"));
            String description = data.get(i).get(attributeNamesMetadata.indexOf("DESCRIPTION"));
            boolean skip = false;

            //Create the catalog is another one with the same CATALOG_ID won't exists
            if(catalogsLookupMap.containsKey(internalId)) {
                Catalog catalog = catalogsLookupMap.get(internalId);
                catalog.setCatalogId(catalogId);
                catalog.setCatalogName(name);
                catalog.setDescription(description);
                catalog.setActive("Y");
                catalog.setDiscontinued("N");

                //update this for batch saving
                if (isValid(catalogService.validate(catalog, new HashMap<>(), Catalog.DetailsGroup.class))) {
                    catalog.setLastModifiedDateTime(LocalDateTime.now());
                    catalog.setLastModifiedUser(catalogService.getCurrentUser());
                    savableCatalogs.add(catalog);
                }
                //SKIP without updating
               // skip = true;
            } else {
                Catalog catalog = new Catalog();
                catalog.setCatalogId(catalogId);
                catalog.setCatalogName(name);
                catalog.setDescription(description);
                catalog.setActive("Y");
                catalog.setDiscontinued("N");

                //Add this for batch saving
                if (isValid(catalogService.validate(catalog, new HashMap<>(), Catalog.CreateGroup.class))) {
                    catalog.setCreatedDateTime(LocalDateTime.now());
                    catalog.setCreatedUser(catalogService.getCurrentUser());
                    savableCatalogs.add(catalog);
                }
                //Add this for checking duplicates in the feed
                catalogsLookupMap.put(catalogId, catalog);
            }
            if(skip) {
                skippedItems.put(i, data.get(i));
            }
        }
        catalogDAO.saveAll(savableCatalogs);
        LOGGER.info("skipped ---------->" + skippedItems.size());
        return true;
    }

    protected boolean isValid(Map<String, Pair<String, Object>> validationResult) {
        return isEmpty(validationResult.get("fieldErrors"));
    }

}
