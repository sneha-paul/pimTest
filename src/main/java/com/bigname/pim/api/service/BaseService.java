package com.bigname.pim.api.service;

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

    boolean toggle(String id, FindBy findBy, Toggle active);

    Page<T> getAll(int page, int size, Sort sort, boolean... activeRequired);

    List<T> getAll(String[] ids, FindBy findBy, Sort sort, boolean... activeRequired);

    Optional<T> get(String id, FindBy findBy, boolean... activeRequired);

    <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Class<?>... groups);
}
