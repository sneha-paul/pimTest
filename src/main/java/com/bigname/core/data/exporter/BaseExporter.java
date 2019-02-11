package com.bigname.core.data.exporter;

import com.bigname.core.domain.Entity;
import com.bigname.core.service.BaseService;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface BaseExporter<T extends Entity, Service extends BaseService> {
    enum Type {
        XLSX(".xlsx"), JSON(".json");
        String ext;
        Type(String ext) {
            this.ext = ext;
        }

        public String getExt() {
            return ext;
        }
    }
    boolean exportData(String filePath);
    String getFileName(Type fileType);
}
