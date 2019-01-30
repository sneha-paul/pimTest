package com.bigname.pim.api.domain;


import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dona on 08-11-2018.
 */
public class PricingAttribute extends Entity<PricingAttribute> {

    @Transient
    @NotEmpty(message = "Pricing Attribute Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String pricingAttributeId;

    @Indexed(unique = true)
    @NotEmpty(message = "Pricing Attribute Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String pricingAttributeName;


    public PricingAttribute() {
        super();
    }


    public String getPricingAttributeId() {
        return getExternalId();
    }

    public void setPricingAttributeId(String pricingAttributeId) {
        this.pricingAttributeId = pricingAttributeId;
        setExternalId(pricingAttributeId);
    }

    public String getPricingAttributeName() {
        return pricingAttributeName;
    }

    public void setPricingAttributeName(String pricingAttributeName) {
        this.pricingAttributeName = pricingAttributeName;
    }

    @Override
    void setExternalId() {
        this.pricingAttributeId = getExternalId();
    }

    @Override
    public PricingAttribute cloneInstance() {
        PricingAttribute clone = new PricingAttribute();
        clone.setActive("N");
        clone.setExternalId(cloneValue(getExternalId()));
        clone.setPricingAttributeName(cloneValue(getPricingAttributeName()));
        return clone;
    }

    @Override
    public PricingAttribute merge(PricingAttribute pricingAttribute) {
        for (String group : pricingAttribute.getGroup()) {
            switch (group) {
                case "DETAILS":
                    this.setExternalId(pricingAttribute.getExternalId());
                    this.setPricingAttributeName(pricingAttribute.getPricingAttributeName());
                    this.setActive(pricingAttribute.getActive());
                    break;
            }
        }
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("pricingAttributeName", getPricingAttributeName());
        map.put("active", getActive());
        return map;
    }

    public Map<String, Object> diff(PricingAttribute pricingAttribute, boolean... ignoreInternalId) {
        boolean _ignoreInternalId = ignoreInternalId != null && ignoreInternalId.length > 0 && ignoreInternalId[0];
        Map<String, Object> diff = new HashMap<>();
        if (!_ignoreInternalId && !this.getId().equals(pricingAttribute.getId())) {
            diff.put("internalId", pricingAttribute.getId());
        }
        if (!this.getPricingAttributeName().equals(pricingAttribute.getPricingAttributeName())) {
            diff.put("pricingAttributeName", pricingAttribute.getPricingAttributeName());
        }
        return diff;
    }
}
