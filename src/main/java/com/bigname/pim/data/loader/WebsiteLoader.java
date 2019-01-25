package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.util.POIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

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

    public boolean load(String filePath) {

        Set<Website> savableWebsites = new LinkedHashSet<>();
        Map<String, Integer> sequenceMap = new HashMap<>();
        Map<Integer, List<String>> skippedItems = new LinkedHashMap<>();
        List<List<String>> data = POIUtil.readData(filePath);

        Map<String, Website> websitesLookupMap = websiteService.getAll(null, false).stream().collect(Collectors.toMap(Website::getWebsiteId, e -> e));

        LOGGER.info("Categories to process -------------->"+  (data.size() - 1));
        LOGGER.info("# of category attributes-------------->"+data.get(0).size());

        List<String> attributeNamesMetadata = data.remove(0);
        for(int i = 0; i < data.size(); i ++) {
            LOGGER.info("----i---"+i);

            String websiteId = data.get(i).get(attributeNamesMetadata.indexOf("Website_Id")).toUpperCase();
            String name = data.get(i).get(attributeNamesMetadata.indexOf("Name"));
            String url = data.get(i).get(attributeNamesMetadata.indexOf("Url"));
            boolean skip = false;
            //Create the website is another one with the same WEBSITE_ID won't exists
            if(websitesLookupMap.containsKey(websiteId)) {
                //SKIP without updating
                skip = true;
            } else {
                Website website = new Website();
                website.setWebsiteId(websiteId);
                website.setWebsiteName(name);
                website.setUrl(url);
                website.setActive("Y");
                //Add this for batch saving
                savableWebsites.add(website);

                //Add this for checking duplicates in the feed
                websitesLookupMap.put(websiteId, website);
            }
            if(skip) {
                skippedItems.put(i, data.get(i));
            }
        }

        websiteDAO.saveAll(savableWebsites);
        LOGGER.info("skipped ---------->" + skippedItems.size());
        return true;
    }

}
