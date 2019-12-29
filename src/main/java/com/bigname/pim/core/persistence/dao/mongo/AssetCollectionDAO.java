package com.bigname.pim.core.persistence.dao.mongo;

import com.bigname.pim.core.domain.AssetCollection;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;

import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface AssetCollectionDAO extends GenericDAO<AssetCollection>, AssetCollectionRepository {
    Optional<AssetCollection> findByCollectionName(String collectionName); //TODO - handle case insensitive lookup
}
