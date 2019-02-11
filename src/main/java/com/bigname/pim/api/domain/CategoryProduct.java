package com.bigname.pim.api.domain;

import com.bigname.core.domain.EntityAssociation;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.ProductUtil;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.bigname.common.util.ValidationUtil.isNotEmpty;

/**
 * Created by sruthi on 26-09-2018.
 */
public class CategoryProduct extends EntityAssociation<Category, Product> {

    /**
     * Internal id of the Website document, not the externalId (websiteId) attribute
     */
    @Indexed
    private String categoryId;

    /**
     * Internal id of the Catalog document, not the externalId (catalogId) attribute
     */
    @Indexed
    private String productId;

    public CategoryProduct() {
        super();
    }

    public CategoryProduct(String categoryId, String productId, int subSequenceNum) {
        super();
        this.categoryId = categoryId;
        this.productId = productId;
        setSubSequenceNum(subSequenceNum);
        setActive("Y");
    }

    public CategoryProduct init(Category category, Product product) {
        super.init(category, product);
        return this;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public Map<String, Object> toMap(Map<String, Object> attributesMap) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("externalId", attributesMap.get("externalId"));
        map.put("productName", attributesMap.get("productName"));
        map.put("active", attributesMap.get("active"));
        map.put("sequenceNum", attributesMap.get("sequenceNum"));
        Map<String, Object> defaultAsset = ProductUtil.getDefaultAsset((Map<String, Object>)((Map<String, Object>)attributesMap.get("scopedAssets")).get(PIMConstants.DEFAULT_CHANNEL_ID), FileAsset.AssetFamily.ASSETS); //TODO - replace the hard coded channel ID in Phase2
        if(isNotEmpty(defaultAsset)) {
            map.put("imageName", (String) defaultAsset.get("internalName"));
        } else {
            map.put("imageName", "noimage.png");
        }
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryProduct that = (CategoryProduct) o;

        if (categoryId != null ? !categoryId.equals(that.categoryId) : that.categoryId != null) return false;
        return productId != null ? productId.equals(that.productId) : that.productId == null;

    }

    @Override
    public int hashCode() {
        int result = categoryId != null ? categoryId.hashCode() : 0;
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        return result;
    }
}
