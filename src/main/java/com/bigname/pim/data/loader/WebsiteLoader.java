package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.service.WebsiteService;
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

import static com.bigname.pim.api.domain.Website.Property.*;
import static com.m7.xtreme.common.util.ValidationUtil.isEmpty;


/**
 * Created by dona on 25-01-2019.
 */
@Component
public class WebsiteLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsiteLoader.class);

    @Autowired
    private WebsiteService websiteService;

    private static List<String> header = Arrays.asList(ID.name(), WEBSITE_ID.name(), WEBSITE_NAME.name(), URL.name(), ACTIVE.name());

    public boolean load(String filePath) {

        List<Website> newSavableWebsites = new ArrayList<>();

        List<Website> modifiedSavableWebsites = new ArrayList<>();

        Map<Integer, List<String>> skippedItems = new LinkedHashMap<>();

        List<List<String>> fullData = POIUtil.readData(filePath);

        List<List<String>> data = new ArrayList<>(fullData);

        Map<String, Website> websitesLookupMap = websiteService.getAll(null, false).stream().collect(Collectors.toMap(Website::getId, e -> e));

        //validate the header row

        List<String> metadataRow = data.size() > 0 ? data.remove(0) : new ArrayList<>();

        boolean validHeader = header.stream().allMatch(metadataRow::contains);

        if(!validHeader) {
            //TODO - throw invalid data exception
            LOGGER.info("Invalid Headers");
            LOGGER.error("Invalid Header Array");
            return false;
        }

        //New websites
        List<List<String>> newWebsitesData = new ArrayList<>();

        List<Website> newWebsites = data.stream().filter(row -> isEmpty(row.get(metadataRow.indexOf(ID.name())))).map(row -> {

            String name = row.get(metadataRow.indexOf(WEBSITE_NAME.name()));
            String websiteId = row.get(metadataRow.indexOf(WEBSITE_ID.name())).toUpperCase();
            String active = row.get(metadataRow.indexOf(ACTIVE.name())).toUpperCase();
            String url = row.get(metadataRow.indexOf(URL.name())).toUpperCase();
            newWebsitesData.add(row);
            return new Website(CollectionsUtil.toMap(WEBSITE_NAME, name, WEBSITE_ID, websiteId, ACTIVE, active));

        }).collect(Collectors.toList());

        //Remove new websites from the list
        data.removeAll(newWebsitesData);

        // Websites with invalid internal Ids
        List<List<String>> invalidWebsites = data.stream().filter(row -> !websitesLookupMap.containsKey(row.get(metadataRow.indexOf(ID.name())))).collect(Collectors.toList());
        //Remove invalid websites from the list
        data.removeAll(invalidWebsites);

        List<List<String>> modifiedWebsitesData = new ArrayList<>();

        List<Website> modifiedWebsites = data.stream().filter(row -> {
            String internalId = row.get(metadataRow.indexOf(ID.name()));
            String name = row.get(metadataRow.indexOf(WEBSITE_NAME.name()));
            String websiteId = row.get(metadataRow.indexOf(WEBSITE_ID.name())).toUpperCase();
            String active = row.get(metadataRow.indexOf(ACTIVE.name())).toUpperCase();
            String url = row.get(metadataRow.indexOf(URL.name())).toUpperCase();

            return !websitesLookupMap.get(internalId).equals(CollectionsUtil.toMap(ID.name(), internalId, WEBSITE_NAME, name, WEBSITE_ID, websiteId, ACTIVE, active, URL, url));

        }).map(row -> {
            String name = row.get(metadataRow.indexOf(WEBSITE_NAME.name()));
            String websiteId = row.get(metadataRow.indexOf(WEBSITE_ID.name())).toUpperCase();
            String active = row.get(metadataRow.indexOf(ACTIVE.name())).toUpperCase();
            String url = row.get(metadataRow.indexOf(URL.name())).toUpperCase();
            modifiedWebsitesData.add(row);
            Website existingWebsite = websitesLookupMap.get(row.get(metadataRow.indexOf(ID.name())));
            existingWebsite.setWebsiteId(websiteId);
            existingWebsite.setWebsiteName(name);
            existingWebsite.setActive(active);
            existingWebsite.setUrl(url);
            return existingWebsite;
        }).collect(Collectors.toList());

        data.removeAll(modifiedWebsitesData);

        //Validate both new and modified websites

        //Validating new websites
        newWebsites.forEach(website -> {
            Map<String, Pair<String, Object>> validationResult = websiteService.validate(website, new HashMap<>(), Website.CreateGroup.class);
            if(!isValid(validationResult)) {
                // Build a map with Row index as the Key and proper validation error message as the value
                LOGGER.error("ERROR--newWebsites--->"+validationResult.values());
            } else{
                website.setCreatedDateTime(LocalDateTime.now());
                website.setCreatedUser(websiteService.getCurrentUser());
                website.setActive("Y");
                //Add this for batch saving
                newSavableWebsites.add(website);
            }
        });

        //Validating modified websites
        modifiedWebsites.forEach(website -> {
            Map<String, Pair<String, Object>> validationResult = websiteService.validate(website, CollectionsUtil.toMap("id", website.getWebsiteId()), Website.DetailsGroup.class);
            if(!isValid(validationResult)) {
                // Build a map with Row index as the Key and proper validation error message as the value
                LOGGER.error("ERROR--modifiedWebsites--->"+validationResult.values());
            } else{
                website.setLastModifiedDateTime(LocalDateTime.now());
                website.setLastModifiedUser(websiteService.getCurrentUser());
                website.setActive("Y");
                //Add this for batch saving
                modifiedSavableWebsites.add(website);
            }
        });
        //If invalid websites and greater than 0, send the Map as response to the controller

        LOGGER.info("Websites to process -------------->"+  (data.size() - 1));
       // LOGGER.info("# of websites attributes-------------->"+data.get(0).size());

        websiteService.create(newSavableWebsites);
        websiteService.update(modifiedSavableWebsites);
        LOGGER.info("skipped ---------->" + skippedItems.size());
        return true;
    }

    protected boolean isValid(Map<String, Pair<String, Object>> validationResult) {
        LOGGER.error("ERROR----->"+validationResult.values());
        return isEmpty(validationResult);
    }

}