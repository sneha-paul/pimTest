package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Catalog;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.CatalogService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/catalogs")
public class CatalogController extends BaseController<Catalog, CatalogService>{

    private CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        super(catalogService);
        this.catalogService = catalogService;
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create( Catalog catalog) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(catalog, model, Catalog.CreateGroup.class)) {
            catalog.setActive("N");
            catalog.setDiscontinued("N");
            catalogService.create(catalog);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, Catalog catalog) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(catalog, model, catalog.getGroup().length == 1 && catalog.getGroup()[0].equals("DETAILS") ? Catalog.DetailsGroup.class : null)) {
            catalogService.update(id, FindBy.EXTERNAL_ID, catalog);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATALOGS");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("catalog", new Catalog());
            model.put("breadcrumbs", new Breadcrumbs("Catalogs", "Catalogs", "/pim/catalogs", "Create Catalog", ""));
            return new ModelAndView("catalog/catalog", model);
        } else {
            return catalogService.get(id, FindBy.findBy(true), false)
                 .map(catalog -> {
                     model.put("mode", "DETAILS");
                     model.put("catalog", catalog);
                     model.put("breadcrumbs", new Breadcrumbs("Catalogs", "Catalogs", "/pim/catalogs", catalog.getCatalogName(), ""));
                     return new ModelAndView("catalog/catalog", model);
                 }).orElseThrow(() -> new EntityNotFoundException("Unable to find Catalog with Id: " + id));
        }
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "CATALOGS");
        return new ModelAndView("catalog/catalogs", model);
    }
}
