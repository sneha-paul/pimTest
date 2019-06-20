package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.api.persistence.dao.VirtualFileDAO;
import com.m7.xtreme.xcore.service.mongo.BaseService;

import java.util.List;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface VirtualFileService extends BaseService<VirtualFile, VirtualFileDAO> {
    List<VirtualFile> getFiles(String directoryId);
    Optional<VirtualFile> getFile(String fileName, String directoryId);

}
