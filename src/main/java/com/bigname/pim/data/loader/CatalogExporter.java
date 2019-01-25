package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.util.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Created by sruthi on 25-01-2019.
 */
@Component
public class CatalogExporter {
    @Autowired
    private CatalogService catalogService;

    public boolean exportData(String filePath) {
        Map<String, Catalog> catalogLookupMap = catalogService.getAll(null, false).stream().collect(Collectors.toMap(Catalog::getId, e -> e));
        //Map<String, WebsiteCatalog> websiteCatalogLookupMap = websiteCatalogDAO.findAll().stream().collect(Collectors.toMap(e -> e.getId(), e->e));
        Map<String, Object[]> data = new TreeMap<>();
        data.put("1", new Object[]{ "CATALOG_ID", "NAME","WEBSITE_ID", "DESCRIPTION"});
        int i=2;
        /*for (Iterator<Map.Entry<String,WebsiteCatalog>> iter = websiteCatalogLookupMap.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, WebsiteCatalog> element = iter.next();
            String catalogKey = element.getKey();
            WebsiteCatalog websitecatalogData = websiteCatalogLookupMap.get(catalogKey);
            String catalogId=websitecatalogData.getCatalogId();
            String websiteId=websitecatalogData.getWebsiteId();
            if(catalogLookupMap.containsKey(catalogId))
            {
                Catalog catalogData = catalogLookupMap.get(catalogId);
                data.put(Integer.toString(i), new Object[]{catalogData.getExternalId(),catalogData.getCatalogName(),websiteId,catalogData.getDescription()});
            }
            i++;
        }
*/
        POIUtil.writeData(filePath,"catalog",data);
        return true;
    }
}
