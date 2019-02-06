package com.bigname.core.service;

import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.StringUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.domain.Entity;
import com.bigname.pim.api.domain.Event;
import com.bigname.pim.api.domain.User;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.exception.DuplicateEntityException;
import com.bigname.core.exception.EntityCreateException;
import com.bigname.core.persistence.dao.GenericDAO;
import com.bigname.core.util.FindBy;
import com.bigname.pim.api.service.EventService;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.PimUtil;
import com.bigname.core.util.Toggle;
import com.google.common.base.Preconditions;
import org.javatuples.Pair;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.core.util.FindBy.EXTERNAL_ID;
import static com.bigname.core.util.FindBy.INTERNAL_ID;

/**
 * Created by manu on 8/18/18.
 */
abstract public class BaseServiceSupport<T extends Entity, DAO extends GenericDAO<T>, Service extends BaseService<T, DAO>> implements BaseService<T, DAO > {

    protected DAO dao;
    protected String entityName;
    protected String externalIdProperty;
    protected String externalIdPropertyLabel;
    protected Validator validator;
    protected Service service;
    @Autowired
    private EventService eventService;

    protected BaseServiceSupport(DAO dao, String entityName, Validator validator) {
        this(dao, entityName, entityName + "Id", StringUtil.capitalize(entityName) + " Id",  validator);
    }

    protected BaseServiceSupport(DAO dao, String entityName, String externalIdProperty, String externalIdPropertyLabel, Validator validator) {
        this.validator = validator;
        this.dao = dao;
        this.entityName = entityName;
        this.externalIdProperty = externalIdProperty;
        this.externalIdPropertyLabel = externalIdPropertyLabel;
    }

