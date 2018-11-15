package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.Entity;
import com.bigname.pim.api.domain.Product;
import com.bigname.pim.api.domain.ValidatableEntity;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.Toggle;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by manu on 8/18/18.
 */
public interface BaseService<T, DAO> {
    T create(T t);

    T update(String id, FindBy findBy, T t);

    Optional<T> get(String id, FindBy findBy, boolean... activeRequired);

    boolean toggle(String id, FindBy findBy, Toggle active);

    T cloneInstance(String id, FindBy findBy, Entity.CloneType type);

    Page<T> getAll(int page, int size, Sort sort, boolean... activeRequired);

    List<T> getAll(Sort sort, boolean... activeRequired);

    Page<T> getAll(String[] ids, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    List<T> getAll(String[] ids, FindBy findBy, Sort sort, boolean... activeRequired);

    Page<T> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    List<T> getAllWithExclusions(String[] excludedIds, FindBy findBy, Sort sort, boolean... activeRequired);

    <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Class<?>... groups);

    Map<String, Pair<String, Object>> validate(Map<String, Pair<String, Object>> fieldErrors, T t, String group);

    String getEntityName();

}
