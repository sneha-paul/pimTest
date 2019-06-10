package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.VirtualFile;
import com.m7.xcore.persistence.dao.GenericRepository;

import java.util.List;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface VirtualFileRepository extends GenericRepository<VirtualFile> {
    List<VirtualFile> getHierarchy(String rootDirectoryId, String nodeDirectoryId);
    List<VirtualFile> getFiles(String directoryId);
}
