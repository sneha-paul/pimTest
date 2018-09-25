package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Entity;
import com.bigname.pim.api.exception.EntityCreateException;
import com.bigname.pim.api.persistence.dao.BaseDAO;
import com.bigname.pim.api.service.BaseService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import com.bigname.pim.util.Toggle;
import com.google.common.base.Preconditions;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

import static com.bigname.pim.util.FindBy.EXTERNAL_ID;
import static com.bigname.pim.util.FindBy.INTERNAL_ID;

/**
 * Created by manu on 8/18/18.
 */
abstract class BaseServiceSupport<T extends Entity, DAO extends BaseDAO<T>> implements BaseService<T, DAO > {

    public static final int MAX_PAGE_SIZE = 200;
    protected DAO dao;
    protected String entityName;
    protected Validator validator;

    protected BaseServiceSupport(DAO dao, String entityName, Validator validator) {
        this.validator = validator;
        this.dao = dao;
        this.entityName = entityName;
    }

    abstract protected T createOrUpdate(T t);

    @Override
    public T create(T t) {
        try {
            return createOrUpdate(t);
        } catch(Exception e) {
            throw new EntityCreateException("An error occurred while creating the " + entityName + " dut to: "+ e.getMessage(), e);
        }
    }

    @Override
    public T update(String id, FindBy findBy, T t) {
        Optional<T> _t1 = get(id, findBy, false);
        if(!_t1.isPresent()) {
            throw new IllegalStateException("Illegal operation");
        } else {
            if(findBy == INTERNAL_ID) {
                Preconditions.checkState(id.equals(t.getId()), "Illegal operation");
            }
            T t1 = _t1.get();
            t1.merge(t);
            return createOrUpdate(t1);
        }
    }

    @Override
    public boolean toggle(String id, FindBy findBy, Toggle active) {
        Optional<T> _t = get(id, findBy, false);
        if(_t.isPresent()) {
            T t = _t.get();
            t.setActive(active.state());
            createOrUpdate(t);
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Pair<String, Object>> validate(T t, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(t, groups);
        return t.getValidationErrors(violations);
    }

    @Override
    public Page<T> getAll(int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = new Sort(Sort.Direction.ASC, "externalId");
        }
        return dao.findAllByActiveIn(PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
    }

    @Override
    public List<T> getAll(String[] ids, FindBy findBy, Sort sort, boolean... activeRequired) {

        //TODO - Implement sorting, if sort is not null

        if(ids.length > MAX_PAGE_SIZE) {
            ids = Arrays.copyOfRange(ids, 0, MAX_PAGE_SIZE);
        }

        return findBy == INTERNAL_ID ? dao.findAllByIdInAndActiveIn(ids, PimUtil.getActiveOptions(activeRequired)) : dao.findAllByExternalIdInAndActiveIn(ids, PimUtil.getActiveOptions(activeRequired));
    }

    @Override
    public Optional<T> get(String id, FindBy findBy, boolean... activeRequired) {
        if(id == null || id.isEmpty()) {
            return Optional.empty();
        }
        return findBy == INTERNAL_ID ? dao.findByIdAndActiveIn(id, PimUtil.getActiveOptions(activeRequired)) :  dao.findByExternalIdAndActiveIn(id, PimUtil.getActiveOptions(activeRequired));
    }

    protected static <E> Page<E> paginate(List<E> list, int page, int size) {
        List<E> sublist = new ArrayList<>();
        int from = page, to = from + size;
        if(from < list.size() && to >= from) {
            if(to > list.size()) {
                to = list.size();
            }
            sublist = list.subList(from, to);
        }
        return new PageImpl<E>(sublist, PageRequest.of(page, size), list.size());
    }
}
