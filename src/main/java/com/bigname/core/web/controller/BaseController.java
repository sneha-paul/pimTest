package com.bigname.core.web.controller;

import com.bigname.common.datatable.model.Pagination;
import com.bigname.common.datatable.model.Request;
import com.bigname.common.datatable.model.Result;
import com.bigname.common.datatable.model.SortOrder;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ReflectionUtil;
import com.bigname.core.data.exporter.BaseExporter;
import com.bigname.core.domain.Entity;
import com.bigname.core.domain.EntityAssociation;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.service.BaseService;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.pim.client.model.Breadcrumbs;
import com.bigname.pim.client.util.BreadcrumbsBuilder;
import com.bigname.pim.client.web.controller.ControllerSupport;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.bigname.common.util.ValidationUtil.isEmpty;

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
    protected BaseExporter exporter;
    protected Logger LOGGER = LoggerFactory.getLogger((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    protected Sort defaultSort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
    protected Predicate<Request> associationSortPredicate = dataTableRequest -> dataTableRequest.getPagination().hasSorts() && !dataTableRequest.getOrder().getName().equals("sequenceNum");

    @Value("${app.export.feed.location:/usr/local/pim/uploads/data/export/}")
    protected String exportFileStorageLocation;

    protected BaseController(Service service) {
        this.service = service;
    }

    protected BaseController(Service service, Class<T> entityClass, BaseExporter<T, Service> exporter, BaseService... services) {
        this(service, entityClass, services);
        this.exporter = exporter;
    }
    protected BaseController(Service service, Class<T> entityClass, BaseService... services) {
        this.service = service;
        this.entityClass = entityClass;
        this.services = new HashSet<>(Arrays.asList(services));
        this.services.add(service);
    }

    @SuppressWarnings("unchecked")
    public Result<Map<String, String>> all(HttpServletRequest request, String searchField) {
        return new Result<Map<String, String>>().buildResult(new Request(request),
                dataTableRequest -> {
                    if(isEmpty(dataTableRequest.getSearch())) {
                        return service.findAll(dataTableRequest.getPageRequest(defaultSort), dataTableRequest.getStatusOptions());
                    } else {
                        return service.findAll(searchField, dataTableRequest.getSearch(), dataTableRequest.getPageRequest(defaultSort), false);
                    }
                },
                paginatedResult -> {
                    List<Map<String, String>> dataObjects = new ArrayList<>();
                    paginatedResult.getContent().forEach(e -> dataObjects.add(e.toMap()));
                    return dataObjects;
                });
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

    @GetMapping("/export")
    public ResponseEntity<Resource> exportFile(HttpServletRequest request) {
        String fileLocation = exportFileStorageLocation;
        String fileName = exporter.getFileName(BaseExporter.Type.XLSX);
        exporter.exportData(fileLocation + fileName);
        return downloadFile(fileLocation, fileName, request);
    }

    protected ModelAndView all(Map<String, Object> model) {
        model.put("breadcrumbs", new Breadcrumbs((String)model.get("title"), (String)model.get("title"), ""));
        return new ModelAndView((String)model.remove("view"), model);
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
            } else if(entity.getGroup().length > 0 && entity.getGroup()[0].equals("DETAILS") && (!active.equals(entity.getActive()) || !discontinued.equals(entity.getDiscontinued()))) {
                model.put("refresh", true);
            }
        }
        return model;
    }

    @Override
    protected <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Map<String, Object> context, Class<?>... groups) {
        return service.validate(e, context, groups);
    }

    protected ModelAndView details(String id, Map<String, Object> parameterMap, HttpServletRequest request, Map<String, Object> model) {
        model.put("breadcrumbs", buildBreadcrumbs(id, request, parameterMap));
        return details(model);
    }

    protected ModelAndView details(String id, Map<String, Object> model) {
        model.put("breadcrumbs", buildBreadcrumbs(id, null, null));
        return details(model);
    }

    protected ModelAndView details(Map<String, Object> model) {
        return new ModelAndView((String)model.remove("view"), model);
    }

    protected Result<Map<String, Object>> getAssociationGridData(HttpServletRequest request,
                                                                 Class<? extends EntityAssociation<T, ?>> associationClass,
                                                                 Function<Request, Page<Map<String, Object>>> resultMapper) {
        return new Result<Map<String, Object>>().buildResult(new Request(request),
                resultMapper,
                paginatedResult -> {
                    List<Map<String, Object>> dataObjects = new ArrayList<>();
                    int seq[] = {1};
                    EntityAssociation<T, ?> association = ReflectionUtil.newInstance(associationClass);
                    paginatedResult.getContent().forEach(e -> {
                        e.put("sequenceNum", Integer.toString(seq[0] ++));
                        dataObjects.add(association != null ? association.toMap(e) : e);
                    });
                    return dataObjects;
                });
    }

    private Breadcrumbs buildBreadcrumbs(String id, HttpServletRequest request, Map<String, Object> parameterMap) {
        return new BreadcrumbsBuilder(id, entityClass, request, parameterMap, new ArrayList<>(services).toArray(new BaseService[0])).build();
    }


}
