package com.bigname.pim.api.domain;

import com.bigname.pim.util.ProductUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.m7.xtreme.common.util.CollectionsUtil;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.xcore.domain.MongoEntity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sruthi on 20-09-2018.
 */
@Document
public class ProductVariant extends MongoEntity<ProductVariant> {

    @Transient
    @NotEmpty(message = "ProductVariant Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String productVariantId;

    //    @Indexed(unique = true)
    @NotEmpty(message = "ProductVariant Name cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String productVariantName;

    @NotEmpty(message = "Product Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String productId;

    @Transient
    private Product product;

    @Transient @JsonIgnore
    private int level;

    private Map<String, String> axisAttributes = new HashMap<>();

    private Map<String, Object> variantAttributes = new HashMap<>();

    private Map<String, Object> variantAssets = new HashMap<>();

    private Map<String, Map<Integer, BigDecimal>> pricingDetails = new HashMap<>();

    @NotEmpty(message = "Channel Id cannot be empty", groups = {CreateGroup.class, DetailsGroup.class})
    private String channelId;

    private long sequenceNum;
    private int subSequenceNum;

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

    public Map<String, Object> getVariantAssets() {
        variantAssets.forEach((family, assets) -> variantAssets.put(family, ProductUtil.orderAssets(ConversionUtil.toGenericMap((List<Object>)assets))));
        return variantAssets;
    }

    public void setVariantAssets(Map<String, Object> variantAssets) {
        this.variantAssets = variantAssets;
    }

    public Map<String, Map<Integer, BigDecimal>> getPricingDetails() {
        return pricingDetails;
    }

    public void setPricingDetails(Map<String, Map<Integer, BigDecimal>> pricingDetails) {
        this.pricingDetails = pricingDetails;
    }

    public Map<String, Object> getDefaultAsset() {
        return ProductUtil.getDefaultAsset(this, FileAsset.AssetFamily.ASSETS);
    }

    public long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public int getSubSequenceNum() {
        return subSequenceNum;
    }

    public void setSubSequenceNum(int subSequenceNum) {
        this.subSequenceNum = subSequenceNum;
    }

    protected void setExternalId() {
        this.productVariantId = getExternalId();
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
    public ProductVariant merge(ProductVariant productVariant) {
        for(String group : productVariant.getGroup()) {
            switch(group) {
                case "DETAILS":
                    this.setExternalId(productVariant.getExternalId());
                    this.setProductVariantName(productVariant.getProductVariantName());
                    this.setActive(productVariant.getActive());
                    mergeBaseProperties(productVariant);

                    break;
                case "ASSETS":
                    if(isNotEmpty(productVariant.getVariantAssets())) {
                        Map<String, Object> variantAssets = this.getVariantAssets();
                        productVariant.getVariantAssets().forEach(variantAssets::put);//TODO - check this logic
                    }
                    break;
                case "PRICING_DETAILS":
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
        // map.put("externalId", getExternalId());
        map.put("productVariantName", getProductVariantName());
        map.putAll(getBasePropertiesMap());
        //   map.put("active", getActive());
        Map<String, Object> defaultAsset = getDefaultAsset();
        if(isNotEmpty(defaultAsset)) {
            map.put("imageName", (String) defaultAsset.get("internalName"));
        } else {
            map.put("imageName", "noimage.png");
        }
        map.put("sequenceNum", String.valueOf(getSequenceNum()));
        return map;
    }

    public Map<String, Object> diff(ProductVariant productVariant, boolean... ignoreInternalId) {
        boolean _ignoreInternalId = ignoreInternalId != null && ignoreInternalId.length > 0 && ignoreInternalId[0];
        Map<String, Object> diff = new HashMap<>();
        if (!_ignoreInternalId && !this.getId().equals(productVariant.getId())) {
            diff.put("internalId", productVariant.getId());
        }
        if (!this.getProductVariantName().equals(productVariant.getProductVariantName())) {
            diff.put("productVariantName", productVariant.getProductVariantName());
        }
        if (!this.getProductId().equals(productVariant.getProductId())) {
            diff.put("productId", productVariant.getProductId());
        }
        Object axisAttributesDiff = CollectionsUtil.compareMaps(productVariant.getAxisAttributes(),this.getAxisAttributes());
        if(axisAttributesDiff.equals(""))
        {
            diff.put("axisAttributes", axisAttributesDiff);
        }

        Object variantAttributesDiff = CollectionsUtil.compareMaps(productVariant.getVariantAttributes(),this.getVariantAttributes());
        if(axisAttributesDiff.equals(""))
        {
            diff.put("variantAttributes", variantAttributesDiff);
        }
        Object variantAssetsDiff = CollectionsUtil.compareMaps(productVariant.getVariantAssets(),this.getVariantAssets());
        if(axisAttributesDiff.equals(""))
        {
            diff.put("variantAssets", variantAssetsDiff);
        }
        Object pricingDetailsDiff = CollectionsUtil.compareMaps(productVariant.getPricingDetails(),this.getPricingDetails());
        if(pricingDetailsDiff.equals(""))
        {
            diff.put("pricingDetails", pricingDetailsDiff);
        }
        if (!this.getChannelId().equals(productVariant.getChannelId())) {
            diff.put("channelId", productVariant.getChannelId());
        }

        return diff;
    }

    @Override
    public Object getCopy(ProductVariant productVariant) {
        ProductVariant _productVariant = new ProductVariant();
        _productVariant.setProductVariantName(productVariant.getProductVariantName());
        _productVariant.setProductVariantId(productVariant.getProductVariantId());
        _productVariant.setProductId(productVariant.getProductId());
        _productVariant.setAxisAttributes(productVariant.getAxisAttributes());
        _productVariant.setVariantAttributes(productVariant.getVariantAttributes());
        _productVariant.setVariantAssets(productVariant.getVariantAssets());
        _productVariant.setPricingDetails(productVariant.getPricingDetails());
        _productVariant.setChannelId(productVariant.getChannelId());
        _productVariant.setSubSequenceNum(productVariant.getSubSequenceNum());
        _productVariant.setSequenceNum(productVariant.getSequenceNum());
        _productVariant.setActive(productVariant.getActive());
        _productVariant.setArchived(productVariant.getArchived());
        _productVariant.setDiscontinued(productVariant.getDiscontinued());
        _productVariant.setVersionId(productVariant.getVersionId());
        _productVariant.setId(productVariant.getId());
        return _productVariant;
    }

    public interface SeoGroup {}
}
