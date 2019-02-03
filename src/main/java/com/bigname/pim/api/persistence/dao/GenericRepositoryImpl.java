package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
abstract public class GenericRepositoryImpl<T> implements GenericRepository<T>{

    protected final MongoTemplate mongoTemplate;
    private Class<T> entityClass;


    public GenericRepositoryImpl(MongoTemplate mongoTemplate, Class<T> entityClass) {

        Assert.notNull(mongoTemplate, "MongoTemplate must not be null!");
        this.mongoTemplate = mongoTemplate;
        this.entityClass = entityClass;
    }
    //Test method using mongoTemplate
    public Optional<T> findById(String id, FindBy findBy) {
        Query query = new Query();
        query.addCriteria(Criteria.where(findBy == FindBy.INTERNAL_ID ? "id" : "externalId").is(id));
        T t = mongoTemplate.findOne(query, entityClass);
        return t == null ? Optional.empty() : Optional.of(t);
    }

    public List<T> findAll(Map<String, Object> criteria) {
        return findAll(PimUtil.buildCriteria(criteria));
    }

    public List<T> findAll(Criteria criteria) {
        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.find(query, entityClass);
    }

    public Page<T> findAll(String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        Query query = new Query();
        keyword = "(?i)" + keyword;
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("externalId").regex(keyword), Criteria.where(searchField).regex(keyword));
        criteria.andOperator(Criteria.where("active").in(Arrays.asList(PimUtil.getActiveOptions(activeRequired))));
        query.addCriteria(criteria).with(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()));
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, entityClass),
                pageable,
                () -> mongoTemplate.count(query, entityClass));
    }

    public Optional<T> findOne(Map<String, Object> criteria) {
        return findOne(PimUtil.buildCriteria(criteria));
    }

    public Optional<T> findOne(Criteria criteria) {
        Query query = new Query();
        query.addCriteria(criteria);
        T t = mongoTemplate.findOne(query, entityClass);
        return t == null ? Optional.empty() : Optional.of(t);
    }

    /**
     * activeRequired vararg combinations
     * 			                    -	active
     *           true			    -	active
     *           false			    -	active, inactive
     *           false, true		-	inactive
     *           false, false, true	-	discontinued
     *           false, true, true	-	inactive, discontinued
     *           true, true, true	-	active, inactive, discontinued
     *
     * */
    @Override
    public Page<T> findAll(Pageable pageable, boolean... activeRequired) {

        Query query = new Query();
        Criteria criteria = new Criteria();
        String[] activeOptions = PimUtil.getActiveOptions(activeRequired);
        Criteria activeCriteria = Criteria.where("active").in(Arrays.asList(activeOptions));

        boolean showDiscontinued = PimUtil.showDiscontinued(activeRequired);
        if(showDiscontinued) {
            //Discontinued
            // (start == null && end == null && disc = 'Y') or
            // (start != null && end != null && start <= now && end >= now) or
            // (start != null && end == null && start <= now) or
            // (start == null && end != null && end >= now)
            Criteria discontinueCriteria = new Criteria();
            discontinueCriteria.orOperator(
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").is(null).and("discontinued").is("N"),
                    Criteria.where("discontinuedFrom").ne(null).lte(LocalDateTime.now()).and("discontinuedTo").ne(null).gte(LocalDateTime.now()),
                    Criteria.where("discontinuedFrom").ne(null).lte(LocalDateTime.now()).and("discontinuedTo").is(null),
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").ne(null).gte(LocalDateTime.now())
            );
            criteria.orOperator(activeCriteria, discontinueCriteria);
        } else {
            //Not discontinued
            // (start == null && end == null && disc != 'Y') or
            // (start != null && end != null && start > now && end < now) or
            // (start != null && end == null && start > now) or
            // (start == null && end != null && end < now)

            Criteria discontinueCriteria = new Criteria();
            discontinueCriteria.orOperator(
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").is(null).and("discontinued").is("N"),
                    Criteria.where("discontinuedFrom").ne(null).gt(LocalDateTime.now()).and("discontinuedTo").ne(null).lt(LocalDateTime.now()),
                    Criteria.where("discontinuedFrom").ne(null).gt(LocalDateTime.now()).and("discontinuedTo").is(null),
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").ne(null).lt(LocalDateTime.now())
            );
            criteria.andOperator(activeCriteria, discontinueCriteria);
        }
        query.addCriteria(criteria).with(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()));
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, entityClass),
                pageable,
                () -> mongoTemplate.count(query, entityClass));
    }
}
