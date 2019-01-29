package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.PricingAttribute;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.PricingAttributeService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bigname.common.util.ValidationUtil.isEmpty;

/**
 * Created by dona on 08-11-2018.
 */

@Controller
@RequestMapping("pim/pricingAttributes")
public class PricingAttributeController extends  BaseController<PricingAttribute,PricingAttributeService>{

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
    public Result<Map<String, String>> all(HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        if(isEmpty(dataTableRequest.getSearch())) {
            return super.all(request, response, model);
        } else {
            Pagination pagination = dataTableRequest.getPagination();
            Result<Map<String, String>> result = new Result<>();
            result.setDraw(dataTableRequest.getDraw());
            Sort sort;
            if(pagination.hasSorts()) {
                sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
            } else {
                sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
            }
            List<Map<String, String>> dataObjects = new ArrayList<>();
            Page<PricingAttribute> paginatedResult = pricingAttributeService.findAll("pricingAttributeName", dataTableRequest.getSearch(), new Pageable(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
            paginatedResult.forEach(e -> dataObjects.add(e.toMap()));
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
            result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
            return result;
        }
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
