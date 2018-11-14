package com.bigname.pim.api.domain;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class PricingDetail extends ValidatableEntity<PricingDetail> {
    @NotEmpty(message = "Pricing Attribute cannot be empty")
    private String pricingAttributeId;
    private String channelId;
    private Map<Integer, BigDecimal> pricing = new TreeMap<>();

    public PricingDetail() {}

    public String getPricingAttributeId() {
        return pricingAttributeId;
    }

    public void setPricingAttributeId(String pricingAttributeId) {
        this.pricingAttributeId = pricingAttributeId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Map<Integer, BigDecimal> getPricing() {
        return pricing;
    }

    public void setPricing(Map<Integer, BigDecimal> pricing) {
        this.pricing = pricing;
    }
}
