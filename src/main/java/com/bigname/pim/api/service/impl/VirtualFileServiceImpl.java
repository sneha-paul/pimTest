package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.api.persistence.dao.AssetCollectionDAO;
import com.bigname.pim.api.persistence.dao.VirtualFileDAO;
import com.bigname.pim.api.service.VirtualFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Service
public class VirtualFileServiceImpl extends BaseServiceSupport<VirtualFile, VirtualFileDAO, VirtualFileService> implements VirtualFileService {

    private VirtualFileDAO virtualFileDAO;
    private AssetCollectionDAO assetCollectionDAO;

    @Autowired
    public VirtualFileServiceImpl(VirtualFileDAO virtualFileDAO, Validator validator, AssetCollectionDAO assetCollectionDAO) {
        super(virtualFileDAO, "virtualFile", validator);
        this.virtualFileDAO = virtualFileDAO;
        this.assetCollectionDAO = assetCollectionDAO;
    }

    @Override
    public List<VirtualFile> findAll(Map<String, Object> criteria) {
//        return dao.findAll(criteria);
        return null;
    }

    @Override
    public List<VirtualFile> findAll(Criteria criteria) {
//        return dao.findAll(criteria);
        return null;
    }

    @Override
    public Optional<VirtualFile> findOne(Map<String, Object> criteria) {
//        return dao.findOne(criteria);
        return null;
    }

    @Override
    public Optional<VirtualFile> findOne(Criteria criteria) {
//        return dao.findOne(criteria);
        return null;
    }

    @Override
    protected VirtualFile createOrUpdate(VirtualFile virtualFile) {
        return virtualFileDAO.save(virtualFile);
    }
}
