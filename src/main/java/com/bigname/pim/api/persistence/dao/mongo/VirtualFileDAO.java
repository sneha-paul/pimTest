package com.bigname.pim.api.persistence.dao.mongo;

import com.bigname.pim.api.domain.VirtualFile;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;

import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface VirtualFileDAO extends GenericDAO<VirtualFile>, VirtualFileRepository {
    Optional<VirtualFile> findByFileNameAndParentDirectoryId(String fileName, String parentDirectoryId);
}