    @Override
    public List<T> findAll(Map<String, Object> criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public List<T> findAll(Criteria criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public Page<T> findAll(String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return dao.findAll(searchField, keyword, pageable, activeRequired);
    }

    @Override
    public Optional<T> findOne(Map<String, Object> criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Optional<T> findOne(Criteria criteria) {
        return dao.findOne(criteria);
    }


    public String getEntityName() {
        return entityName;
    }

    @Override
    public List<T> create(List<T> entities) {
        entities.forEach(t -> {
            t.setCreatedUser(getCurrentUser());
            t.setCreatedDateTime(LocalDateTime.now());
        });
        return dao.insert(entities);
    }

    @Override
    public List<T> update(List<T> entities) {
        entities.forEach(t -> {
            t.setLastModifiedUser(getCurrentUser());
            t.setLastModifiedDateTime(LocalDateTime.now());
        });
        return dao.saveAll(entities);
    }

    @Override
    public T create(T t) {
        Event event = new Event();
        try {
            get(t.getExternalId(), EXTERNAL_ID, false)
                    .ifPresent(t1 ->    {
                        throw new DuplicateEntityException("Another " + entityName + " instance exists with the given " + entityName + " id:" + t.getExternalId());
                    });
            t.setCreatedDateTime(LocalDateTime.now());
            t.setCreatedUser(getCurrentUser());
            T _t = dao.insert(t);
            if(!t.getClass().equals(Event.class)) {
                event.setEntity(getEntityName());
                event.setTimeStamp(_t.getCreatedDateTime());
                event.setUser(getCurrentUser().map(Entity::getId).orElse(""));
                event.setEventType(Event.Type.CREATE);
                event.setDetails("New " + getEntityName() + " instance created with " + getExternalIdPropertyLabel() + ":" + _t.getExternalId());
                Map<String, Object> dataObj = ConversionUtil.toJSONMap(_t);
                dataObj.put("createdDateTime", _t.getCreatedDateTime());
                event.setData(dataObj);
            }
            return _t;
        } catch(Exception e) {
            String message = "An error occurred while creating the " + entityName + " due to: "+ e.getMessage();
            if(!t.getClass().equals(Event.class)) {
                event.setEntity(getEntityName());
                event.setTimeStamp(t.getCreatedDateTime());
                event.setUser(getCurrentUser().map(Entity::getId).orElse(""));
                event.setEventType(Event.Type.ERROR);
                event.setDetails(message);
                Map<String, Object> dataObj = ConversionUtil.toJSONMap(t);
                dataObj.put("createdDateTime", t.getCreatedDateTime());
                event.setData(dataObj);
            }
            throw new EntityCreateException(message, e);
        } finally {
            if(!t.getClass().equals(Event.class)) {
                eventService.create(event);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
//    @Caching(put = {@CachePut(value = "entities", key = "#findBy.INTERNAL_ID+#id"), @CachePut(value = "entities", key = "#findBy.EXTERNAL_ID+#id")})
    public T update(String id, FindBy findBy, T t) {
        Event event = new Event();
        Optional<T> _t1 = proxy().get(id, findBy, false);
        if(!_t1.isPresent()) {
            throw new IllegalStateException("Illegal operation");
        } else {
            if(findBy == INTERNAL_ID && !entityName.equals("productVariant")) { //TODO need to handle productVariant update from controller using INTERNAL_ID
                Preconditions.checkState(id.equals(t.getId()), "Illegal operation");
            }
            T t1 = _t1.get();
            if(!t.getExternalId().equals(t1.getExternalId())) {
                get(t.getExternalId(), EXTERNAL_ID, false)
                        .ifPresent(t2 ->    {
                            throw new DuplicateEntityException("Another " + entityName + " instance exists with the given " + entityName + " id:" + t.getExternalId());
                        });
            }
            t1.merge(t);
            t1.setLastModifiedDateTime(LocalDateTime.now());
            t1.setLastModifiedUser(getCurrentUser());
            T _t = dao.save(t1);
            if(!t.getClass().equals(Event.class)) {
                event.setEntity(getEntityName());
                event.setTimeStamp(_t.getLastModifiedDateTime());
                event.setUser(getCurrentUser().map(Entity::getId).orElse(""));
                event.setEventType(Event.Type.UPDATE);
                event.setDetails("Updated " + getEntityName() + " instance with " + getExternalIdPropertyLabel() + ":" + _t.getExternalId());
                Map<String, Object> dataObj = ConversionUtil.toJSONMap(_t);
                dataObj.put("lastModifiedDateTime", _t.getLastModifiedDateTime());
                dataObj.put("createdDateTime", _t.getCreatedDateTime());
                event.setData(dataObj);
            }
            if(!t1.getClass().equals(Event.class)) {
                eventService.create(event);
            }
            return _t;
        }
    }

    @Override
    public boolean toggle(String id, FindBy findBy, Toggle active) {
        return proxy().get(id, findBy, false).map(entity -> {
            entity.setGroup("DETAILS");
            entity.setActive(active.state());
            proxy().update(id, findBy, entity);
            return true;
        }).orElse(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T cloneInstance(String id, FindBy findBy, Entity.CloneType type) {
        Optional<T> _t = proxy().get(id, findBy, false);
        return _t.map(t -> cloneInstance((T) t.cloneInstance(), type)).orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Map<String, Object> context, Class<?>... groups) {
        e.orchestrate();
        Set<ConstraintViolation<E>> violations = ConversionUtil.toList(groups).size() > 0 ? validator.validate(e, groups) : validator.validate(e);
        if(e.getClass().getSuperclass().equals(Entity.class)) {
            if(e.getGroup().length != 0 && !"CREATE".equals(e.getGroup()[0])) {
                return validate(context, e.getValidationErrors(violations), (T) e, e.getGroup().length != 0 ? e.getGroup()[0] : "DETAILS");
            } else {
                return validate(context, e.getValidationErrors(violations), (T) e, "");
            }
        }
        return e.getValidationErrors(violations);
    }

    @Override
    public Map<String, Pair<String, Object>> validate(Map<String, Object> context, Map<String, Pair<String, Object>> fieldErrors, T t, String group) {
        if(ValidationUtil.isEmpty(context.get("id")) || !context.get("id").equals(t.getExternalId())) {

            get(t.getExternalId(), EXTERNAL_ID, false)
                    .ifPresent(t1 -> {
                        if(context.containsKey("forceUniqueId") && (boolean)context.get("forceUniqueId")) {
                            t.setExternalId(StringUtil.getUniqueName(t.getExternalId(), dao.findByExternalIdStartingWith(t.getExternalId()).stream().map(Entity::getExternalId).collect(Collectors.toList())));
                        } else {
                            fieldErrors.put(externalIdProperty, Pair.with(externalIdPropertyLabel + " already exists", t.getExternalId()));
                        }

                    });
        }
        return fieldErrors;
    }

    @Override
    public Page<T> findAll(Pageable pageable, boolean... activeRequired) {
        return dao.findAll(pageable, activeRequired);
    }

    @Override
    public Page<T> getAll(int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = new Sort(Sort.Direction.ASC, "externalId");
        }
        return dao.findByActiveIn(PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
    }

    @Override
    public List<T> getAll(Sort sort, boolean... activeRequired) {
        int totalPages;
        int pageNum = 0;
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        List<T> content = new ArrayList<>();
        do {
            Page<T> pagedContent = getAll(pageNum, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired);
            content.addAll(pagedContent.getContent());
            totalPages = pagedContent.getTotalPages();
        } while (pageNum ++ < totalPages - 1);

        return content;
    }

    @Override
    public Page<T> getAll(String[] ids, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return findBy == INTERNAL_ID ? dao.findByIdInAndActiveIn(ids, PimUtil.getActiveOptions(activeRequired), pageable) : dao.findByExternalIdInAndActiveIn(ids, PimUtil.getActiveOptions(activeRequired), pageable);
    }

    @Override
    public List<T> getAll(String[] ids, FindBy findBy, Sort sort, boolean... activeRequired) {
        List<T> temp =  getAll(ids, findBy, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
        if(sort != null) {
            return temp;
        } else {
            Map<String, T> map = temp.stream().collect(Collectors.toMap(t -> findBy == INTERNAL_ID ? t.getId() : t.getExternalId(), t -> t));
            Map<String, T> resultMap = new LinkedHashMap<>();
            for(String id : ids) {
                if(map.containsKey(id)) {
                    resultMap.put(id, map.get(id));
                }
            }
            return resultMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        }
    }

    @Override
    public Page<T> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return findBy == FindBy.INTERNAL_ID ? dao.findByIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable) : dao.findByExternalIdNotInAndActiveIn(excludedIds, PimUtil.getActiveOptions(activeRequired), pageable);
    }

    @Override
    public List<T> getAllWithExclusions(String[] excludedIds, FindBy findBy, Sort sort, boolean... activeRequired) {
        return getAllWithExclusions(excludedIds, findBy, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    @Override
//    @Cacheable(value = "entities", keyGenerator = "cacheKeyGenerator")
    public Optional<T> get(String id, FindBy findBy, boolean... activeRequired) {
        if(id == null || id.isEmpty()) {
            return Optional.empty();
        }
        return findBy == INTERNAL_ID ? dao.findByIdAndActiveIn(id, PimUtil.getActiveOptions(activeRequired)) :  dao.findByExternalIdAndActiveIn(id, PimUtil.getActiveOptions(activeRequired));
    }

    @SuppressWarnings("unchecked")
    protected static <E extends ValidatableEntity> Page<E> paginate(List<E> list, int page, int size, Sort sort) {
        if(sort != null) {
            List<Sort.Order> orders = sort.stream().collect(Collectors.toList());
            if(!orders.isEmpty()) {
                list.sort((e1, e2) -> e1.compare(e2, orders.get(0).getProperty(), orders.get(0).getDirection().name()));
            }
        }
        return paginate(list, page, size);
    }

    protected static void sort(List<Map<String, String>> list, Sort sort) {
        if(sort != null) {
            List<Sort.Order> orders = sort.stream().collect(Collectors.toList());
            if(!orders.isEmpty()) {
                list.sort((e1, e2) -> (orders.get(0).getDirection() == Sort.Direction.ASC ? 1 : -1) * (e1.containsKey(orders.get(0).getProperty()) ? e1.get(orders.get(0).getProperty()).compareTo(e2.get(orders.get(0).getProperty())) : 0));
            }
        }
    }

    protected static <E> Page<E> paginate(List<E> list, int page, int size) {
        List<E> sublist = new ArrayList<>();
        int from = page * size, to = from + size;
        if(from < list.size() && to >= from) {
            if(to > list.size()) {
                to = list.size();
            }
            sublist = list.subList(from, to);
        }
        return new PageImpl<>(sublist, PageRequest.of(page, size), list.size());
    }
    protected T cloneInstance(T t, Entity.CloneType type) {
        return create(t);
    }

    protected Service proxy() {
        try {
            return (Service) AopContext.currentProxy();
        } catch (IllegalStateException e) {
            return (Service) this;
        }
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(User.class::cast);
    }

    public String getExternalIdProperty() {
        return externalIdProperty;
    }

    public String getExternalIdPropertyLabel() {
        return externalIdPropertyLabel;
    }
}
