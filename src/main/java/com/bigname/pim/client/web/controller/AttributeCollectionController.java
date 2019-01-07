package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.AttributeOption;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.util.FindBy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/attributeCollections")
public class AttributeCollectionController extends BaseController<AttributeCollection, AttributeCollectionService> {

    private AttributeCollectionService attributeCollectionService;

    public AttributeCollectionController(AttributeCollectionService attributeCollectionService) {
        super(attributeCollectionService, AttributeCollection.class);
        this.attributeCollectionService = attributeCollectionService;
    }

    /**
     * Handler method to load the list attributeCollection page
     *
     * @return The ModelAndView instance for the list attributeCollection page
     */
    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "ATTRIBUTE_COLLECTIONS");
        return new ModelAndView("settings/attributeCollections", model);
    }

    /**
     * Handler method to create a new attributeCollection
     *
     * @param attributeCollection The attributeCollection model attribute that needs to be created
     *
     * @return a map of model attributes
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create( AttributeCollection attributeCollection) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(attributeCollection, model, AttributeCollection.CreateGroup.class)) {
            attributeCollection.setActive("N");
            attributeCollectionService.create(attributeCollection);
            model.put("success", true);
        }
        return model;
    }

    /**
     * Handler method to update a attributeCollection instance
     *
     * @param id collectionId of the attributeCollection instance that needs to be updated
     * @param attributeCollection The modified website instance corresponding to the given collectionId
     *
     * @return a map of model attributes
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, AttributeCollection attributeCollection) {
        Map<String, Object> model = new HashMap<>();
        model.put("context", CollectionsUtil.toMap("id", id));
        if(isValid(attributeCollection, model, attributeCollection.getGroup().length == 1 && attributeCollection.getGroup()[0].equals("DETAILS") ? AttributeCollection.DetailsGroup.class : null)) {
            attributeCollectionService.update(id, FindBy.EXTERNAL_ID, attributeCollection);
            model.put("success", true);
        }
        return model;
    }

   /* @RequestMapping(value = "/{id}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggle(@PathVariable(value = "id") String id, @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", attributeCollectionService.toggle(id, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }*/

    /**
     * Handler method to load the attributeCollection details page or the create new attributeCollection page
     *
     * @param id collectionId of the attributeCollection instance that needs to be loaded
     *
     * @return The ModelAndView instance for the details page or create page depending on the presence of the 'id' pathVariable
     */
    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "ATTRIBUTE_COLLECTIONS");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "settings/attributeCollection");
        return id == null ? super.details(model) : attributeCollectionService.get(id, FindBy.EXTERNAL_ID, false)
                .map(attributeCollection -> {
                    model.put("attributeCollection", attributeCollection);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute Collection with Id: " + id));
    }

    @RequestMapping("/{id}/attribute")
    public ModelAndView attributeDetails(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("attribute", new Attribute());
        model.put("attributeGroups", attributeCollectionService.getAttributeGroupsIdNamePair(id, FindBy.EXTERNAL_ID, null));
        return new ModelAndView("settings/attribute", model);
    }

    @RequestMapping(value = "/{collectionId}/attribute", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttribute(@PathVariable(value = "collectionId") String id, Attribute attribute) {
        Map<String, Object> model = new HashMap<>();
        Optional<AttributeCollection> attributeCollection = attributeCollectionService.get(id, FindBy.EXTERNAL_ID, false);
        // TODO - cross field validation to see if one of attributeGroup ID and attributeGroup name is not empty
        if(attributeCollection.isPresent() && isValid(attribute, model)) {
            attributeCollection.get().setGroup("ATTRIBUTES");
            attributeCollection.get().setGroup(attribute.getGroup());
            attributeCollection.get().addAttribute(attribute);
            attributeCollectionService.update(id, FindBy.EXTERNAL_ID, attributeCollection.get());
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping("/{id}/attributes")
    @ResponseBody
    public Result<Map<String, String>> getAttributes(@PathVariable(value = "id") String id, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<Attribute> paginatedResult = attributeCollectionService.getAttributes(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping("/{collectionId}/attributes/{attributeId}/options/list")
    @ResponseBody
    public Result<Map<String, String>> getAttributeOptions(@PathVariable(value = "collectionId") String collectionId, @PathVariable(value = "attributeId") String attributeId, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<AttributeOption> paginatedResult = attributeCollectionService.getAttributeOptions(collectionId, FindBy.EXTERNAL_ID, attributeId, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping("/{collectionId}/attributes/{attributeId}/options")
    public ModelAndView attributeOptions(@PathVariable(value = "collectionId") String collectionId,
                                         @PathVariable(value = "attributeId") String attributeId) {
        Map<String, Object> model = new HashMap<>();
//        model.put("attributeCollectionId", collectionId);
        model.put("attributeId", attributeId);
        return new ModelAndView("settings/attributeOptions", model);
    }

    @RequestMapping(value = "/{collectionId}/attributes/{attributeId}/options", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttributeOptions(@PathVariable(value = "collectionId") String collectionId, AttributeOption attributeOption) {
        Map<String, Object> model = new HashMap<>();
        Optional<AttributeCollection> attributeCollection = attributeCollectionService.get(collectionId, FindBy.EXTERNAL_ID, false);
        if(attributeCollection.isPresent() && isValid(attributeOption, model)) {
            attributeCollection.get().setGroup("ATTRIBUTES");
            attributeCollection.get().addAttributeOption(attributeOption);
            attributeCollectionService.update(collectionId, FindBy.EXTERNAL_ID, attributeCollection.get());
            model.put("success", true);
        }
        return model;
    }
}
