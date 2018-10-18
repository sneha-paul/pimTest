package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/families")
public class FamilyController extends BaseController<Family, FamilyService> {

    private FamilyService familyService;
    private AttributeCollectionService collectionService;

    public FamilyController(FamilyService familyService, AttributeCollectionService collectionService) {
        super(familyService);
        this.familyService = familyService;
        this.collectionService = collectionService;
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "FAMILIES");
        return new ModelAndView("settings/families", model);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create( Family family) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(family, model, Family.CreateGroup.class)) {
            family.setActive("N");
            familyService.create(family);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> update(@PathVariable(value = "id") String id, Family family) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(family, model, family.getGroup().equals("DETAILS") ? Family.DetailsGroup.class : null)) {
            familyService.update(id, FindBy.EXTERNAL_ID, family);
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{id}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggle(@PathVariable(value = "id") String id, @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", familyService.toggle(id, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "FAMILIES");
        if(id == null) {
            model.put("mode", "CREATE");
            model.put("family", new Family());
            model.put("breadcrumbs", new Breadcrumbs("Families", "Families", "/pim/families", "Create Family", ""));
        } else {
            Optional<Family> family = familyService.get(id, FindBy.EXTERNAL_ID, false);
            if(family.isPresent()) {
                model.put("mode", "DETAILS");
                model.put("family", family.get());
                model.put("breadcrumbs", new Breadcrumbs("Families", "Families", "/pim/families", family.get().getFamilyName(), ""));
            } else {
                throw new EntityNotFoundException("Unable to find Family with Id: " + id);
            }
        }
        return new ModelAndView("settings/family", model);
    }

    @RequestMapping("/{id}/attribute")
    public ModelAndView attributeDetails(@PathVariable(value = "id") String id) {
        Map<String, Object> model = new HashMap<>();
        model.put("attributeCollections", collectionService.getAll(0, 100, null).getContent());
        model.put("attribute", new FamilyAttribute());
        model.put("attributeGroups", familyService.getAttributeGroupsIdNamePair(id, FindBy.EXTERNAL_ID, null));
        model.put("parentAttributeGroups", familyService.getParentAttributeGroupsIdNamePair(id, FindBy.EXTERNAL_ID, null));
        return new ModelAndView("settings/familyAttribute", model);
    }

    @RequestMapping("/{familyId}/variantGroups/{variantGroupId}/axisAttributes/available")
    public ModelAndView availableAxisAttributes(@PathVariable(value = "familyId") String familyId,
                                                @PathVariable(value = "variantGroupId") String variantGroupId) {
        Map<String, Object> model = new HashMap<>();
        model.put("familyId", familyId);
        model.put("variantGroupId", variantGroupId);
        return new ModelAndView("settings/availableVariantAxisAttributes", model);
    }

    @RequestMapping(value = {"/{familyId}/variantGroups/create", "/{familyId}/variantGroups/{variantGroupId}"})
    public ModelAndView variantGroupDetails(@PathVariable(value = "familyId") String familyId,
                                            @PathVariable(value = "variantGroupId", required = false) String variantGroupId) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        model.put("active", "FAMILIES");
        if(variantGroupId == null) {
            model.put("mode", "CREATE");
        } else {
            if(family.isPresent()) {
                model.put("mode", "DETAILS");
                model.put("familyId", familyId);
                model.put("variantGroup", family.get().getVariantGroups().get(variantGroupId));
                model.put("variantGroupAttributes", family.get().getVariantGroupAttributes(variantGroupId));
                model.put("breadcrumbs", new Breadcrumbs("Families",
                        "Families", "/pim/families",
                        family.get().getFamilyName(), "/pim/families/" + family.get().getFamilyId(),
                        "Variant Groups", "/pim/families/" + family.get().getFamilyId() + "#variantGroups",
                        family.get().getVariantGroups().get(variantGroupId).getName(), ""));
            }
        }
        return new ModelAndView("settings/variantGroup", model);
    }

    @RequestMapping("/{familyId}/variantGroups/{variantGroupId}/axisAttributes")
    @ResponseBody
    public Result<Map<String, String>> getVariantAxisAttributes(@PathVariable(value = "familyId") String familyId,
                                                                @PathVariable(value = "variantGroupId") String variantGroupId,
                                                                HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        List<FamilyAttribute> attributes = familyService.getVariantAxisAttributes(familyId, variantGroupId, FindBy.EXTERNAL_ID, sort);
        attributes.forEach(e -> dataObjects.add(e.toMap("AXIS_ATTRIBUTE")));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(attributes.size()));
        result.setRecordsFiltered(Long.toString(attributes.size()));
        return result;
    }

    @RequestMapping("/{familyId}/variantGroups/{variantGroupId}/axisAttributes/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableVariantAxisAttributes(@PathVariable(value = "familyId") String familyId,
                                                                @PathVariable(value = "variantGroupId") String variantGroupId,
                                                                HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        List<FamilyAttribute> attributes = familyService.getAvailableVariantAxisAttributes(familyId, variantGroupId, FindBy.EXTERNAL_ID, sort);
        attributes.forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(attributes.size()));
        result.setRecordsFiltered(Long.toString(attributes.size()));
        return result;
    }

    @RequestMapping(value = "/{familyId}/variantGroups/{variantGroupId}/axisAttributes/{attributeId}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addAxisAttribute(@PathVariable(value = "familyId") String familyId,
                                                @PathVariable(value = "variantGroupId") String variantGroupId,
                                                @PathVariable(value = "attributeId") String attributeId) {
        Map<String, Object> model = new HashMap<>();

        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        if(family.isPresent()) {
            FamilyAttribute axisAttribute = FamilyAttribute.findAttribute(attributeId, family.get().getAttributes());
            if(ValidationUtil.isNotEmpty(axisAttribute)) {
                Map<Integer, List<FamilyAttribute>> variantAxis = family.get().getVariantGroups().get(variantGroupId).getVariantAxis();
                if(ValidationUtil.isNotEmpty(variantAxis) && variantAxis.containsKey(1)) {
                    variantAxis.get(1).add(axisAttribute);
                } else {
                    variantAxis.put(1, Arrays.asList(axisAttribute));
                }
            }
            familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
            model.put("success", true);
        }

        return model;
    }

    @RequestMapping(value = "/{familyId}/variantGroups/{variantGroupId}/variantAttributes", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addAxisAttribute(@PathVariable(value = "familyId") String familyId,
                                                @PathVariable(value = "variantGroupId") String variantGroupId,
                                                HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();

        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        if(family.isPresent()) {
            family.get().updateVariantGroupAttributes(variantGroupId, request.getParameterValues("variantLevel1AttributeIds[]"), request.getParameterValues("variantLevel2AttributeIds[]"));
            familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
            model.put("success", true);
        }

        return model;
    }

    @RequestMapping(value = "/{familyId}/variantGroups/{variantGroupId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveVariantGroup(@PathVariable(value = "familyId") String familyId,
                                                @PathVariable(value = "variantGroupId") String variantGroupId,
                                                VariantGroup variantGroup) {
        Map<String, Object> model = new HashMap<>();
        if(isValid(variantGroup, model, VariantGroup.DetailsGroup.class)) {
            Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
            if(family.isPresent()) {
                family.get().getVariantGroups().get(variantGroupId).merge(variantGroup);
                familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
                model.put("success", true);
            }
        }
        return model;
    }


    @RequestMapping(value = "/{familyId}/variantGroups", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createVariantGroup(@PathVariable(value = "familyId") String familyId, VariantGroup variantGroup) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        if(family.isPresent() && isValid(variantGroup, model, VariantGroup.CreateGroup.class)) { // TODO - check if another group with the same id/name exists
            variantGroup.setActive("N");
            family.get().addVariantGroup(variantGroup);
            familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{familyId}/attribute", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttribute(@PathVariable(value = "familyId") String id, FamilyAttribute familyAttribute) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(id, FindBy.EXTERNAL_ID, false);
        // TODO - cross field validation to see if one of attributeGroup ID and FamilyAttributeGroup name is not empty
        if(family.isPresent() /*&& isValid(familyAttribute, model)*/) {
            Attribute attribute = collectionService.findAttribute(familyAttribute.getCollectionId(), FindBy.EXTERNAL_ID, familyAttribute.getAttributeId()).get();
            familyAttribute.setAttribute(attribute);
            family.get().addAttribute(familyAttribute);
            familyService.update(id, FindBy.EXTERNAL_ID, family.get());
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping("/{id}/attributes")
    @ResponseBody
    public Result<Map<String, String>> getFamilyAttributes(@PathVariable(value = "id") String id, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<FamilyAttribute> paginatedResult = familyService.getFamilyAttributes(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping("/{familyId}/attributes/{attributeId}/options/list")
    @ResponseBody
    public Result<Map<String, String>> getFamilyAttributeOptions(@PathVariable(value = "familyId") String familyId, @PathVariable(value = "attributeId") String attributeId, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<FamilyAttributeOption> paginatedResult = familyService.getFamilyAttributeOptions(familyId, FindBy.EXTERNAL_ID, attributeId, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping("/{familyId}/attributes/{attributeId}/options")
    public ModelAndView attributeOptions(@PathVariable(value = "familyId") String familyId,
                                         @PathVariable(value = "attributeId") String attributeId) {
        Map<String, Object> model = new HashMap<>();
//        model.put("familyId", familyId);
        model.put("attributeId", attributeId);
        return new ModelAndView("settings/familyAttributeOptions", model);
    }

    @RequestMapping("/{familyId}/attributes/{attributeId}/options/available")
    public ModelAndView availableAttributeOptions(@PathVariable(value = "familyId") String familyId,
                                         @PathVariable(value = "attributeId") String attributeId) {
        Map<String, Object> model = new HashMap<>();
//        model.put("familyId", familyId);
        model.put("attributeId", attributeId);
        return new ModelAndView("settings/availableFamilyAttributeOptions", model);
    }

    @RequestMapping("/{familyId}/attributes/{attributeId}/options/available/list")
    @ResponseBody
    public Result<Map<String, String>> getAvailableFamilyAttributeOptions(@PathVariable(value = "familyId") String familyId,
                                                                          @PathVariable(value = "attributeId") String familyAttributeId,
                                                                          HttpServletRequest request) {
        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        Result<Map<String, String>> result = new Result<>();
        if(family.isPresent()) {
            Request dataTableRequest = new Request(request);
            Pagination pagination = dataTableRequest.getPagination();
            result.setDraw(dataTableRequest.getDraw());
            Sort sort = null;
            if(pagination.hasSorts()) {
                sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
            }
            List<Map<String, String>> dataObjects = new ArrayList<>();
            List<FamilyAttribute> familyAttribute = FamilyAttributeGroup.getAllAttributes(family.get().getAttributes()).stream().filter(e -> e.getFullId().equals(familyAttributeId)).collect(Collectors.toList());
            if(ValidationUtil.isNotEmpty(familyAttribute)) {
                Map<String, FamilyAttributeOption> familyAttributeOptions = familyAttribute.get(0).getOptions();
                collectionService.findAttribute(familyAttribute.get(0).getCollectionId(), FindBy.EXTERNAL_ID, familyAttribute.get(0).getAttributeId()).ifPresent(attribute -> {
                    Map<String, AttributeOption> optionsMap = attribute.getOptions();
                    List<AttributeOption> optionsList = optionsMap.entrySet().stream().filter(e -> !familyAttributeOptions.keySet().contains(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
                    //TODO - sorting and pagination
                    Page<AttributeOption> paginatedResult = new PageImpl<>(optionsList);
                    paginatedResult.getContent().forEach(e -> {
                        e.setCollectionId(familyAttribute.get(0).getCollectionId());
                        dataObjects.add(e.toMap());
                    });
                    result.setDataObjects(dataObjects);
                    result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
                    result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
                });
            }
        }
        return result;
    }

    @RequestMapping(value = "/{familyId}/attributes/{familyAttributeId}/options/{attributeOptionId}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveAttributeOptions(@PathVariable(value = "familyId") String familyId, FamilyAttributeOption familyAttributeOption) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        if(family.isPresent() && isValid(familyAttributeOption, model, FamilyAttributeOption.AddOptionGroup.class)) {
            Optional<AttributeOption> attributeOption = collectionService.findAttributeOption(familyAttributeOption.getAttributeOptionId());
            if(attributeOption.isPresent()) {
                family.get().addAttributeOption(familyAttributeOption, attributeOption.get());
                familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
                model.put("success", true);
            }
        }
        return model;
    }

    @RequestMapping("/{id}/variantGroups/list")
    @ResponseBody
    public Result<Map<String, String>> getVariantGroups(@PathVariable(value = "id") String id, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, String>> dataObjects = new ArrayList<>();
        Page<VariantGroup> paginatedResult = familyService.getVariantGroups(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }
}