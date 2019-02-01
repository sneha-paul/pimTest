package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.exception.EntityNotFoundException;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.api.service.ChannelService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.common.util.ValidationUtil.isEmpty;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/families")
public class FamilyController extends BaseController<Family, FamilyService> {

    private FamilyService familyService;
    private AttributeCollectionService collectionService;
    private ChannelService channelService;

    public FamilyController(FamilyService familyService, AttributeCollectionService collectionService, ChannelService channelService) {
        super(familyService, Family.class, collectionService, channelService);
        this.familyService = familyService;
        this.collectionService = collectionService;
        this.channelService = channelService;
    }

    @RequestMapping()
    public ModelAndView all() {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "FAMILIES");
        return new ModelAndView("settings/families", model);
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
            Page<Family> paginatedResult = familyService.findAll("familyName", dataTableRequest.getSearch(), PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), sort), false);
            paginatedResult.forEach(e -> dataObjects.add(e.toMap()));
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
            result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
            return result;
        }
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
    public Map<String, Object> update(@PathVariable(value = "id") String familyId, Family family) {
        return update(familyId, family, "/pim/families/", family.getGroup().length == 1 && family.getGroup()[0].equals("DETAILS") ? Family.DetailsGroup.class : null);
    }


    /*@RequestMapping(value = "/{id}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggle(@PathVariable(value = "id") String id, @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", familyService.toggle(id, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }*/

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "FAMILIES");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "settings/family"  + (reload ? "_body" : ""));
       return id == null ? super.details(model) : familyService.get(id, FindBy.EXTERNAL_ID, false)
               .map(family -> {
                   model.put("family", family);
                   model.put("channels", ConversionUtil.toJSONString(channelService.getAll(0, 100, null).getContent().stream().collect(Collectors.toMap(Channel::getChannelId, Channel::getChannelName))));
                   return super.details(id, model);
               }).orElseThrow(() -> new EntityNotFoundException("Unable to find Family with Id: " + id));
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
                                            @PathVariable(value = "variantGroupId", required = false) String variantGroupId,
                                            @RequestParam(name = "reload", required = false) boolean reload) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        model.put("active", "FAMILIES");
        if(variantGroupId == null) {
            model.put("mode", "CREATE");
            model.put("channels", channelService.getAll(0, 100, null).stream().collect(Collectors.toMap(Channel::getChannelId, Channel::getChannelName))); //TODO - replace with a separate service method
        } else {
            if(family.isPresent()) {
                model.put("mode", "DETAILS");
                model.put("familyId", familyId);
                model.put("variantGroup", family.get().getVariantGroups().get(variantGroupId));
                model.put("variantGroupAttributes", family.get().getVariantGroupAttributes(variantGroupId));
                model.put("variantGroupAxisAttributes", family.get().getVariantGroupAxisAttributes(variantGroupId));
                model.put("breadcrumbs", new Breadcrumbs("Families",
                        "Families", "/pim/families",
                        family.get().getFamilyName(), "/pim/families/" + family.get().getFamilyId(),
                        "Variant Groups", "/pim/families/" + family.get().getFamilyId() + "#variantGroups",
                        family.get().getVariantGroups().get(variantGroupId).getName(), ""));
            }
        }
        return new ModelAndView("settings/variantGroup" + (reload ? "_body" : ""), model);
    }

    /*@RequestMapping("/{familyId}/variantGroups/{variantGroupId}/axisAttributes")
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
    }*/

    /*@RequestMapping("/{familyId}/variantGroups/{variantGroupId}/axisAttributes/available/list")
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
    }*/

    /*@RequestMapping(value = "/{familyId}/variantGroups/{variantGroupId}/axisAttributes/{attributeId}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addAxisAttribute(@PathVariable(value = "familyId") String familyId,
                                                @PathVariable(value = "variantGroupId") String variantGroupId,
                                                @PathVariable(value = "attributeId") String attributeId) {
        Map<String, Object> model = new HashMap<>();

        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        if(family.isPresent()) {
            family.get().setGroup("VARIANT_GROUPS");
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
    }*/

    @RequestMapping(value = "/{familyId}/variantGroups/{variantGroupId}/variantAttributes", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateVariantAttribute(@PathVariable(value = "familyId") String familyId,
                                                @PathVariable(value = "variantGroupId") String variantGroupId,
                                                HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();

        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        if(family.isPresent()) {
            family.get().setGroup("VARIANT_GROUPS");
            family.get().updateVariantGroupAttributes(variantGroupId, request.getParameterValues("variantLevel1AttributeIds[]"), request.getParameterValues("variantLevel2AttributeIds[]"));
            familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
            model.put("success", true);
        }

        return model;
    }

    @RequestMapping(value = "/{familyId}/variantGroups/{variantGroupId}/axisAttributes", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateAxisAttribute(@PathVariable(value = "familyId") String familyId,
                                                @PathVariable(value = "variantGroupId") String variantGroupId,
                                                HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();

        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        if(family.isPresent()) {
            family.get().setGroup("VARIANT_GROUPS");
            family.get().updateVariantGroupAxisAttributes(variantGroupId, request.getParameterValues("axisLevel1AttributeIds[]"), request.getParameterValues("axisLevel2AttributeIds[]"));
            familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
            model.put("refresh", true);
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
                family.get().setGroup("VARIANT_GROUPS");
                family.get().getVariantGroups().get(variantGroupId).merge(variantGroup);
                familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
                model.put("refresh", true);
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
            family.get().setGroup("VARIANT_GROUPS");
            variantGroup.setActive("N");
            family.get().addVariantGroup(variantGroup);
            familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{familyId}/variantGroups/{variantGroupId}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggleVariantGroup(@PathVariable(value = "familyId") String familyId,
                                            @PathVariable(value = "variantGroupId") String variantGroupId,
                                            @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        familyService.get(familyId, FindBy.EXTERNAL_ID, false).ifPresent(family ->
                model.put("success", familyService.toggleVariantGroup(family.getId(), FindBy.INTERNAL_ID, variantGroupId, FindBy.EXTERNAL_ID, Toggle.get(active))));
        return model;
    }

    @RequestMapping(value = "/{familyId}/attribute", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttribute(@PathVariable(value = "familyId") String id, FamilyAttribute familyAttribute) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(id, FindBy.EXTERNAL_ID, false);
        // TODO - cross field validation to see if one of attributeGroup ID and FamilyAttributeGroup name is not empty
        if(family.isPresent() /*&& isValid(familyAttribute, model)*/) {
            family.get().setGroup("ATTRIBUTES");
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
    public Result<Map<String, Object>> getFamilyAttributes(@PathVariable(value = "id") String id, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, Object>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = pagination.hasSorts() ? Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName())) : null;
        familyService.get(id, FindBy.EXTERNAL_ID, false).ifPresent(family -> {
            List<Map<String, Object>> dataObjects = new ArrayList<>();
            Page<FamilyAttribute> paginatedResult = familyService.getFamilyAttributes(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort);
            paginatedResult.getContent().forEach(familyAttribute -> {
                familyAttribute.getScope().forEach((channelId, scope) -> {
                    if (family.getChannelVariantGroups().containsKey(channelId)) {
                        VariantGroup channelVariantGroup = family.getVariantGroups().get(family.getChannelVariantGroups().get(channelId));
                        channelVariantGroup.getVariantAxis().forEach((level, axisAttributeIds) -> axisAttributeIds.stream().filter(axisAttributeId -> axisAttributeId.equals(familyAttribute.getId())).findFirst().ifPresent(axisAttributeId -> familyAttribute.getScope().put(channelId, FamilyAttribute.Scope.LOCKED)));
                    }
                });
                dataObjects.add(familyAttribute.toMap());
            });
            result.setDataObjects(dataObjects);
            result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
            result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        });
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
        familyService.get(familyId, FindBy.EXTERNAL_ID, false).ifPresent(family -> model.put("attributeName", family.getAllAttributesMap().get(attributeId.substring(attributeId.lastIndexOf("|") + 1)).getName()));
        model.put("attributeId", attributeId);
        return new ModelAndView("settings/familyAttributeOptions", model);
    }

    @RequestMapping("/{familyId}/attributes/{attributeId}/options/available")
    public ModelAndView availableAttributeOptions(@PathVariable(value = "familyId") String familyId,
                                         @PathVariable(value = "attributeId") String attributeId) {
        Map<String, Object> model = new HashMap<>();
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
            List<FamilyAttribute> familyAttribute = FamilyAttributeGroup.getAllAttributes(family.get()).stream().filter(e -> e.getFullId().equals(familyAttributeId)).collect(Collectors.toList());
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


    @RequestMapping(value = "/{familyId}/attributes/{familyAttributeId}/scope/{scope}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttributeScope(@PathVariable(value = "familyId") String familyId,
                                                          @PathVariable(value = "familyAttributeId") String familyAttributeId,
                                                          @PathVariable(value = "scope") String scope,
                                                          @RequestParam(name = "channelId") String channelId) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        Optional<Channel> channel = channelService.get(channelId, FindBy.EXTERNAL_ID, false);
        FamilyAttribute.Scope _scope = FamilyAttribute.Scope.get(scope);
        if(family.isPresent() && channel.isPresent() && _scope != FamilyAttribute.Scope.UNKNOWN) {
            family.get().setGroup("ATTRIBUTES");
            FamilyAttribute familyAttribute = FamilyAttribute.findAttribute(familyAttributeId, family.get().getAttributes());
            familyAttribute.getScope().put(channel.get().getChannelId(), _scope.next());
            familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
            model.put("success", true);
        }

        return model;
    }

    @RequestMapping(value = "/{familyId}/variantGroups/{variantGroupId}/channels/{channelId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveVariantGroupScope(@PathVariable(value = "familyId") String familyId,
                                                  @PathVariable(value = "variantGroupId") String variantGroupId,
                                                  @PathVariable(value = "channelId") String channelId) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        Optional<Channel> channel = channelService.get(channelId, FindBy.EXTERNAL_ID, false);
        if(family.isPresent() && channel.isPresent()) {
            Map<String, FamilyAttribute> familyAttributes = FamilyAttributeGroup.getAllAttributesMap(family.get());
            family.get().setGroup("ATTRIBUTES", "VARIANT_GROUPS");
            family.get().getVariantGroups().get(variantGroupId).getVariantAxis().forEach((level, axisAttributeIds) -> axisAttributeIds.forEach(axisAttributeId -> familyAttributes.get(axisAttributeId).getScope().put(channelId, FamilyAttribute.Scope.REQUIRED)));
            //TODO - implement the locking of Variant Group
            family.get().getChannelVariantGroups().put(channelId, variantGroupId);
            familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
            model.put("refresh", true);
            model.put("success", true);
        }

        return model;
    }

    @RequestMapping(value = "/{familyId}/attributes/{familyAttributeId}/scopable/{scopable}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttributeAsScopable(@PathVariable(value = "familyId") String familyId,
                                                  @PathVariable(value = "familyAttributeId") String familyAttributeId,
                                                  @PathVariable(value = "scopable") String scopable) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        if(family.isPresent()) {
            family.get().setGroup("ATTRIBUTES");
            FamilyAttribute familyAttribute = FamilyAttribute.findAttribute(familyAttributeId, family.get().getAttributes());
            familyAttribute.setScopable("Y".equals(scopable) ? "N" : "Y");
            familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
            model.put("success", true);
        }

        return model;
    }

    @RequestMapping(value = "/{familyId}/attributes/{familyAttributeId}/options/{attributeOptionId}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveAttributeOptions(@PathVariable(value = "familyId") String familyId, FamilyAttributeOption familyAttributeOption) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(familyId, FindBy.EXTERNAL_ID, false);
        if(family.isPresent() && isValid(familyAttributeOption, model, FamilyAttributeOption.AddOptionGroup.class)) {
            family.get().setGroup("ATTRIBUTES");
            FamilyAttribute familyAttribute = FamilyAttribute.findAttribute(familyAttributeOption.getFamilyAttributeId().substring(familyAttributeOption.getFamilyAttributeId().lastIndexOf("|") + 1), family.get().getAttributes());
            Optional<AttributeOption> attributeOption = collectionService.findAttributeOption(familyAttribute, familyAttributeOption.getAttributeOptionId());
            if(attributeOption.isPresent()) {
                family.get().addAttributeOption(familyAttributeOption, attributeOption.get());
                familyService.update(familyId, FindBy.EXTERNAL_ID, family.get());
                model.put("success", true);
            }
        }
        return model;
    }

    @RequestMapping(value = {"/{id}/variantGroups/list", "/{id}/variantGroups"})
    @ResponseBody
    public Result<Map<String, Object>> getVariantGroups(@PathVariable(value = "id") String id, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, Object>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, Object>> dataObjects = new ArrayList<>();
        Page<VariantGroup> paginatedResult = familyService.getVariantGroups(id, FindBy.EXTERNAL_ID, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }
}