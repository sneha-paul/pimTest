package com.bigname.core.service;

import com.bigname.core.domain.Entity;
import com.bigname.core.domain.ValidatableEntity;
import com.bigname.core.util.FindBy;
import com.bigname.core.util.Toggle;
import com.bigname.pim.api.domain.User;
import org.javatuples.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by manu on 8/18/18.
 */
public interface BaseService<T, DAO> {
    T create(T t);

    T update(String id, FindBy findBy, T t);

    List<T> create(List<T> entities);

    List<T> update(List<T> entities);

    Optional<T> get(String id, FindBy findBy, boolean... activeRequired);

    boolean toggle(String id, FindBy findBy, Toggle active);

    T cloneInstance(String id, FindBy findBy, Entity.CloneType type);

    /**
     * activeRequired vararg combinations
     * 			                    -	active
     *           true			    -	active
     *           false			    -	active, inactive
     *           false, true		-	inactive
     *           false, false, true	-	discontinued
     *           false, true, true	-	inactive, discontinued
     *           true, true, true	-	active, inactive, discontinued
     * @param page
     * @param size
     * @param sort
     * @param activeRequired
     * @return
     */
    Page<T> getAll(int page, int size, Sort sort, boolean... activeRequired);

    List<T> getAll(Sort sort, boolean... activeRequired);

    Page<T> findAll(Pageable pageable, boolean... activeRequired);

    Page<T> getAll(String[] ids, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    List<T> getAll(String[] ids, FindBy findBy, Sort sort, boolean... activeRequired);

    Page<T> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired);

    List<T> getAllWithExclusions(String[] excludedIds, FindBy findBy, Sort sort, boolean... activeRequired);

    <E extends ValidatableEntity> Map<String, Pair<String, Object>> validate(E e, Map<String, Object> context, Class<?>... groups);

    Map<String, Pair<String, Object>> validate(Map<String, Object> context, Map<String, Pair<String, Object>> fieldErrors, T t, String group);

    List<T> findAll(Map<String, Object> criteria);

    List<T> findAll(Criteria criteria);

    Page<T> findAll(String searchField, String keyword, Pageable pageable, boolean... activeRequired);

    Optional<T> findOne(Map<String, Object> criteria);

    Optional<T> findOne(Criteria criteria);

    String getEntityName();

    Optional<User> getCurrentUser();

}
