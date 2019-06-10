package com.bigname.pim.api.domain;

import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.ProductUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.m7.common.util.BeanUtil;
import com.m7.common.util.CollectionsUtil;
import com.m7.common.util.ConversionUtil;
import com.m7.common.util.ValidationUtil;
import com.m7.xcore.domain.Entity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sruthi on 19-09-2018.
 */
@Document
public class Product extends Entity<Product> {


    @Transient
    @NotEmpty(message = "Product Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    String productId;

    //    @Indexed(unique = true)
    @NotEmpty(message = "Product Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String productName;

    @NotEmpty(message = "Product Family cannot be empty", groups = {CreateGroup.class})
    private String productFamilyId;

    //@NotEmpty(message = "ChannelId is required", groups = {CreateGroup.class, DetailsGroup.class})
    @Transient @JsonIgnore
    private String channelId = PIMConstants.DEFAULT_CHANNEL_ID; //TODO - should remove this default and make sure channelId is passed correctly whenever required.

    @Transient
    private Family productFamily;

    private Map<String, Map<String, Object>> scopedFamilyAttributes = new HashMap<>();

    private Map<String, Map<String, Object>> scopedAssets = new HashMap<>();

    public Product() {
        super();
    }

    public Product(String channelId) {
        this();
        this.channelId = channelId;
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

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Family getProductFamily() {
        return productFamily;
    }

    public void setProductFamily(Family productFamily) {
        this.productFamily = productFamily;
        setProductFamilyId(productFamily.getId());
    }

    public Map<String, Map<String, Object>> getScopedFamilyAttributes() {
        return scopedFamilyAttributes;
    }

    public void setScopedFamilyAttributes(String channelId, Map<String, Object> familyAttributes) {
        getScopedFamilyAttributes().put(channelId, CollectionsUtil.filterMap(familyAttributes, BeanUtil.getAllFieldNames(this.getClass())));
    }

    public Map<String, Object> getChannelFamilyAttributes() {
        return scopedFamilyAttributes.get(getChannelId());
    }

    public void setChannelFamilyAttributes(Map<String, Object> familyAttributes) {
        setScopedFamilyAttributes(getChannelId(), familyAttributes);
    }

    public Map<String, Map<String, Object>> getScopedAssets() {
        scopedAssets.forEach((channelId, assetFamilies) -> assetFamilies.forEach((family, assets) -> assetFamilies.put(family, ProductUtil.orderAssets(ConversionUtil.toGenericMap((List<Object>)assets)))));
        return scopedAssets;
    }

    public void setScopedAssets(String channelId, Map<String, Object> assets) {
        getScopedAssets().put(channelId, assets); //TODO - check this logic
    }

    public Map<String, Object> getChannelAssets() {
        return getScopedAssets().get(getChannelId());
    }

    public void setChannelAssets(Map<String, Object> assets) {
        setScopedAssets(getChannelId(), assets);
    }

    public Map<String, Object> getDefaultAsset() {
        return ProductUtil.getDefaultAsset(this, FileAsset.AssetFamily.ASSETS);
    }

    protected void setExternalId() {
        this.productId = getExternalId();
    }

    @Override
    public void orchestrate() {
        super.orchestrate();
        // setDiscontinued(getDiscontinued());
       /* if (booleanValue(getActive()) && booleanValue(getDiscontinued())){
            setActive("N");
        }*/
    }

    @Override
    public Product merge(Product product) {
        this.setChannelId(product.getChannelId());
        for (String group : product.getGroup()) {
            switch(group) {
                case "DETAILS":
                    this.setExternalId(product.getExternalId());
                    this.setProductName(product.getProductName());
                    this.setActive(product.getActive());
                    mergeBaseProperties(product);
                    break;

                case "ASSETS":
                    if(isNotEmpty(product.getChannelAssets())) {
                        if(!this.getScopedAssets().containsKey(product.getChannelId())) {
                            this.getScopedAssets().put(product.getChannelId(), new HashMap<>());
                        }
                        Map<String, Object> channelAssets = this.getChannelAssets();
                        product.getChannelAssets().forEach(channelAssets::put);
                    }
                    break;
            }
        }
        if(isNotEmpty(product.getChannelFamilyAttributes())) {
            if(!this.getScopedFamilyAttributes().containsKey(product.getChannelId())) {
                this.getScopedFamilyAttributes().put(product.getChannelId(), new HashMap<>());
            }
            Map<String, Object> channelFamilyAttributes = this.getChannelFamilyAttributes();
            product.getChannelFamilyAttributes().forEach(channelFamilyAttributes::put);
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
        //  map.put("externalId", getExternalId());
        map.put("productName", getProductName());
        map.put("productFamilyId", ValidationUtil.isEmpty(getProductFamily()) ? "" : getProductFamily().getExternalId());
        /// map.put("active", getActive());
        Map<String, Object> defaultAsset = getDefaultAsset();
        if(isNotEmpty(defaultAsset)) {
            map.put("imageName", (String) defaultAsset.get("internalName"));
        } else {
            map.put("imageName", "noimage.png");
        }
        map.putAll(getBasePropertiesMap());
        return map;
    }

    public void setAttributeValues(Map<String, Object> attributeValues) {
        String channelId = getChannelId();
        Family family = getProductFamily();
        Map<String, FamilyAttribute> familyAttributesMap = FamilyAttributeGroup.getAllAttributesMap(family);
        if(!getScopedFamilyAttributes().containsKey(channelId)) {
            setScopedFamilyAttributes(channelId, new HashMap<>());
        }

        Map<String, Object> scopedFamilyAttributes = getScopedFamilyAttributes().get(channelId);

        attributeValues.forEach((attributeId, attributeValue) -> {
            if(familyAttributesMap.containsKey(attributeId)) {
                FamilyAttribute familyAttribute = familyAttributesMap.get(attributeId);
                if(familyAttribute.getUiType() == Attribute.UIType.MULTI_SELECT) {
                   if(attributeValue instanceof String) {
                       attributeValue = new String[]{(String)attributeValue};
                   }
                }
                scopedFamilyAttributes.put(attributeId, attributeValue);
//                if(booleanValue(familyAttribute.getScopable())) {

                /*} else {
                    scopedFamilyAttributes.remove(attributeId);
                    familyAttributes.put(attributeId, attributeValue);
                }*/
            }
        });
    }

    public Map<String, Object> diff(Product product, boolean... ignoreInternalId) {
        boolean _ignoreInternalId = ignoreInternalId != null && ignoreInternalId.length > 0 && ignoreInternalId[0];
        Map<String, Object> diff = new HashMap<>();
        if (!_ignoreInternalId && !this.getId().equals(product.getId())) {
            diff.put("internalId", product.getId());
        }
        if (!this.getProductName().equals(product.getProductName())) {
            diff.put("productName", product.getProductName());
        }
        if (!this.getProductFamilyId().equals(product.getProductFamilyId())) {
            diff.put("productFamilyId", product.getProductFamilyId());
        }
        if (!this.getChannelId().equals(product.getChannelId())) {
            diff.put("channelId", product.getChannelId());
        }
        Object scopedFamilyAttributesDiff = CollectionsUtil.compareMaps(product.getScopedFamilyAttributes(),this.getScopedFamilyAttributes());
        if(scopedFamilyAttributesDiff.equals(""))
        {
            diff.put("scopedFamilyAttributes", scopedFamilyAttributesDiff);
        }
        if (!this.getScopedAssets().equals(product.getScopedAssets())) {
            diff.put("scopedAssets", product.getScopedAssets());
        }

        return diff;
    }
}
