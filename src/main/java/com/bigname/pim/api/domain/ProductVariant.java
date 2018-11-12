package com.bigname.pim.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sruthi on 20-09-2018.
 */
@Document
public class ProductVariant extends Entity<ProductVariant> {

    @Transient
    @NotEmpty(message = "ProductVariant Id cannot be empty", groups = {CreateGroup.class})
    private String productVariantId;

//    @Indexed(unique = true)
    @NotEmpty(message = "ProductVariant Name cannot be empty", groups = {CreateGroup.class})
    private String productVariantName;

    @NotEmpty(message = "Product Id cannot be empty", groups = {CreateGroup.class})
    private String productId;

    @Transient
    private Product product;

    @Transient @JsonIgnore
    private int level;

    private Map<String, String> axisAttributes = new HashMap<>();

    private Map<String, Object> variantAttributes = new HashMap<>();

    private Map<String, Map<String, BigDecimal>> pricingDetails = new HashMap<>();

    private String channelId;

    public ProductVariant() {
        super();
    }

    public ProductVariant(Product product) {
        super();
        setProduct(product);
    }

    public ProductVariant(String externalId, String productVariantName) {
        super(externalId);
        this.productVariantName = productVariantName;
    }

    public String getProductVariantId() {
        return getExternalId();
    }

    public void setProductVariantId(String productVariantId) {
        this.productVariantId = productVariantId;
        setExternalId(productVariantId);
    }

    public String getProductVariantName() {
        return productVariantName;
    }

    public void setProductVariantName(String productVariantName) {
        this.productVariantName = productVariantName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if(product != null) {
            setProductId(product.getId());
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Map<String, String> getAxisAttributes() {
        return axisAttributes;
    }

    public void setAxisAttributes(Map<String, String> axisAttributes) {
        this.axisAttributes = axisAttributes;
    }

    public Map<String, Object> getVariantAttributes() {
        return variantAttributes;
    }

    public void setVariantAttributes(Map<String, Object> variantAttributes) {
        this.variantAttributes = variantAttributes;
    }

    public Map<String, Map<String, BigDecimal>> getPricingDetails() {
        return pricingDetails;
    }

    public void setPricingDetails(Map<String, Map<String, BigDecimal>> pricingDetails) {
        this.pricingDetails = pricingDetails;
    }

    void setExternalId() {
        this.productVariantId = getExternalId();
    }

    @Override
    public ProductVariant merge(ProductVariant productVariant) {
        for(String group : productVariant.getGroup()) {
            switch(group) {
                case "DETAILS":
                    this.setExternalId(productVariant.getExternalId());
                    this.setProductVariantName(productVariant.getProductVariantName());
                    this.setActive(productVariant.getActive());
                break;
                case "ASSETS":

                break;
                case "PRICING":
                    if(isNotEmpty(productVariant.getPricingDetails())) {
                        productVariant.getPricingDetails().forEach(pricingDetails::put);
                    }
                break;
            }

            if(isNotEmpty(productVariant.getVariantAttributes())) {
                productVariant.getVariantAttributes().forEach(variantAttributes::put);
            }
        }

        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("productVariantName", getProductVariantName());
        map.put("active", getActive());
        return map;
    }

    public interface SeoGroup {}
}
