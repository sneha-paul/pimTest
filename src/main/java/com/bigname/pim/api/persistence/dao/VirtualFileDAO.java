package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.VirtualFile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface VirtualFileDAO extends BaseDAO<VirtualFile>, MongoRepository<VirtualFile, String>, VirtualFileRepository {
}
