package com.bigname.pim.api.persistence.dao;

import com.bigname.core.persistence.dao.GenericDAO;
import com.bigname.pim.api.domain.VirtualFile;

import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface VirtualFileDAO extends GenericDAO<VirtualFile>, VirtualFileRepository {
    Optional<VirtualFile> findByFileNameAndParentDirectoryId(String fileName, String parentDirectoryId);
}
