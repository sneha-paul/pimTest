package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

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

    @RequestMapping(value = "/{familyId}/attribute", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttribute(@PathVariable(value = "familyId") String id, FamilyAttribute attribute) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(id, FindBy.EXTERNAL_ID, false);
        // TODO - cross field validation to see if one of attributeGroup ID and FamilyAttributeGroup name is not empty
        if(family.isPresent() && isValid(attribute, model)) {
            family.get().addAttribute(attribute);
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

    @RequestMapping(value = "/{familyId}/attributes/{attributeId}/options", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttributeOptions(@PathVariable(value = "familyId") String familyId, FamilyAttributeOption attributeOption) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        if(family.isPresent() && isValid(attributeOption, model)) {
            family.get().addAttributeOption(attributeOption);
            familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
            model.put("success", true);
        }
        return model;
    }
}