package com.bigname.core.persistence.dao;

import com.bigname.core.util.FindBy;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
abstract public class GenericRepositoryImpl<T> implements GenericRepository<T> {

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
        Criteria activeCriteria = new Criteria();
        if(activeOptions.length == 2) {
            activeCriteria = Criteria.where("active").in(Arrays.asList(activeOptions));
        } else if(activeOptions.length == 0){
            activeCriteria = null;
            //activeCriteria = Criteria.where("active").nin(Arrays.asList(new String[]{"Y", "N"}));
        } else if(activeOptions.length == 1){
            if(activeOptions[0].equals("Y")) {
                activeCriteria.orOperator(
                        Criteria.where("activeFrom").is(null).and("activeTo").is(null).and("active").is("Y"),
                        Criteria.where("activeFrom").ne(null).lte(LocalDateTime.now()).and("activeTo").ne(null).gte(LocalDateTime.now()),
                        Criteria.where("activeFrom").ne(null).lte(LocalDateTime.now()).and("activeTo").is(null),
                        Criteria.where("activeFrom").is(null).and("activeTo").ne(null).gte(LocalDateTime.now())
                );
               // activeCriteria = Criteria.where("active").is("Y");
            } else {
                activeCriteria.orOperator(
                        Criteria.where("activeFrom").is(null).and("activeTo").is(null).and("active").is("N"),
                        Criteria.where("activeFrom").ne(null).gt(LocalDateTime.now()),
                        Criteria.where("activeFrom").is(null).and("activeTo").ne(null).lt(LocalDateTime.now().minusDays(1)),
                        Criteria.where("activeFrom").ne(null).lte(LocalDateTime.now()).and("activeTo").ne(null).lt(LocalDateTime.now().minusDays(1))
                );
                //activeCriteria = Criteria.where("active").is("N");
            }
        }

        boolean showDiscontinued = PimUtil.showDiscontinued(activeRequired);
        if(showDiscontinued) {
            //Discontinued
            // (start == null && end == null && disc = 'Y') or
            // (start != null && end != null && start <= now && end >= now) or
            // (start != null && end == null && start <= now) or
            // (start == null && end != null && end >= now)
            Criteria discontinueCriteria = new Criteria();
            discontinueCriteria.orOperator(
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").is(null).and("discontinued").is("Y"),
                    Criteria.where("discontinuedFrom").ne(null).lte(LocalDateTime.now()).and("discontinuedTo").ne(null).gte(LocalDateTime.now()),
                    Criteria.where("discontinuedFrom").ne(null).lte(LocalDateTime.now()).and("discontinuedTo").is(null),
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").ne(null).gte(LocalDateTime.now())
            );
            if(activeCriteria != null){
                criteria.orOperator(activeCriteria, discontinueCriteria);
            } else {
                criteria.orOperator(discontinueCriteria);
            }

        } else {
            //Not discontinued
            // (start == null && end == null && disc != 'Y') or
            // (start != null && end != null && start > now && end < now) or
            // (start != null && end == null && start > now) or
            // (start == null && end != null && end < now)

            Criteria discontinueCriteria = new Criteria();
            discontinueCriteria.orOperator(
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").is(null).and("discontinued").is("N"),
                    Criteria.where("discontinuedFrom").ne(null).gt(LocalDateTime.now()),
                    Criteria.where("discontinuedFrom").is(null).and("discontinuedTo").ne(null).lt(LocalDateTime.now().minusDays(1)),
                    Criteria.where("discontinuedFrom").ne(null).lte(LocalDateTime.now()).and("discontinuedTo").ne(null).lt(LocalDateTime.now().minusDays(1))
            );
            if(activeCriteria != null) {
                criteria.andOperator(activeCriteria, discontinueCriteria);
            } else {
                criteria.andOperator(discontinueCriteria);
            }
        }
        query.addCriteria(criteria).with(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()));
        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, entityClass),
                pageable,
                () -> mongoTemplate.count(query, entityClass));
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
