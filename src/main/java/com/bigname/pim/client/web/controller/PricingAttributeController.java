package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.PricingAttribute;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.PricingAttributeService;
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
import java.util.Optional;

/**
 * Created by dona on 08-11-2018.
 */

@Controller
@RequestMapping("pim/pricingAttributes")
public class PricingAttributeController extends  BaseController<PricingAttribute,PricingAttributeService>{

    private PricingAttributeService pricingAttributeService;

    public PricingAttributeController(PricingAttributeService pricingAttributeService) {
        super(pricingAttributeService);
        this.pricingAttributeService = pricingAttributeService;
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRICING_ATTRIBUTES");
        return new ModelAndView("settings/pricingAttributes", model);
    }

    /**
     * Handler method to create a new pricingAttribute
     *
     * @param pricingAttribute The pricingAttribute model attribute that needs to be created
     *
     * @return a map of model attributes
     */

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create( PricingAttribute pricingAttribute) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(pricingAttribute, model, PricingAttribute.CreateGroup.class)) {
            pricingAttribute.setActive("N");
            pricingAttributeService.create(pricingAttribute);
            model.put("success", true);
        }
        return model;
    }

    /**
     * Handler method to update a pricingAttribute instance
     *
     * @param id pricingAttributeId of the pricingAttribute instance that needs to be updated
     * @param pricingAttribute The modified pricingAttribute instance corresponding to the given pricingAttributeId
     *
     * @return a map of model attributes
     */

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, PricingAttribute pricingAttribute) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(pricingAttribute, model, pricingAttribute.getGroup().length == 1 && pricingAttribute.getGroup()[0].equals("DETAILS") ? PricingAttribute.DetailsGroup.class : null)) {
            pricingAttributeService.update(id, FindBy.EXTERNAL_ID, pricingAttribute);
            model.put("success", true);
        }
        return model;
    }

    /**
     * Handler method to load the pricingAttribute details page or the create new pricingAttribute page
     *
     * @param id pricingAttributeId of the pricingAttribute instance that needs to be loaded
     *
     * @return The ModelAndView instance for the details page or create page depending on the presence of the 'id' pathVariable
     */
    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRICING_ATTRIBUTES");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("pricingAttribute", new PricingAttribute());
            model.put("breadcrumbs", new Breadcrumbs("PricingAttributes", "PricingAttributes", "/pim/pricingAttributes", "Create Pricing Attribute", ""));
        } else {
            Optional<PricingAttribute> pricingAttribute = pricingAttributeService.get(id, FindBy.EXTERNAL_ID, false);
            if(pricingAttribute.isPresent()) {
                model.put("mode", "DETAILS");
                model.put("pricingAttribute", pricingAttribute.get());
                model.put("breadcrumbs", new Breadcrumbs("PricingAttributes", "PricingAttributes", "/pim/pricingAttributes", pricingAttribute.get().getPricingAttributeName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find PricingAttribute with Id: " + id);
            }
        }
        return new ModelAndView("settings/pricingAttribute", model);
    }
}
