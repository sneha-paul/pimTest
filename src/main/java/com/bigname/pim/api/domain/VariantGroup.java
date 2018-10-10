package com.bigname.pim.api.domain;

import java.util.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class VariantGroup extends Entity<VariantGroup> {

    private String variantGroupName;
    private String active = "N";

    @Override
    void setExternalId() {

    }

    @Override
    public VariantGroup merge(VariantGroup variantGroup) {
        return null;
    }

    @Override
    public Map<String, String> toMap() {
        return null;
    }
}
