package com.bigname.pim.client.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import static com.bigname.common.util.ValidationUtil.*;
import com.bigname.pim.api.domain.Category;
import com.bigname.pim.api.domain.Entity;
import com.bigname.pim.api.domain.ValidatableEntity;
import com.bigname.pim.api.service.BaseService;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.client.util.BreadcrumbsBuilder;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validation;
import javax.validation.Validator;
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
        result.setRecordsFiltered(Long.toString(pagination.hasFilters() ? paginatedResult.getContent().size() : paginatedResult.getTotalElements())); //TODO - verify this logic
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

    @Override
    protected <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Class<?>... groups) {
        return service.validate(e, groups);
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

    private Breadcrumbs buildBreadcrumbs(String id, HttpServletRequest request, Map<String, Object> parameterMap) {
        return new BreadcrumbsBuilder(id, entityClass, request, parameterMap, new ArrayList<>(services).toArray(new BaseService[0])).build();
    }


}
