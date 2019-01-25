package com.bigname.pim.client.web.controller;

import com.bigname.pim.data.loader.CatalogExporter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sruthi on 25-01-2019.
 */
@Controller
@RequestMapping("pim/export")
public class ExportController {
    private CatalogExporter catalogExporter;

    public ExportController( CatalogExporter catalogExporter) {
        this.catalogExporter = catalogExporter;
    }

    @RequestMapping(value = "/catalogs" , method = RequestMethod.GET)
    public Map<String, Object> exportCatalogData(HttpServletRequest request) {
        Map<String, Object> model =new HashMap<>();
        catalogExporter.exportData(  "C:\\Users\\Documents\\excel\\catalogData.xlsx");

        return model;
    }
}
