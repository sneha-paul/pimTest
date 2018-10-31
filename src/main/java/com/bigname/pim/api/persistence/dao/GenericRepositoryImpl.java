package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.util.FindBy;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
abstract public class GenericRepositoryImpl<T>  implements GenericRepository<T>{

    protected final MongoTemplate mongoTemplate;


    public GenericRepositoryImpl(MongoTemplate mongoTemplate) {

        Assert.notNull(mongoTemplate, "MongoTemplate must not be null!");
        this.mongoTemplate = mongoTemplate;
    }
    //Test method using mongoTemplate
    public Optional<T> findById(String id, FindBy findBy, Class<T> clazz) {
        Query query = new Query();
        query.addCriteria(Criteria.where(findBy == FindBy.INTERNAL_ID ? "id" : "externalId").is(id));
        T t = mongoTemplate.findOne(query, clazz);
        return Optional.of(t);
    }
}
