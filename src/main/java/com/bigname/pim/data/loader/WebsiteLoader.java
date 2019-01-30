package com.bigname.pim.data.loader;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.WebsiteService;
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
import static com.bigname.pim.api.domain.Website.Property.*;


/**
 * Created by dona on 25-01-2019.
 */
@Component
public class WebsiteLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsiteLoader.class);

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private WebsiteDAO websiteDAO;

    private static List<String> header = Arrays.asList(ID.name(), WEBSITE_ID.name(), WEBSITE_NAME.name(), URL.name(), ACTIVE.name());

    public boolean load(String filePath) {

        Set<Website> newSavableWebsites = new LinkedHashSet<>();

        Set<Website> modifiedSavableWebsites = new LinkedHashSet<>();

       // Map<String, Integer> sequenceMap = new HashMap<>();

        Map<Integer, List<String>> skippedItems = new LinkedHashMap<>();

        List<List<String>> fullData = POIUtil.readData(filePath);

        List<List<String>> data = POIUtil.readData(filePath);

        Map<String, Website> websitesLookupMap = websiteService.getAll(null, false).stream().collect(Collectors.toMap(Website::getId, e -> e));

        //validate the header row

        List<String> metadataRow = data.size() > 0 ? data.remove(0) : new ArrayList<>();

        boolean validHeader = header.stream().allMatch(metadataRow::contains);

        if(!validHeader) {
            //TODO - throw invalid data exception
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
            return new Website(CollectionsUtil.toMap(WEBSITE_NAME, name, WEBSITE_ID, websiteId, ACTIVE, active, URL, url));
        }).collect(Collectors.toList());

        data.removeAll(modifiedWebsitesData);

        //Validate both new and modified websites

        //Validating new websites
        newWebsites.forEach(website -> {
            Map<String, Pair<String, Object>> validationResult = websiteService.validate(website, new HashMap<>(), Website.CreateGroup.class);
            if(!isValid(validationResult)) {
                // Build a map with Row index as the Key and proper validation error message as the value
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
            Map<String, Pair<String, Object>> validationResult = websiteService.validate(website, CollectionsUtil.toMap("id", website.getWebsiteId()), Website.CreateGroup.class, Website.DetailsGroup.class);
            if(!isValid(validationResult)) {
                // Build a map with Row index as the Key and proper validation error message as the value
            } else{
                website.setCreatedDateTime(LocalDateTime.now());
                website.setCreatedUser(websiteService.getCurrentUser());
                website.setActive("Y");
                //Add this for batch saving
                modifiedSavableWebsites.add(website);
                websiteDAO.saveAll(modifiedSavableWebsites);
            }
        });
        //If invalid websites and greater than 0, send the Map as response to the controller




        LOGGER.info("Websites to process -------------->"+  (data.size() - 1));
        LOGGER.info("# of websites attributes-------------->"+data.get(0).size());


       /* List<String> attributeNamesMetadata = data.remove(0);
        for(int i = 0; i < data.size(); i ++) {
            LOGGER.info("----i---"+i);

            String websiteId = data.get(i).get(attributeNamesMetadata.indexOf("WEBSITE_ID")).toUpperCase();
            String name = data.get(i).get(attributeNamesMetadata.indexOf("NAME"));
            String url = data.get(i).get(attributeNamesMetadata.indexOf("URL"));
            String internalId = data.get(i).get(attributeNamesMetadata.indexOf("ID"));
            boolean skip = false;
            //Create the website is another one with the same WEBSITE_ID won't exists
            if(websitesLookupMap.containsKey(internalId)) {
                //SKIP without updating
                Website website = websitesLookupMap.get(internalId);
                website.setWebsiteId(websiteId);
                website.setWebsiteName(name);
                website.setUrl(url);

                //update this for batch saving
                if (isValid(websiteService.validate(website, new HashMap<>(), Website.DetailsGroup.class))) {
                    website.setLastModifiedDateTime(LocalDateTime.now());
                    website.setLastModifiedUser(websiteService.getCurrentUser());
                    website.setActive("Y");
                    savableWebsites.add(website);
                }

            } else {
                Website website = new Website();
                website.setWebsiteId(websiteId);
                website.setWebsiteName(name);
                website.setUrl(url);
                if (isValid(websiteService.validate(website, new HashMap<>(), Website.CreateGroup.class))) {
                    website.setCreatedDateTime(LocalDateTime.now());
                    website.setCreatedUser(websiteService.getCurrentUser());
                    website.setActive("Y");
                    //Add this for batch saving
                    savableWebsites.add(website);
                }
                //Add this for checking duplicates in the feed
                websitesLookupMap.put(websiteId, website);
            }
            if(skip) {
                skippedItems.put(i, data.get(i));
            }
        }
*/
        websiteDAO.saveAll(newSavableWebsites);
        LOGGER.info("skipped ---------->" + skippedItems.size());
        return true;
    }

    protected boolean isValid(Map<String, Pair<String, Object>> validationResult) {
        return isEmpty(validationResult.get("fieldErrors"));
    }

}
