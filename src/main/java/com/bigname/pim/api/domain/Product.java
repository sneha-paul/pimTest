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
    @NotEmpty(message = "Product Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    String productId;

    @Indexed(unique = true)
    @NotEmpty(message = "Product Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String productName;

    @NotEmpty(message = "Product Family cannot be empty", groups = {CreateGroup.class})
    private String productFamilyId;

    private String variantGroupId;

    @Transient
    private Family productFamily;

    private Map<String, Object> familyAttributes = new HashMap<>();

    public Product() {
        super();
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

    public String getProductFamilyId() {
        return productFamilyId;
    }

    public void setProductFamilyId(String productFamilyId) {
        this.productFamilyId = productFamilyId;
    }

    public String getVariantGroupId() {
        return variantGroupId;
    }

    public void setVariantGroupId(String variantGroupId) {
        this.variantGroupId = variantGroupId;
    }

    public Family getProductFamily() {
        return productFamily;
    }

    public void setProductFamily(Family productFamily) {
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
    public void orchestrate() {
        super.orchestrate();
        setDiscontinued(getDiscontinued());
        if (booleanValue(getActive()) && booleanValue(getDiscontinued())){
            setActive("N");
        }
    }

    @Override
    public Product merge(Product product) {
        switch(product.getGroup()) {
            case "DETAILS":
                this.setExternalId(product.getExternalId());
                this.setProductName(product.getProductName());
                this.setActive(product.getActive());
                this.setDiscontinued(product.getDiscontinued());
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
    public Product cloneInstance() {
        Product clone = new Product();
        clone.setActive("N");
        clone.setDiscontinued("N");
        clone.setExternalId(cloneValue(getExternalId()));
        clone.setProductName(cloneValue(getProductName()));
        clone.setProductFamilyId(cloneValue(getProductFamilyId()));
        return clone;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("externalId", getExternalId());
        map.put("productName", getProductName());
        map.put("productFamilyId", ValidationUtil.isEmpty(getProductFamily()) ? "" : getProductFamily().getExternalId());
        map.put("active", getActive());
        map.put("discontinued", getDiscontinued());
        return map;
    }
}
