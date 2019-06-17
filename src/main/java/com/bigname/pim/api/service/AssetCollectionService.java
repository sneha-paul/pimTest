package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.persistence.dao.AssetCollectionDAO;
import com.m7.xtreme.xcore.service.BaseService;
import com.m7.xtreme.xcore.util.FindBy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface AssetCollectionService extends BaseService<AssetCollection, AssetCollectionDAO> {
    List<Map<String, Object>> getAssetsHierarchy(String collectionId, FindBy findBy, String nodeId, boolean... activeRequired);
    Optional<AssetCollection> getAssetCollection(String collectionName);
}
