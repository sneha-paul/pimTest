package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.VirtualFile;

import java.util.List;
import java.util.Map;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface VirtualFileRepository extends GenericRepository<VirtualFile> {
    List<VirtualFile> getHierarchy(String rootDirectoryId, String nodeDirectoryId);
}
