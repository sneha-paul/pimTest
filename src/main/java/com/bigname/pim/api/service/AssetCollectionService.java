package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.persistence.dao.mongo.AssetCollectionDAO;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.ID;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface AssetCollectionService extends BaseService<AssetCollection, AssetCollectionDAO> {
    List<Map<String, Object>> getAssetsHierarchy(ID<String> id, String nodeId, boolean... activeRequired);
    Optional<AssetCollection> getAssetCollection(String collectionName);
}
