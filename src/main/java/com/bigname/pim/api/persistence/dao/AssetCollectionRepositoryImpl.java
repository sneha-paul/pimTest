package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.AssetCollection;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class AssetCollectionRepositoryImpl extends GenericRepositoryImpl<AssetCollection> implements AssetCollectionRepository {
    public AssetCollectionRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, AssetCollection.class);
    }
}
