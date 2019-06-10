package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.PricingAttribute;
import com.bigname.pim.api.service.PricingAttributeService;
import com.m7.common.datatable.model.Result;
import com.m7.xcore.exception.EntityNotFoundException;
import com.m7.xcore.util.FindBy;
import com.m7.xcore.web.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by dona on 08-11-2018.
 */

@Controller
@RequestMapping("pim/pricingAttributes")
public class PricingAttributeController extends BaseController<PricingAttribute,PricingAttributeService> {

    private PricingAttributeService pricingAttributeService;

    public PricingAttributeController(PricingAttributeService pricingAttributeService) {
        super(pricingAttributeService, PricingAttribute.class);
        this.pricingAttributeService = pricingAttributeService;
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRICING_ATTRIBUTES");
        return new ModelAndView("settings/pricingAttributes", model);
    }
    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request) {
        return all(request, "pricingAttributeName");
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
     * @param  pricingAttributeId of the pricingAttribute instance that needs to be updated
     * @param pricingAttribute The modified pricingAttribute instance corresponding to the given pricingAttributeId
     *
     * @return a map of model attributes
     */

 
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String pricingAttributeId, PricingAttribute pricingAttribute) {
        return update(pricingAttributeId, pricingAttribute, "/pim/pricingAttributes/", pricingAttribute.getGroup().length == 1 && pricingAttribute.getGroup()[0].equals("DETAILS") ? PricingAttribute.DetailsGroup.class : null);
    }


    /**
     * Handler method to load the pricingAttribute details page or the create new pricingAttribute page
     *
     * @param id pricingAttributeId of the pricingAttribute instance that needs to be loaded
     *
     * @return The ModelAndView instance for the details page or create page depending on the presence of the 'id' pathVariable
     */
    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id ,
                                @RequestParam(name = "reload", required = false) boolean reload){
        Map<String, Object> model = new HashMap<>();
        model.put("active", "PRICING_ATTRIBUTES");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "settings/pricingAttribute"  + (reload ? "_body" : ""));

        return id == null ? super.details(model) : pricingAttributeService.get(id, FindBy.EXTERNAL_ID, false)
                .map(pricingAttribute -> {
                    model.put("pricingAttribute", pricingAttribute);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find PricingAttribute with Id: " + id));
    }
}
