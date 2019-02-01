package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ReflectionUtil;
import com.bigname.pim.api.domain.Entity;
import com.bigname.pim.api.domain.EntityAssociation;
import com.bigname.pim.api.domain.ValidatableEntity;
import com.bigname.pim.api.service.BaseService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.client.util.BreadcrumbsBuilder;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * The base controller class containing reusable endpoints and methods
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class BaseController<T extends Entity, Service extends BaseService<T, ?>> extends ControllerSupport {
    private Service service;
    private Class<T> entityClass;
    private Set<BaseService> services = new HashSet<>();

    protected BaseController(Service service) {
        this.service = service;
    }

    protected BaseController(Service service, Class<T> entityClass, BaseService... services) {
        this.service = service;
        this.entityClass = entityClass;
        this.services = new HashSet<>(Arrays.asList(services));
        this.services.add(service);
    }

    /*@ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file, ModelMap modelMap) throws IOException {
        modelMap.addAttribute("file", file);
        Files.write(Paths.get("/tmp/" + file.getOriginalFilename()), file.getBytes());
        return new HashMap<>();
    }*/

    @RequestMapping(value =  {"/list", "/data"})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request, HttpServletResponse response, Model model) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, String>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts()) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        } else {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        Page<T> paginatedResult = service.getAll(pagination.getPageNumber(), pagination.getPageSize(), sort, false);
        List<Map<String, String>> dataObjects = new ArrayList<>();
        paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    @RequestMapping(value = "/{id}/active/{active}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> toggle(@PathVariable(value = "id") String id, @PathVariable(value = "active") String active) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", service.toggle(id, FindBy.EXTERNAL_ID, Toggle.get(active)));
        return model;
    }

    @RequestMapping(value = "/{id}/clone/{cloneType}", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> clone(@PathVariable(value = "id") String id, @PathVariable(value = "cloneType") String type) {
        Map<String, Object> model = new HashMap<>();
        model.put("success", service.cloneInstance(id, FindBy.EXTERNAL_ID, Entity.CloneType.find(type)));
        return model;
    }

    protected Map<String, Object> update(String id, T entity, String baseMapping, Class<?>... groups) {
        Map<String, Object> model = new HashMap<>();
        model.put("context", CollectionsUtil.toMap("id", id));
        String active = entity.getActive();
        String discontinued = entity.getDiscontinued();
        if(isValid(entity, model, groups)) {
            service.update(id, FindBy.EXTERNAL_ID, entity);
            model.put("success", true);
            if(!id.equals(entity.getExternalId())) {
                model.put("refreshUrl", baseMapping + entity.getExternalId());
            } else if(entity.getGroup()[0].equals("DETAILS") && (!active.equals(entity.getActive()) || !discontinued.equals(entity.getDiscontinued()))) {
                model.put("refresh", true);
            }
        }
        return model;
    }

    @Override
    protected <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Map<String, Object> context, Class<?>... groups) {
        return service.validate(e, context, groups);
    }

    ModelAndView details(String id, Map<String, Object> parameterMap, HttpServletRequest request, Map<String, Object> model) {
        model.put("breadcrumbs", buildBreadcrumbs(id, request, parameterMap));
        return details(model);
    }

    ModelAndView details(String id, Map<String, Object> model) {
        model.put("breadcrumbs", buildBreadcrumbs(id, null, null));
        return details(model);
    }

    ModelAndView details(Map<String, Object> model) {
        return new ModelAndView((String)model.remove("view"), model);
    }

    Result<Map<String, Object>> getAssociationGridData(Page<Map<String, Object>> paginatedResult, Class<? extends EntityAssociation<T, ?>> associationClass, HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Result<Map<String, Object>> result = new Result<>();
        result.setDraw(dataTableRequest.getDraw());
        Sort sort = null;
        if(pagination.hasSorts() && !dataTableRequest.getOrder().getName().equals("sequenceNum")) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        List<Map<String, Object>> dataObjects = new ArrayList<>();
        int seq[] = {1};
        EntityAssociation<T, ?> association = ReflectionUtil.newInstance(associationClass);
        paginatedResult.getContent().forEach(e -> {
            e.put("sequenceNum", Integer.toString(seq[0] ++));
            dataObjects.add(association != null ? association.toMap(e) : e);
        });
        result.setDataObjects(dataObjects);
        result.setRecordsTotal(Long.toString(paginatedResult.getTotalElements()));
        result.setRecordsFiltered(Long.toString(paginatedResult.getTotalElements()));
        return result;
    }

    Pageable getPaginationRequest(HttpServletRequest request) {
        Request dataTableRequest = new Request(request);
        Pagination pagination = dataTableRequest.getPagination();
        Sort sort = null;
        if(pagination.hasSorts() && !dataTableRequest.getOrder().getName().equals("sequenceNum")) {
            sort = Sort.by(new Sort.Order(Sort.Direction.valueOf(SortOrder.fromValue(dataTableRequest.getOrder().getSortDir()).name()), dataTableRequest.getOrder().getName()));
        }
        return PageRequest.of(pagination.getPageNumber(), pagination.getPageSize(), sort);
    }


    private Breadcrumbs buildBreadcrumbs(String id, HttpServletRequest request, Map<String, Object> parameterMap) {
        return new BreadcrumbsBuilder(id, entityClass, request, parameterMap, new ArrayList<>(services).toArray(new BaseService[0])).build();
    }


}
