package com.bigname.pim.api.persistence.dao;

import com.bigname.core.persistence.dao.GenericRepositoryImpl;
import com.bigname.pim.api.domain.AssetFamily;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by sanoop on 14/02/2019.
 */
public class AssetFamilyRepositoryImpl  extends GenericRepositoryImpl<AssetFamily> implements AssetFamilyRepository {
    public AssetFamilyRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, AssetFamily.class);
    }

    {
    }}