package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.PimUtil;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        return Optional.of(t);
    }

    public List<T> findAll(Map<String, Object> criteria) {
        return findAll(PimUtil.buildCriteria(criteria));
    }

    public List<T> findAll(Criteria criteria) {
        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.find(query, entityClass);
    }

    public Optional<T> findOne(Map<String, Object> criteria) {
        return findOne(PimUtil.buildCriteria(criteria));
    }

    public Optional<T> findOne(Criteria criteria) {
        Query query = new Query();
        query.addCriteria(criteria);
        T t = mongoTemplate.findOne(query, entityClass);
        return Optional.of(t);
    }


}
