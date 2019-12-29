package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.AssetCollection;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericRepositoryImpl;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class AssetCollectionRepositoryImpl extends GenericRepositoryImpl<AssetCollection, Criteria> implements AssetCollectionRepository {
    public AssetCollectionRepositoryImpl() {
        super(AssetCollection.class);
    }
}
