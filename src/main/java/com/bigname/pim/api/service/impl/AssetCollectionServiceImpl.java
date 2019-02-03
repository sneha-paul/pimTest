package com.bigname.pim.api.service.impl;

import com.bigname.common.util.ValidationUtil;
import com.bigname.core.service.BaseServiceSupport;
import com.bigname.core.util.FindBy;
import com.bigname.pim.api.domain.AssetCollection;
import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.api.persistence.dao.AssetCollectionDAO;
import com.bigname.pim.api.persistence.dao.VirtualFileDAO;
import com.bigname.pim.api.service.AssetCollectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Service
public class AssetCollectionServiceImpl extends BaseServiceSupport<AssetCollection, AssetCollectionDAO, AssetCollectionService> implements AssetCollectionService {

    private AssetCollectionDAO assetCollectionDAO;
    private VirtualFileDAO assetDAO;

    protected AssetCollectionServiceImpl(AssetCollectionDAO assetCollectionDAO, Validator validator, VirtualFileDAO assetDAO) {
        super(assetCollectionDAO, "assetCollection", validator);
        this.assetCollectionDAO = assetCollectionDAO;
        this.assetDAO = assetDAO;
    }

    @Override
    public List<AssetCollection> findAll(Map<String, Object> criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public List<AssetCollection> findAll(Criteria criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public Page<AssetCollection> findAll(String searchField, String keyword, Pageable pageable, boolean... activeRequired) {
        return assetCollectionDAO.findAll(searchField, keyword, pageable, activeRequired);
    }

    @Override
    public Optional<AssetCollection> findOne(Map<String, Object> criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Optional<AssetCollection> findOne(Criteria criteria) {
        return dao.findOne(criteria);
    }

    @Override
    protected AssetCollection createOrUpdate(AssetCollection assetCollection) {
        return assetCollectionDAO.save(assetCollection);
    }

    @Override
    public List<AssetCollection> create(List<AssetCollection> assetCollections) {
        assetCollections.forEach(assetCollection -> {assetCollection.setCreatedUser(getCurrentUser());assetCollection.setCreatedDateTime(LocalDateTime.now());});
        return assetCollectionDAO.insert(assetCollections);
    }

    @Override
    public List<AssetCollection> update(List<AssetCollection> assetCollections) {
        assetCollections.forEach(assetCollection -> {assetCollection.setLastModifiedUser(getCurrentUser());assetCollection.setLastModifiedDateTime(LocalDateTime.now());});
        return assetCollectionDAO.saveAll(assetCollections);
    }

    @Override
    public List<Map<String, Object>> getAssetsHierarchy(String collectionId, FindBy findBy, String nodeId,  boolean... activeRequired) {
        List<Map<String, Object>> hierarchy = new ArrayList<>();
        get(collectionId, findBy, activeRequired)
                .ifPresent(assetCollection -> {
                    List<VirtualFile> assets = assetDAO.getHierarchy(assetCollection.getRootId(), nodeId);
                    Map<String, String> lookUpMap = new HashMap<>();
                    assets.forEach(asset -> {
                        lookUpMap.put(asset.getId(), "N");
                        String parentId = asset.getParentDirectoryId();
                        lookUpMap.put(parentId, "Y");
                    });
                    List<String> parentChain = new ArrayList<>();
                    assets.forEach(asset -> {

                        String parentId = asset.getParentDirectoryId();
//                        String parentName = !parentId.isEmpty() ? lookUpMap.get(parentId) : "";
                        boolean isParent = lookUpMap.get(asset.getId()).equals("Y");
                        if(ValidationUtil.isNotEmpty(parentId)) {
                            if(!parentChain.contains(parentId)) {
                                parentChain.add(parentId);
                            } else {
                                while(!parentChain.get(parentChain.size() - 1).equals(parentId)) {
                                    parentChain.remove(parentChain.size() - 1);
                                }
                            }
                        }
                        Map<String, Object> assetMap = new HashMap<>();
                        assetMap.put("id", asset.getId());
                        assetMap.put("key", asset.getFileId());
                        assetMap.put("name", asset.getFileName());
                        assetMap.put("active", asset.getActive());
                        assetMap.put("isParent", isParent);
                        assetMap.put("parent", parentId.equals(nodeId) ? "0" : parentId);
                        assetMap.put("level", parentChain.size());
//                        asset.put("parentChain", StringUtil.concatinate(parentChain, "|"));
                        hierarchy.add(assetMap);
                    });
                });


        return hierarchy;
    }

    @Override
    public Optional<AssetCollection> getAssetCollection(String collectionName) {
        return assetCollectionDAO.findByCollectionName(collectionName);
    }
}
