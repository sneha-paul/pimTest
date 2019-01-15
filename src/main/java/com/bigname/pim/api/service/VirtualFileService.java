package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.VirtualFile;
import com.bigname.pim.api.persistence.dao.VirtualFileDAO;

import java.util.List;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface VirtualFileService extends BaseService<VirtualFile, VirtualFileDAO> {
    List<VirtualFile> getFiles(String directoryId);
}
