package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.util.POIUtil;
import com.m7.xtreme.common.util.CollectionsUtil;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.pim.api.domain.Catalog.Property.*;
import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;

/**
 * Created by sruthi on 25-01-2019.
 */
@Component
public class CatalogLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogLoader.class);

    @Autowired
    private CatalogService catalogService;

    private static List<String> header = Arrays.asList(CATALOG_ID.name(), CATALOG_NAME.name(), DESCRIPTION.name(), ACTIVE.name(), DISCONTINUED.name(), ID.name());

    public boolean load(String filePath) {

        List<Catalog> newSavableCatalogs = new ArrayList<>();

        List<Catalog> modifiedSavableCatalogs = new ArrayList<>();

        Map<Integer, List<String>> skippedItems = new LinkedHashMap<>();

        List<List<String>> fullData = POIUtil.readData(filePath);

        List<List<String>> data = new ArrayList<>(fullData);

        Map<String, Catalog> catalogsLookupMap = catalogService.getAll(null, false).stream().collect(Collectors.toMap(Catalog::getId, e -> e));

        //validate the header row

        List<String> metadataRow = data.size() > 0 ? data.remove(0) : new ArrayList<>();

        boolean validHeader = header.stream().allMatch(metadataRow::contains);

        if(!validHeader) {
            //TODO - throw invalid data exception
            LOGGER.info("Invalid Headers");
            LOGGER.error("Invalid Header Array");
            return false;
        }

        //New catalogs
        List<List<String>> newCatalogsData = new ArrayList<>();

        List<Catalog> newCatalogs = data.stream().filter(row -> isEmpty(row.get(metadataRow.indexOf(ID.name())))).map(row -> {

            String name = row.get(metadataRow.indexOf(CATALOG_NAME.name()));
            String catalogId = row.get(metadataRow.indexOf(CATALOG_ID.name())).toUpperCase();
            String active = row.get(metadataRow.indexOf(ACTIVE.name())).toUpperCase();
            String discontinued = row.get(metadataRow.indexOf(DISCONTINUED.name())).toUpperCase();
            String description = row.get(metadataRow.indexOf(DESCRIPTION.name()));
            newCatalogsData.add(row);
            return new Catalog(CollectionsUtil.toMap(CATALOG_NAME, name, CATALOG_ID, catalogId, ACTIVE, active, DISCONTINUED, discontinued, DESCRIPTION, description));

        }).collect(Collectors.toList());

        //Remove new catalogs from the list
        data.removeAll(newCatalogsData);

        // Catalogs with invalid internal Ids
        List<List<String>> invalidCatalogs = data.stream().filter(row -> !catalogsLookupMap.containsKey(row.get(metadataRow.indexOf(ID.name())))).collect(Collectors.toList());
        //Remove invalidCatalogs from the list
        data.removeAll(invalidCatalogs);

        //Modified catalogs
        List<List<String>> modifiedCatalogsData = new ArrayList<>();

        List<Catalog> modifiedCatalogs = data.stream().filter(row -> {
            String internalId = row.get(metadataRow.indexOf(ID.name()));
            String name = row.get(metadataRow.indexOf(CATALOG_NAME.name()));
            String catalogId = row.get(metadataRow.indexOf(CATALOG_ID.name())).toUpperCase();
            String active = row.get(metadataRow.indexOf(ACTIVE.name())).toUpperCase();
            String discontinued = row.get(metadataRow.indexOf(DISCONTINUED.name())).toUpperCase();
            String description = row.get(metadataRow.indexOf(DESCRIPTION.name()));

            return !catalogsLookupMap.get(internalId).equals(CollectionsUtil.toMap(ID.name(), internalId, CATALOG_NAME, name, CATALOG_ID, catalogId, ACTIVE, active, DISCONTINUED, discontinued, DESCRIPTION, description));

        }).map(row -> {
            String catalogId = row.get(metadataRow.indexOf(CATALOG_ID.name())).toUpperCase();
            String name = row.get(metadataRow.indexOf(CATALOG_NAME.name()));
            String active = row.get(metadataRow.indexOf(ACTIVE.name())).toUpperCase();
            String discontinued = row.get(metadataRow.indexOf(DISCONTINUED.name())).toUpperCase();
            String description = row.get(metadataRow.indexOf(DESCRIPTION.name()));
            modifiedCatalogsData.add(row);
            Catalog existingCatalog = catalogsLookupMap.get(row.get(metadataRow.indexOf(ID.name())));
            existingCatalog.setCatalogId(catalogId);
            existingCatalog.setCatalogName(name);
            existingCatalog.setActive(active);
            existingCatalog.setDiscontinued(discontinued);
            existingCatalog.setDescription(description);
            return existingCatalog;
        }).collect(Collectors.toList());

        data.removeAll(modifiedCatalogsData);

        //Validate both new and modified catalogs

        //Validating new catalogs
        newCatalogs.forEach(catalog -> {
            Map<String, Pair<String, Object>> validationResult = catalogService.validate(catalog, new HashMap<>(), Catalog.CreateGroup.class);
            if(!isValid(validationResult)) {
                // Build a map with Row index as the Key and proper validation error message as the value
                LOGGER.error("ERROR--newCatalogs--->"+validationResult.values());
            }
            else{
                    catalog.setCreatedDateTime(LocalDateTime.now());
                    catalog.setCreatedUser(catalogService.getCurrentUser());
                    newSavableCatalogs.add(catalog);
            }
        });

        //Validating modified catalogs
        modifiedCatalogs.forEach(catalog -> {
            Map<String, Pair<String, Object>> validationResult = catalogService.validate(catalog, CollectionsUtil.toMap("id", catalog.getCatalogId()), Catalog.DetailsGroup.class);
            if(!isValid(validationResult)) {
                LOGGER.error("ERROR--modifiedCatalogs-->"+validationResult.values());
                // Build a map with Row index as the Key and proper validation error message as the value
            }else{
                    catalog.setLastModifiedDateTime(LocalDateTime.now());
                    catalog.setLastModifiedUser(catalogService.getCurrentUser());
                    modifiedSavableCatalogs.add(catalog);
            }
        });
        //If invalid catalogs and greater than 0, send the Map as response to the controller

        LOGGER.info("Catalogs to process -------------->"+  (data.size() - 1));
      //  LOGGER.info("# of catalog attributes-------------->"+data.get(0).size());

        catalogService.create(newSavableCatalogs);
        catalogService.update(modifiedSavableCatalogs);
        LOGGER.info("skipped ---------->" + skippedItems.size());
        return true;
    }

    protected boolean isValid(Map<String, Pair<String, Object>> validationResult) {
        LOGGER.error("ERROR----->"+validationResult.values());
        return isEmpty(validationResult);
    }

}