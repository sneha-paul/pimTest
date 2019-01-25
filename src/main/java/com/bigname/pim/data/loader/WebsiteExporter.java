package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.Website;
import com.bigname.pim.api.persistence.dao.WebsiteDAO;
import com.bigname.pim.api.service.WebsiteService;
import com.bigname.pim.util.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by dona on 25-01-2019.
 */
@Component
public class WebsiteExporter {

    @Autowired
    private WebsiteService websiteService;

    @Autowired
    private WebsiteDAO websiteDAO;

    public boolean exportData(String filePath) {

        List<Website> websitesData = websiteService.getAll(null,true);
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[]{"Website Id", "Website Name", "Url", "Id"});
        int i=2;
        for (Iterator<Website> iter = websitesData.iterator(); iter.hasNext(); ) {
            Website element = iter.next();
            data.put(Integer.toString(i), new Object[]{element.getExternalId(), element.getWebsiteName(), element.getUrl(), element.getId()});
            i++;
        }
        POIUtil.writeData(filePath,"Website",data);

        return  true;
    }
}
