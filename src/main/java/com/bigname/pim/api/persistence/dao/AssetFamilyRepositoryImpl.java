package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.AssetFamily;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Created by sanoop on 14/02/2019.
 */
public class AssetFamilyRepositoryImpl  extends GenericRepositoryImpl<AssetFamily, Criteria> implements AssetFamilyRepository {
    public AssetFamilyRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, AssetFamily.class);
    }

    {
    }}