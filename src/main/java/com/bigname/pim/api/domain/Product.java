package com.bigname.pim.api.domain;

import com.bigname.common.util.BeanUtil;
import com.bigname.common.util.CollectionsUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.util.PIMConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.index.Indexed;
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

    @Indexed(unique = true)
    @NotEmpty(message = "Product Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String productName;

    @NotEmpty(message = "Product Family cannot be empty", groups = {CreateGroup.class})
    private String productFamilyId;

    private String variantGroupId;

    @Transient @JsonIgnore
    private String channelId = PIMConstants.DEFAULT_CHANNEL_ID;

//    @Transient
//    private Channel channel;

    @Transient
    private Family productFamily;

    private Map<String, Object> familyAttributes = new HashMap<>();

    private Map<String, Map<String, Object>> scopedFamilyAttributes = new HashMap<>();

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

    public String getVariantGroupId() {
        return variantGroupId;
    }

    public void setVariantGroupId(String variantGroupId) {
        this.variantGroupId = variantGroupId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    /*public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }*/

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
        for (String group : product.getGroup()) {
            switch(group) {
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
//                if(booleanValue(familyAttribute.getScopable())) {
                    scopedFamilyAttributes.put(attributeId, attributeValue);
                /*} else {
                    scopedFamilyAttributes.remove(attributeId);
                    familyAttributes.put(attributeId, attributeValue);
                }*/
            }
        });
    }
}
