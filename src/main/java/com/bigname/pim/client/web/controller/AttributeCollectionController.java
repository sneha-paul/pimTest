package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.AttributeOption;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.m7.xtreme.common.datatable.model.Pagination;
import com.m7.xtreme.common.datatable.model.Request;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.datatable.model.SortOrder;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.PimUtil;
import com.m7.xtreme.common.util.StringUtil;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.web.controller.BaseController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;


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
     * Handler method to load the attributeCollection details page or the create new attributeCollection page
     *
     * @param id collectionId of the attributeCollection instance that needs to be loaded
     *
     * @return The ModelAndView instance for the details page or create page depending on the presence of the 'id' pathVariable
     */
    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload){


        Map<String, Object> model = new HashMap<>();
        model.put("active", "ATTRIBUTE_COLLECTIONS");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "settings/attributeCollection" + (reload ? "_body" : ""));

        return id == null ? super.details(model) : attributeCollectionService.get(ID.EXTERNAL_ID(id), false)
                .map(attributeCollection -> {
                    model.put("attributeCollection", attributeCollection);
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute Collection with Id: " + id));
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
    public Map<String, Object> create(AttributeCollection attributeCollection) {
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
     * @param   collectionId of the attributeCollection instance that needs to be updated
     * @param attributeCollection The modified website instance corresponding to the given collectionId
     *
     * @return a map of model attributes
     */

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String collectionId,
                                      AttributeCollection attributeCollection) {
        return update(collectionId, attributeCollection, "/pim/attributeCollections/", attributeCollection.getGroup().length == 1 && attributeCollection.getGroup()[0].equals("DETAILS") ? AttributeCollection.DetailsGroup.class : null);
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

    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request) {
        return all(request, "collectionName");
    }

    @RequestMapping(value= {"/{collectionId}/attributes/{attributeId}", "/{collectionId}/attributes/create"})
    public ModelAndView attributeDetails(@PathVariable(value = "collectionId") String collectionId,
                                         @PathVariable(value = "attributeId", required = false) String attributeId) {
        return attributeCollectionService.get(ID.EXTERNAL_ID(collectionId), false)
                .map(attributeCollection -> {
                    Map<String, Object> model = new HashMap<>();
                    Attribute attribute;
                    String mode;
                    if(isNotEmpty(attributeId)) {
                        mode = "DETAILS";
                        attribute = attributeCollection.getAllAttributes().stream()
                                .filter(attribute1 -> attribute1.getId().equals(attributeId)).findFirst()
                                .orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeId));
                        if(isNotEmpty(attribute.getParentAttributeId())) {
                            model.put("parentAttribute", attributeCollection.getAttribute(attribute.getParentAttributeId()).orElse(null));
                        }
                        model.put("breadcrumbs", new Breadcrumbs("Attribute Collections",
                                "Attribute Collections", "/pim/attributeCollections",
                                attributeCollection.getCollectionName(), "/pim/attributeCollections/" + attributeCollection.getCollectionId(),
                                "Attributes", "/pim/attributeCollections/" + attributeCollection.getCollectionId() + "#attributes",
                                attribute.getName(), ""));
                        model.put("collectionId", collectionId);
                    } else {
                        attribute = new Attribute();
                        mode = "CREATE";
                        model.put("attributeGroups", attributeCollectionService.getAttributeGroupsIdNamePair(ID.EXTERNAL_ID(collectionId), null));

                    }
                    model.put("attribute", attribute);
                    model.put("mode", mode);
                    model.put("uiTypes", Attribute.UIType.getAll());
                    model.put("parentAttributes", attributeCollection.getAvailableParentAttributes(attribute).stream().collect(CollectionsUtil.toLinkedMap(Attribute::getFullId, Attribute::getName)));
                    return new ModelAndView("settings/attribute", model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute Collection with Id: " + collectionId));

    }

    @RequestMapping(value = "/{collectionId}/attributes", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createAttribute(@PathVariable(value = "collectionId") String id, Attribute attribute) {
        Map<String, Object> model = new HashMap<>();
        Optional<AttributeCollection> attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(id), false);
        // TODO - cross field validation to see if one of attributeGroup ID and attributeGroup name is not empty
        if(attributeCollection.isPresent() && isValid(attribute, model)) {
            attributeCollection.get().setGroup("ATTRIBUTES");
            attributeCollection.get().addAttribute(attribute);
            attributeCollectionService.update(ID.EXTERNAL_ID(id), attributeCollection.get());
            model.put("success", true);
        }
        return model;
    }


    @RequestMapping(value = "/{collectionId}/attributes/{attributeId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> updateAttribute(@PathVariable(value = "collectionId") String collectionId,
                                               @PathVariable(value = "attributeId") String attributeId,
                                               @RequestParam Map<String, Object> parameterMap) {
        return attributeCollectionService.get(ID.EXTERNAL_ID(collectionId), false)
                .map(attributeCollection -> {
                    Map<String, Object> model = new HashMap<>();
                    String attributeFullId = (String)parameterMap.get("fullId");
                    return attributeCollection.getAttribute(attributeFullId)
                            .map(attribute -> {
                                attribute.setName((String)parameterMap.get("name"));
                                if(isNotEmpty(parameterMap.get("parentAttributeId"))) {
                                    attribute.setParentAttributeId((String)parameterMap.get("parentAttributeId"));
                                }
                                if(isNotEmpty(parameterMap.get("uiType"))) {
                                    attribute.setUiType(Attribute.UIType.get((String)parameterMap.get("uiType")));
                                }
                                if(isValid(attribute, model)) {
                                    attributeCollection.setGroup("ATTRIBUTES");
                                    attributeCollection.updateAttribute(attribute);
                                    attributeCollectionService.update(ID.EXTERNAL_ID(collectionId), attributeCollection);
                                    model.put("success", true);
                                }
                                return model;
                            }).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeFullId));
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute Collection with Id: " + collectionId));

    }



    @RequestMapping("/{collectionId}/attributes/data")
    @ResponseBody
    public Result<Map<String, String>> getAttributes(@PathVariable(value = "collectionId") String id, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<Attribute> paginatedResult = attributeCollectionService.getAttributes(ID.EXTERNAL_ID(id), pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping("/{collectionId}/attributes/{attributeId}/options/data")
    @ResponseBody
    public Result<Map<String, String>> getAttributeOptions(@PathVariable(value = "collectionId") String collectionId,
                                                           @PathVariable(value = "attributeId") String attributeId,
                                                           HttpServletRequest request) {

        if(PimUtil.isDataTableRequest(request)) {   // Datatable
            Request dataTableRequest = new Request(request);
            Pagination pagination = dataTableRequest.getPagination();
            Result<Map<String, String>> result = new Result<>();
            result.setDraw(dataTableRequest.getDraw());
            Sort sort = null;
            if (pagination.hasSorts()) {
                sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
            }
            List<Map<String, String>> dataObjects = new ArrayList<>();
            Page<AttributeOption> paginatedResult = attributeCollectionService.getAttributeOptions(ID.EXTERNAL_ID(collectionId), attributeId, pagination.getPageNumber(), pagination.getPageSize(), sort);
            paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
            result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
            return result;
        } else {    // Handsontable
            Result<Map<String, String>> result = new Result<>();
            List<Map<String, String>> dataObjects = new ArrayList<>();
            Page<AttributeOption> paginatedResult = attributeCollectionService.getAttributeOptions(ID.EXTERNAL_ID(collectionId), attributeId, 0, 300, null);
            paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
            return result;
        }
    }

    @RequestMapping(value= {"/{collectionId}/attributes/{attributeId}/options/{attributeOptionId}", "/{collectionId}/attributes/{attributeId}/options/create"})
    public ModelAndView attributeOptionDetails(@PathVariable(value = "collectionId") String collectionId,
                                         @PathVariable(value = "attributeId") String attributeId,
                                         @PathVariable(value = "attributeOptionId", required = false) String attributeOptionId) {

        return attributeCollectionService.get(ID.EXTERNAL_ID(collectionId), false)
            .map(attributeCollection -> {
                Map<String, Object> model = new HashMap<>();
                Attribute attribute = attributeCollection.getAllAttributes().stream()
                        .filter(attribute1 -> attribute1.getId().equals(attributeId)).findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeId));

                String mode;
                AttributeOption attributeOption;
                if(isNotEmpty(attributeOptionId)) {
                    mode = "DETAILS";
                    String optionFullId = StringUtil.getPipedValue(attribute.getFullId(), attributeOptionId);
                    attributeOption = attributeCollection.getAttributeOption(optionFullId).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute Option with Id: " + optionFullId));
                } else {
                    attributeOption = new AttributeOption();
                    mode = "CREATE";
                }
                model.put("mode", mode);
                model.put("attribute", attribute);
                model.put("attributeOption", attributeOption);
                if(isNotEmpty(attribute.getParentAttributeId())) {
                    model.put("parentAttributeOptions", attributeCollection.getAttribute(attribute.getParentAttributeId())
                            .map(parentAttribute -> parentAttribute.getOptions()
                                    .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparing(AttributeOption::getValue)))
                                    .collect(CollectionsUtil.toLinkedMap(e -> e.getValue().getFullId(), e -> e.getValue().getValue())))
                            .orElse(new HashMap<>()));
                }
                return new ModelAndView("settings/attributeOption", model);
            }).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute Collection with Id: " + collectionId));

    }


    @RequestMapping(value = "/{collectionId}/attributes/{attributeId}/attributeOptions", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createAttributeOption(@PathVariable(value = "collectionId") String collectionId,
                                                    AttributeOption attributeOptionDTO) {
        return attributeCollectionService.get(ID.EXTERNAL_ID(collectionId), false)
                .map(attributeCollection -> {
                    Map<String, Object> model = new HashMap<>();
                    if(isValid(attributeOptionDTO, model)) {
                        attributeCollection.setGroup("ATTRIBUTES");
                        attributeCollection.addAttributeOption(attributeOptionDTO);
                        attributeCollectionService.update(ID.EXTERNAL_ID(collectionId), attributeCollection);
                        model.put("success", true);
                    }
                    return model;
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute Collection with Id: " + collectionId));
    }

    @RequestMapping(value = "/{collectionId}/attributes/{attributeId}/attributeOptions/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> updateAttributeOption(@PathVariable(value = "collectionId") String collectionId,
                                                    AttributeOption attributeOptionDTO) {
        return attributeCollectionService.get(ID.EXTERNAL_ID(collectionId), false)
                .map(attributeCollection -> {
                    Map<String, Object> model = new HashMap<>();
                    if(isValid(attributeOptionDTO, model)) {
                        attributeCollection.setGroup("ATTRIBUTES");
                        attributeCollection.updateAttributeOption(attributeOptionDTO);
                        attributeCollectionService.update(ID.EXTERNAL_ID(collectionId), attributeCollection);
                        model.put("success", true);
                    }
                    return model;
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute Collection with Id: " + collectionId));
    }
}
