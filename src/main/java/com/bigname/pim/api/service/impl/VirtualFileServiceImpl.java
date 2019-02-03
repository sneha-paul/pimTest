package com.bigname.pim.api.service.impl;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.core.service.BaseServiceSupport;
import com.bigname.core.util.FindBy;
import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.api.persistence.dao.AssetCollectionDAO;
import com.bigname.pim.api.persistence.dao.VirtualFileDAO;
import com.bigname.pim.api.service.VirtualFileService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.time.LocalDateTime;
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
        return dao.findAll(criteria);
    }

    @Override
    public List<VirtualFile> findAll(Criteria criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public Optional<VirtualFile> findOne(Map<String, Object> criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Optional<VirtualFile> findOne(Criteria criteria) {
        return dao.findOne(criteria);
    }

    @Override
    protected VirtualFile createOrUpdate(VirtualFile virtualFile) {
        return virtualFileDAO.save(virtualFile);
    }

    @Override
    public List<VirtualFile> create(List<VirtualFile> virtualFiles) {
        virtualFiles.forEach(virtualFile -> {virtualFile.setCreatedUser(getCurrentUser());virtualFile.setCreatedDateTime(LocalDateTime.now());});
        return virtualFileDAO.insert(virtualFiles);
    }

    @Override
    public List<VirtualFile> update(List<VirtualFile> virtualFiles) {
        virtualFiles.forEach(virtualFile -> {virtualFile.setLastModifiedUser(getCurrentUser());virtualFile.setLastModifiedDateTime(LocalDateTime.now());});
        return virtualFileDAO.saveAll(virtualFiles);
    }

    @Override
    public List<VirtualFile> getFiles(String directoryId) {
        return virtualFileDAO.getFiles(directoryId);
    }

    @Override
    public Optional<VirtualFile> getFile(String fileName, String directoryId) {
        return virtualFileDAO.findByFileNameAndParentDirectoryId(fileName, directoryId);
    }

    @Override
    public Map<String, Pair<String, Object>> validate(Map<String, Object> context, Map<String, Pair<String, Object>> fieldErrors, VirtualFile virtualFile, String group) {
        Map<String, Pair<String, Object>> _fieldErrors = super.validate(context, fieldErrors, virtualFile, group);
        VirtualFile existing = ValidationUtil.isNotEmpty(context.get("id")) ? get((String)context.get("id"), FindBy.EXTERNAL_ID, false).orElse(null) : null;
        if(ValidationUtil.isEmpty(context.get("id")) || (existing != null && !existing.getFileName().equals(virtualFile.getFileName()))) {
            findOne(CollectionsUtil.toMap("parentDirectoryId", virtualFile.getParentDirectoryId(), "fileName", virtualFile.getFileName().trim(), "isDirectory", virtualFile.getIsDirectory()))
                    .ifPresent(virtualFile1 -> fieldErrors.put("fileName", Pair.with("A " + (virtualFile.getIsDirectory().equals("Y") ? "directory" : "file") + " with the given name already exists", virtualFile.getFileName())));
        }
        return _fieldErrors;
    }
}
