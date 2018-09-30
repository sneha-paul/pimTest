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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The base controller class containing reusable endpoints and methods
 *
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class BaseController<T extends Entity, Service extends BaseService<T, ?>> {
    private Service service;

    protected BaseController(Service service) {
        this.service = service;
    }

    @RequestMapping("/list")
    @ResponseBody
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
        Page<T> paginatedResult = service.getAll(pagination.getPageNumber()/pagination.getPageSize(), pagination.getPageSize(), sort, false);
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

    private <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Class<?>... groups) {
        return service.validate(e, groups);
    }

    protected <E extends ValidatableEntity> boolean isValid(E e, Map<String, Object> model, Class<?>... groups) {
        model.put("fieldErrors", validate(e, groups));
        model.put("group", e.getGroup());
        return isEmpty(model.get("fieldErrors"));
    }


}
