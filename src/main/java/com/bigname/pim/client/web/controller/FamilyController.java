package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.api.service.ChannelService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.m7.xtreme.common.datatable.model.Pagination;
import com.m7.xtreme.common.datatable.model.Request;
import com.m7.xtreme.common.datatable.model.Result;
import com.m7.xtreme.common.datatable.model.SortOrder;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.StringUtil;
import com.m7.xtreme.common.util.ValidationUtil;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import com.m7.xtreme.xcore.web.controller.BaseController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;


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

    @RequestMapping(value =  {"/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request) {
        return all(request, "familyName");
    }

    @RequestMapping(value = {"/{id}", "/create"})
    public ModelAndView details(@PathVariable(value = "id", required = false) String id,
                                @RequestParam(name = "reload", required = false) boolean reload) {
        Map<String, Object> model = new HashMap<>();
        model.put("active", "FAMILIES");
        model.put("mode", id == null ? "CREATE" : "DETAILS");
        model.put("view", "settings/family"  + (reload ? "_body" : ""));
        return id == null ? super.details(model) : familyService.get(ID.EXTERNAL_ID(id), false)
                .map(family -> {
                    model.put("family", family);
                    model.put("channels", ConversionUtil.toJSONString(channelService.getAll(0, 100, null).getContent().stream().collect(Collectors.toMap(Channel::getChannelId, Channel::getChannelName))));
                    return super.details(id, model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Family with Id: " + id));
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

    @RequestMapping("/{familyId}/attributes/data")
    @ResponseBody
    public Result<Map<String, Object>> allAttributes(@PathVariable(value = "familyId") String id, HttpServletRequest request) {

        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, Object>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = pagination.hasSorts() ? Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName())) : null;
        familyService.get(ID.EXTERNAL_ID(id), false).ifPresent(family -> {
            List<Map<String, Object>> dataObjects = new ArrayList<>();
            Page<FamilyAttribute> paginatedResult = familyService.getFamilyAttributes(ID.EXTERNAL_ID(id), pagination.getPageNumber(), pagination.getPageSize(), sort);
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


    @RequestMapping(value= {"/{familyId}/attributes/{attributeId}", "/{familyId}/attributes/create"})
    public ModelAndView attributeDetails(@PathVariable(value = "familyId") String familyId,
                                         @PathVariable(value = "attributeId", required = false) String attributeId) {
        return familyService.get(ID.EXTERNAL_ID(familyId), false)
                .map(family -> {
                    Map<String, Object> model = new HashMap<>();
                    FamilyAttribute attribute;
                    String mode;
                    if(isNotEmpty(attributeId)) {
                        mode = "DETAILS";
                        attribute = family.getAllAttributes().stream()
                                .filter(attribute1 -> attribute1.getId().equals(attributeId)).findFirst()
                                .orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeId));
                        if(isNotEmpty(attribute.getParentAttributeId())) {
                            model.put("parentAttribute", family.getAttribute(attribute.getParentAttributeId()).orElse(null));
                        }
                        model.put("breadcrumbs", new Breadcrumbs("Product Types",
                                "Product Types", "/pim/families",
                                family.getFamilyName(), "/pim/families/" + family.getFamilyId(),
                                "Attributes", "/pim/families/" + family.getFamilyId() + "#attributes",
                                attribute.getName(), ""));
                        model.put("familyId", familyId);
                        model.put("attributeGroup", FamilyAttributeGroup.getUniqueLeafGroupLabel(attribute.getAttributeGroup(), "|"));
                    } else {
                        attribute = new FamilyAttribute();
                        mode = "CREATE";
                        model.put("attributeCollections", collectionService.getAll(0, 100, null).getContent());
                        model.put("attributeGroups", familyService.getAttributeGroupsIdNamePair(ID.EXTERNAL_ID(familyId), null));
                        model.put("parentAttributeGroups", familyService.getParentAttributeGroupsIdNamePair(ID.EXTERNAL_ID(familyId), null));
                    }

                    model.put("attribute", attribute);
                    model.put("mode", mode);
                    model.put("uiTypes", Attribute.UIType.getAll());
                    model.put("parentAttributes", family.getAvailableParentAttributes(attribute).stream().collect(CollectionsUtil.toLinkedMap(FamilyAttribute::getFullId, FamilyAttribute::getName)));
                    return new ModelAndView("settings/familyAttribute", model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Family with Id: " + familyId));

    }

    @RequestMapping(value = "/{familyId}/attributes", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createAttribute(@PathVariable(value = "familyId") String id, FamilyAttribute familyAttribute) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(ID.EXTERNAL_ID(id), false);
        // TODO - cross field validation to see if one of attributeGroup ID and attributeGroup name is not empty
        if(family.isPresent() && isValid(familyAttribute, model)) {
            family.get().setGroup("ATTRIBUTES");
            Attribute attribute = collectionService.findAttribute(ID.EXTERNAL_ID(familyAttribute.getCollectionId()), familyAttribute.getAttributeId()).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute in Collection with ids: [" + familyAttribute.getCollectionId() + " >> " + familyAttribute.getAttributeId() + "]"));
            familyAttribute.setAttribute(attribute);
            family.get().addAttribute(familyAttribute);
            familyService.update(ID.EXTERNAL_ID(id), family.get());
            model.put("success", true);
        }
        return model;
    }

    @RequestMapping(value = "/{familyId}/attributes/{attributeId}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> updateAttribute(@PathVariable(value = "familyId") String familyId,
                                               @PathVariable(value = "attributeId") String attributeId,
                                               @RequestParam Map<String, Object> parameterMap) {
        return familyService.get(ID.EXTERNAL_ID(familyId), false)
                .map(family -> {
                    Map<String, Object> model = new HashMap<>();
                    String attributeFullId = (String)parameterMap.get("fullId");
                    return family.getAttribute(attributeFullId)
                            .map(attribute -> {
                                attribute.setName((String)parameterMap.get("name"));
                                if(isNotEmpty(parameterMap.get("parentAttributeId"))) {
                                    attribute.setParentAttributeId((String)parameterMap.get("parentAttributeId"));
                                } else if(isNotEmpty(attribute.getParentAttributeId())) {
                                    attribute.setParentAttributeId(null);
                                }
                                /*if(isNotEmpty(parameterMap.get("uiType"))) {
                                    attribute.setUiType(Attribute.UIType.get((String)parameterMap.get("uiType")));
                                }*/
                                if(isValid(attribute, model)) {
                                    family.setGroup("ATTRIBUTES");
                                    family.updateAttribute(attribute);
                                    familyService.update(ID.EXTERNAL_ID(familyId), family);
                                    model.put("success", true);
                                }
                                return model;
                            }).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeFullId));
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Family with Id: " + familyId));
    }

    @RequestMapping("/{familyId}/attributes/{attributeId}/options/data")
    @ResponseBody
    public Result<Map<String, String>> allAttributeOptions(@PathVariable(value = "familyId") String familyId,
                                                           @PathVariable(value = "attributeId") String attributeId,
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
        Page<FamilyAttributeOption> paginatedResult = familyService.getFamilyAttributeOptions(ID.EXTERNAL_ID(familyId), attributeId, pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping("/{familyId}/attributes/{attributeId}/options/available")
    public ModelAndView availableAttributeOptions(@PathVariable(value = "familyId") String familyId,
                                                  @PathVariable(value = "attributeId") String attributeId) {
        Map<String, Object> model = new HashMap<>();
        model.put("attributeId", attributeId);
        return new ModelAndView("settings/availableFamilyAttributeOptions", model);
    }

    @RequestMapping("/{familyId}/attributes/{attributeId}/options/available/data")
    @ResponseBody
    public Result<Map<String, String>> availableAttributeOptions(@PathVariable(value = "familyId") String familyId,
                                                                 @PathVariable(value = "attributeId") String _familyAttributeId,
                                                                 HttpServletRequest request) {
        Optional<Family> family = familyService.get(ID.EXTERNAL_ID(familyId), false);
        Result<Map<String, String>> result = new Result<>();
        if(family.isPresent()) {
            String familyAttributeId = family.get().getAttributeFullId(_familyAttributeId);
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
                collectionService.findAttribute(ID.EXTERNAL_ID(familyAttribute.get(0).getCollectionId()), familyAttribute.get(0).getAttributeId()).ifPresent(attribute -> {
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

    @RequestMapping(value= {"/{familyId}/attributes/{attributeId}/options/{attributeOptionId}"})
    public ModelAndView attributeOptionDetails(@PathVariable(value = "familyId") String familyId,
                                               @PathVariable(value = "attributeId") String attributeId,
                                               @PathVariable(value = "attributeOptionId", required = false) String attributeOptionId) {

        return familyService.get(ID.EXTERNAL_ID(familyId), false)
                .map(family -> {
                    Map<String, Object> model = new HashMap<>();
                    FamilyAttribute attribute = family.getAllAttributes().stream()
                            .filter(attribute1 -> attribute1.getId().equals(attributeId)).findFirst()
                            .orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute with Id: " + attributeId));

                    String mode;
                    FamilyAttributeOption attributeOption;
                    if(isNotEmpty(attributeOptionId)) {
                        mode = "DETAILS";
                        String optionFullId = StringUtil.getPipedValue(attribute.getFullId(), attributeOptionId);
//                        attributeOption = family.getAttributeOption(optionFullId).orElseThrow(() -> new EntityNotFoundException("Unable to find Attribute Option with Id: " + optionFullId));
                    } else {
                        attributeOption = new FamilyAttributeOption();
                        mode = "CREATE";
                    }
                    model.put("mode", mode);
                    model.put("attribute", attribute);
//                    model.put("attributeOption", attributeOption);
                    if(isNotEmpty(attribute.getParentAttributeId())) {
                        model.put("parentAttributeOptions", family.getAttribute(attribute.getParentAttributeId())
                                .map(parentAttribute -> parentAttribute.getOptions()
                                        .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.comparing(FamilyAttributeOption::getValue)))
                                        .collect(CollectionsUtil.toLinkedMap(e -> e.getValue().getFullId(), e -> e.getValue().getValue())))
                                .orElse(new HashMap<>()));
                    }
                    return new ModelAndView("settings/familyAttributeOption", model);
                }).orElseThrow(() -> new EntityNotFoundException("Unable to find Family with Id: " + familyId));

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
        Optional<Family> family = familyService.get(ID.EXTERNAL_ID(familyId), false);
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
                model.put("breadcrumbs", new Breadcrumbs("Product Types",
                        "Product Types", "/pim/families",
                        family.get().getFamilyName(), "/pim/families/" + family.get().getFamilyId(),
                        "Variant Groups", "/pim/families/" + family.get().getFamilyId() + "#variantGroups",
                        family.get().getVariantGroups().get(variantGroupId).getName(), ""));
            }
        }
        return new ModelAndView("settings/variantGroup" + (reload ? "_body" : ""), model);
    }

    @RequestMapping(value = "/{familyId}/variantGroups/{variantGroupId}/variantAttributes", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateVariantAttribute(@PathVariable(value = "familyId") String familyId,
                                                @PathVariable(value = "variantGroupId") String variantGroupId,
                                                HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();

        Optional<Family> family = familyService.get(ID.EXTERNAL_ID(familyId), false);
        if(family.isPresent()) {
            family.get().setGroup("VARIANT_GROUPS");
            family.get().updateVariantGroupAttributes(variantGroupId, request.getParameterValues("variantLevel1AttributeIds[]"), request.getParameterValues("variantLevel2AttributeIds[]"));
            familyService.update(ID.EXTERNAL_ID(familyId), family.get());
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

        Optional<Family> family = familyService.get(ID.EXTERNAL_ID(familyId), false);
        if(family.isPresent()) {
            family.get().setGroup("VARIANT_GROUPS");
            family.get().updateVariantGroupAxisAttributes(variantGroupId, request.getParameterValues("axisLevel1AttributeIds[]"), request.getParameterValues("axisLevel2AttributeIds[]"));
            familyService.update(ID.EXTERNAL_ID(familyId), family.get());
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
            Optional<Family> family = familyService.get(ID.EXTERNAL_ID(familyId), false);
            if(family.isPresent()) {
                family.get().setGroup("VARIANT_GROUPS");
                family.get().getVariantGroups().get(variantGroupId).merge(variantGroup);
                familyService.update(ID.EXTERNAL_ID(familyId), family.get());
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
        Optional<Family> family = familyService.get(ID.EXTERNAL_ID(familyId), false);
        if(family.isPresent() && isValid(variantGroup, model, VariantGroup.CreateGroup.class)) { // TODO - check if another group with the same id/name exists
            family.get().setGroup("VARIANT_GROUPS");
            variantGroup.setActive("N");
            family.get().addVariantGroup(variantGroup);
            familyService.update(ID.EXTERNAL_ID(familyId), family.get());
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
        familyService.get(ID.EXTERNAL_ID(familyId), false).ifPresent(family ->
                model.put("success", familyService.toggleVariantGroup(ID.EXTERNAL_ID(family.getId()), variantGroupId, Toggle.get(active))));
        return model;
    }



    @RequestMapping("/{familyId}/attributes/{attributeId}/options")
    public ModelAndView attributeOptions(@PathVariable(value = "familyId") String familyId,
                                         @PathVariable(value = "attributeId") String attributeId) {
        Map<String, Object> model = new HashMap<>();
        familyService.get(ID.EXTERNAL_ID(familyId), false).ifPresent(family -> model.put("attributeName", family.getAllAttributesMap().get(attributeId.substring(attributeId.lastIndexOf("|") + 1)).getName()));
        model.put("attributeId", attributeId);
        return new ModelAndView("settings/familyAttributeOptions", model);
    }

    @RequestMapping(value = "/{familyId}/attributes/{familyAttributeId}/scope/{scope}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttributeScope(@PathVariable(value = "familyId") String familyId,
                                                          @PathVariable(value = "familyAttributeId") String familyAttributeId,
                                                          @PathVariable(value = "scope") String scope,
                                                          @RequestParam(name = "channelId") String channelId) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(ID.EXTERNAL_ID(familyId), false);
        Optional<Channel> channel = channelService.get(ID.EXTERNAL_ID(channelId), false);
        FamilyAttribute.Scope _scope = FamilyAttribute.Scope.get(scope);
        if(family.isPresent() && channel.isPresent() && _scope != FamilyAttribute.Scope.UNKNOWN) {
            family.get().setGroup("ATTRIBUTES");
            FamilyAttribute familyAttribute = FamilyAttribute.findAttribute(familyAttributeId, family.get().getAttributes());
            familyAttribute.getScope().put(channel.get().getChannelId(), _scope.next());
            familyService.update(ID.EXTERNAL_ID(familyId), family.get());
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
        Optional<Family> family = familyService.get(ID.EXTERNAL_ID(familyId), false);
        Optional<Channel> channel = channelService.get(ID.EXTERNAL_ID(channelId), false);
        if(family.isPresent() && channel.isPresent()) {
            Map<String, FamilyAttribute> familyAttributes = FamilyAttributeGroup.getAllAttributesMap(family.get());
            family.get().setGroup("ATTRIBUTES", "VARIANT_GROUPS");
            family.get().getVariantGroups().get(variantGroupId).getVariantAxis().forEach((level, axisAttributeIds) -> axisAttributeIds.forEach(axisAttributeId -> familyAttributes.get(axisAttributeId).getScope().put(channelId, FamilyAttribute.Scope.REQUIRED)));
            //TODO - implement the locking of Variant Group
            family.get().getChannelVariantGroups().put(channelId, variantGroupId);
            familyService.update(ID.EXTERNAL_ID(familyId), family.get());
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
        Optional<Family> family = familyService.get(ID.EXTERNAL_ID(familyId), false);
        if(family.isPresent()) {
            family.get().setGroup("ATTRIBUTES");
            FamilyAttribute familyAttribute = FamilyAttribute.findAttribute(familyAttributeId, family.get().getAttributes());
            familyAttribute.setScopable("Y".equals(scopable) ? "N" : "Y");
            familyService.update(ID.EXTERNAL_ID(familyId), family.get());
            model.put("success", true);
        }

        return model;
    }

    @RequestMapping(value = "/{familyId}/attributes/{familyAttributeId}/options/{attributeOptionId}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveAttributeOptions(@PathVariable(value = "familyId") String familyId, FamilyAttributeOption familyAttributeOption) {
        Map<String, Object> model = new HashMap<>();
        Optional<Family> family = familyService.get(ID.EXTERNAL_ID(familyId), false);
        if(family.isPresent() && isValid(familyAttributeOption, model, FamilyAttributeOption.AddOptionGroup.class)) {
            family.get().setGroup("ATTRIBUTES");
            FamilyAttribute familyAttribute = FamilyAttribute.findAttribute(familyAttributeOption.getFamilyAttributeId().substring(familyAttributeOption.getFamilyAttributeId().lastIndexOf("|") + 1), family.get().getAttributes());
            Optional<AttributeOption> attributeOption = collectionService.findAttributeOption(familyAttribute, familyAttributeOption.getAttributeOptionId());
            if(attributeOption.isPresent()) {
                family.get().addAttributeOption(familyAttributeOption, attributeOption.get());
                familyService.update(ID.EXTERNAL_ID(familyId), family.get());
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
        Page<VariantGroup> paginatedResult = familyService.getVariantGroups(ID.EXTERNAL_ID(id), pagination.getPageNumber(), pagination.getPageSize(), sort);
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }
}