package com.bigname.pim.data.loader.exporter;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.util.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sruthi on 25-01-2019.
 */
@Component
public class CatalogExporter {
    @Autowired
    private CatalogService catalogService;

    public boolean exportData(String filePath) {
        List<Catalog> catalogData = catalogService.getAll(null,true);
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[]{"CATALOG_ID", "CATALOG_NAME", "DESCRIPTION", "ACTIVE", "DISCONTINUED", "ID" });
        int i=2;
        for (Iterator<Catalog> iter = catalogData.iterator(); iter.hasNext(); ) {
            Catalog element = iter.next();
            data.put(Integer.toString(i), new Object[]{element.getExternalId(), element.getCatalogName(), element.getDescription(), element.getActive(), element.getDiscontinued(), element.getId() });
            i++;
        }
        POIUtil.writeData(filePath, "catalog", data);
        return true;
    }
}
