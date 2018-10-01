package com.bigname.pim.api.domain;

import com.bigname.common.util.BeanUtil;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sruthi on 19-09-2018.
 */
@Document
public class Product extends Entity<Product> {


    @Transient
    @NotEmpty(message = "Product Id cannot be empty", groups = {CreateGroup.class})
    String productId;

    @Indexed(unique = true)
    @NotEmpty(message = "Product Name cannot be empty", groups = {CreateGroup.class})
    private String productName;

    private String description;

    private String longDescription;

    private String metaTitle;

    private String metaDescription;

    private String metaKeywords;

    @NotEmpty(message = "Product Family cannot be empty", groups = {CreateGroup.class})
    private String productFamilyId;

    @Transient
    private ProductFamily productFamily;

    private Map<String, Object> familyAttributes = new HashMap<>();

    public Product() {
        super();
    }

    //TODO - check to see, if we need the overloaded constructor in all entities, since spring and jpa are using reflection to initialize the instance
    public Product(String externalId, String productName, String productFamilyId) {
        super(externalId);
        this.productName = productName;
        this.productFamilyId = productFamilyId;

    }

    public String getProductId() {
        return getExternalId();
    }

    public void setProductId(String productId) {
        this.productId = productId;
        setExternalId(productId);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    public String getProductFamilyId() {
        return productFamilyId;
    }

    public void setProductFamilyId(String productFamilyId) {
        this.productFamilyId = productFamilyId;
    }

    public ProductFamily getProductFamily() {
        return productFamily;
    }

    public void setProductFamily(ProductFamily productFamily) {
        this.productFamily = productFamily;
        setProductFamilyId(productFamily.getId());
    }

    public Map<String, Object> getFamilyAttributes() {
        return familyAttributes;
    }

    public void setFamilyAttributes(Map<String, Object> familyAttributes) {
        this.familyAttributes = CollectionsUtil.filterMap(familyAttributes, BeanUtil.getAllFieldNames(this.getClass()));
    }

    void setExternalId() {
        this.productId = getExternalId();
    }

    @Override
    public Product merge(Product product) {
        switch(product.getGroup()) {
            case "DETAILS":
                this.setExternalId(product.getExternalId());
                this.setProductName(product.getProductName());
                this.setDescription((product.getDescription()));
                this.setLongDescription((product.getLongDescription()));
                this.setActive(product.getActive());
                break;
            case "SEO":
                this.setMetaTitle(product.getMetaTitle());
                this.setMetaDescription(product.getMetaDescription());
                this.setMetaKeywords(product.getMetaKeywords());
                break;
            case "ASSETS":
                //TODO
                break;
        }
        if(isNotEmpty(product.getFamilyAttributes())) {
            product.getFamilyAttributes().forEach(this.getFamilyAttributes()::put);
        }
        return this;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("productName", getProductName());
        map.put("productFamilyId", ValidationUtil.isEmpty(getProductFamily()) ? "" : getProductFamily().getExternalId());
        map.put("active", getActive());
        return map;
    }

    public interface SeoGroup {}
}
