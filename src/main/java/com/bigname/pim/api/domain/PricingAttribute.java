package com.bigname.pim.api.domain;


import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dona on 08-11-2018.
 */
public class PricingAttribute extends Entity<PricingAttribute> {

    @Transient
    @NotEmpty(message = "Pricing Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String pricingId;

    @Indexed(unique = true)
    @NotEmpty(message = "Pricing name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String pricingName;



    public PricingAttribute() {
        super();
    }


    public String getPricingId() {return getExternalId();}

    public void setPricingId(String pricingId) {
        this.pricingId = pricingId;
        setExternalId(pricingId);
    }

    public String getPricingName() {return pricingName;}

    public void setPricingName(String pricingName) {this.pricingName = pricingName;}

    @Override
    void setExternalId() {this.pricingId=getExternalId();}

    @Override
    public PricingAttribute cloneInstance() {
        PricingAttribute clone = new PricingAttribute();
        clone.setActive("N");
        clone.setExternalId(cloneValue(getExternalId()));
        clone.setPricingName(cloneValue(getPricingName()));
        return clone;
    }

    @Override
    public PricingAttribute merge(PricingAttribute pricingAttribute) {
        for (String group : pricingAttribute.getGroup()) {
            switch(group) {
                case "DETAILS":
                    this.setExternalId(pricingAttribute.getExternalId());
                    this.setPricingName(pricingAttribute.getPricingName());
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
        map.put("pricingName", getPricingName());
        map.put("active", getActive());
        return map;
    }
}
