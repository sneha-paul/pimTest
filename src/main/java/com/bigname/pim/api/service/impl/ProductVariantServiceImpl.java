package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.persistence.dao.ProductDAO;
import com.bigname.pim.api.persistence.dao.ProductVariantDAO;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.api.service.VirtualFileService;
import com.bigname.pim.util.PIMConstants;
import com.bigname.pim.util.ProductUtil;
import com.google.common.base.Preconditions;
import com.m7.xtreme.common.util.ConversionUtil;
import com.m7.xtreme.common.util.PimUtil;
import com.m7.xtreme.xcore.domain.Entity;
import com.m7.xtreme.xcore.exception.EntityNotFoundException;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import com.m7.xtreme.xcore.util.FindBy;
import com.m7.xtreme.xcore.util.ID;
import com.m7.xtreme.xcore.util.Toggle;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

import static com.bigname.pim.util.PIMConstants.ReorderingDirection.DOWN;
import static com.bigname.pim.util.PIMConstants.ReorderingDirection.UP;
import static com.m7.xtreme.common.util.ValidationUtil.isNotEmpty;

/**
 * Created by sruthi on 20-09-2018.
 */

@Service
public class ProductVariantServiceImpl extends BaseServiceSupport<ProductVariant, ProductVariantDAO, ProductVariantService> implements ProductVariantService {

    private VirtualFileService assetService;
    private ProductVariantDAO productVariantDAO;
    private ProductDAO productDAO;


    @Autowired
    public ProductVariantServiceImpl(ProductVariantDAO productVariantDAO, Validator validator, ProductDAO productDAO, VirtualFileService assetService) {
        super(productVariantDAO, "productVariant", validator);
        this.assetService = assetService;
        this.productVariantDAO = productVariantDAO;
        this.productDAO = productDAO;
    }

    @Override
    public Page<ProductVariant> findAll(String searchField, String keyword, ID<String> productId, String channelId, Pageable pageable, boolean... activeRequired) {
        return productDAO.findById(productId)
                .map(product -> productVariantDAO.findAll(searchField, keyword, product.getId(), channelId, pageable, activeRequired))
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Override
    public <I> ProductVariant update(ID<I> productVariantId, ProductVariant productVariant) {
        //Variant cannot be loaded using the externalId alone at the base layer. So if the id is EXTERNAL, convert it to the INTERNAL one
        if(productVariantId.isExternalId()) {
            Optional<ProductVariant> _variant = get(ID.INTERNAL_ID(productVariant.getProductId()), productVariant.getChannelId(), ID.EXTERNAL_ID(productVariantId.getId()), false);
            if(_variant.isPresent()) {
                productVariantId = ID.INTERNAL_ID((I)_variant.get().getId());
            } else {
                throw new EntityNotFoundException("Unable to find product variant with variantId:" + productVariantId.getId() + ", productId:" + productVariant.getProductId() + ", channelId:" + productVariant.getChannelId());
            }
        } else {
            Preconditions.checkState(productVariantId.getId().equals(productVariant.getId()), "Illegal operation");
        }
        return super.update(productVariantId, productVariant);
    }

    /*@Override
    protected ProductVariant createOrUpdate(ProductVariant productVariant) {
        return productVariantDAO.save(productVariant);
    }*/

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public <I> Optional<ProductVariant> get(ID<I> productVariantId, boolean... activeRequired) {
        return productVariantId.isInternalId() ? super.get(productVariantId, activeRequired) : Optional.empty();
    }

    @Override
    public <I> Optional<ProductVariant> get(ID<I> productId, String channelId, ID<I> productVariantId, boolean... activeRequired) {
        if(productVariantId.isInternalId()) {
            return get(productVariantId, activeRequired);
        } else {
            Optional<Product> _product = productId.isInternalId() ? productDAO.findById(productId) : productDAO.findByExternalId(productId);
            if(_product.isPresent()) {
                productId = ID.INTERNAL_ID((I)_product.get().getId());
            } else {
                return Optional.empty();
            }
            return productVariantDAO.findByProductIdAndChannelIdAndExternalIdAndActiveIn(productId.getId(), channelId, productVariantId.getId(), PimUtil.getActiveOptions(activeRequired));
        }
    }

    @Override
    public Optional<ProductVariant> get(ID<String> productVariantId, String channelId, boolean... activeRequired) {
        return productVariantId.isInternalId() ? productVariantDAO.findByIdAndChannelIdAndActiveIn(productVariantId.getId(), channelId, PimUtil.getActiveOptions(activeRequired)) : productVariantDAO.findByExternalIdAndChannelIdAndActiveIn(productVariantId.getId(), channelId, PimUtil.getActiveOptions(activeRequired));
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public <I> boolean toggle(ID<I> productVariantId, Toggle active) {
        return productVariantId.isInternalId() && super.toggle(productVariantId, active);
    }

    @Override
    public boolean toggle(ID<String> productId, String channelId, ID<String> productVariantId, Toggle active) {
        Optional<ProductVariant> _variant = get(productId, channelId, productVariantId,false);

        if(_variant.isPresent()) {
            ProductVariant variant = _variant.get();
            variant.setActive(active.state());
            productVariantDAO.save(variant);
            return true;
        } else {
            return false;
        }
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public <I> ProductVariant cloneInstance(ID<I> productVariantId, Entity.CloneType type) {
        return productVariantId.isInternalId() ? super.cloneInstance(productVariantId, type) : null;
    }

    @Override
    public ProductVariant cloneInstance(ID<String> productId, String channelId, ID<String> productVariantId, Entity.CloneType type) {
        return get(productId, channelId, productVariantId, false)
                .map(productVariant -> cloneInstance(ID.INTERNAL_ID(productVariant.getId()), type)).orElse(null);
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public Page<ProductVariant> getAll(int page, int size, Sort sort, boolean... activeRequired) {
        return new PageImpl<>(new ArrayList<>()); //IMPORTANT - Purposely returning empty page
    }

    @Override
    public Page<ProductVariant> getAll(ID<String> productId,  String channelId, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            //sort = new Sort(Sort.Direction.ASC, "externalId");
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Optional<Product> _product = productId.isInternalId() ? productDAO.findById(productId.getId()) : productDAO.findByExternalId(productId);
        if(_product.isPresent()) {
            productId = ID.INTERNAL_ID(_product.get().getId());
        } else {
            return null;
        }
        return productVariantDAO.findByProductIdAndChannelIdAndActiveIn(productId.getId(), channelId, PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
    }

    @Override
    public List<ProductVariant> getAll(List<ID<String>> productIds,  String channelId, boolean... activeRequired) {
        if(productIds.size() == 0) {
            return new ArrayList<>();
        }
        if(productIds.get(0).isExternalId()) {
            List<Product> products = productDAO.findByExternalIdInAndActiveIn(productIds.stream().map(ID::getId).collect(Collectors.toList()), PimUtil.getActiveOptions(activeRequired));
            productIds = products.stream().map(product -> ID.INTERNAL_ID(product.getId())).collect(Collectors.toList());
            if(productIds.size() == 0) {
                return new ArrayList<>();
            }
        }
        return productVariantDAO.findByProductIdInAndChannelIdAndActiveIn(productIds.stream().map(ID::getId).collect(Collectors.toList()), channelId, PimUtil.getActiveOptions(activeRequired));
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public List<ProductVariant> getAll(Sort sort, boolean... activeRequired) {
        return new ArrayList<>(); //IMPORTANT - Purposely returning empty list
    }

    @Override
    public List<ProductVariant> getAll(ID<String> productId, String channelId, Sort sort, boolean... activeRequired) {
        return getAll(productId, channelId, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public <I> Page<ProductVariant> getAll(List<ID<I>> productVariantIds, int page, int size, Sort sort, boolean... activeRequired) {
        if(isEmpty(productVariantIds)) {
            return new PageImpl<>(new ArrayList<>());
        }
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        return productVariantIds.get(0).isInternalId() ? productVariantDAO.findByIdInAndActiveIn(productVariantIds, PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort)) : new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<ProductVariant> getAll(ID<String> productId, String channelId, List<ID<String>> productVariantIds, int page, int size, Sort sort, boolean... activeRequired) {
        if(isEmpty(productVariantIds)) {
            return new PageImpl<>(new ArrayList<>());
        }
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        if(productVariantIds.get(0).isInternalId()) {
            return getAll(productVariantIds, page, size, sort, activeRequired);
        } else {
            if(productId.isExternalId()) {
                Optional<Product> _product = productDAO.findByExternalId(productId);
                if(_product.isPresent()) {
                    productId = ID.INTERNAL_ID(_product.get().getId());
                } else {
                    return new PageImpl<>(new ArrayList<>());
                }
            }
            return productVariantDAO.findByProductIdAndChannelIdAndExternalIdInAndActiveIn(productId.getId(), channelId, productVariantIds.stream().map(ID::getId).collect(Collectors.toList()), PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
        }
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public <I> List<ProductVariant> getAll(List<ID<I>> productVariantIds, Sort sort, boolean... activeRequired) {
        if(isEmpty(productVariantIds) || productVariantIds.get(0).isExternalId()) {
            return new ArrayList<>();
        }
        return getAll(productVariantIds, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    @Override
    public List<ProductVariant> getAll(ID<String> productId, String channelId, List<ID<String>> productVariantIds, Sort sort, boolean... activeRequired) {
        return getAll(productId, channelId, productVariantIds, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public <I> Page<ProductVariant> getAllWithExclusions(List<ID<I>> excludedIds, int page, int size, Sort sort, boolean... activeRequired) {
        return new PageImpl<>(new ArrayList<>()); //IMPORTANT - Purposely returning empty page
    }

    @Override
    public Page<ProductVariant> getAllWithExclusions(ID<String> productId, String channelId, List<ID<String>> excludedVariantIds, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        if(isNotEmpty(excludedVariantIds) && excludedVariantIds.get(0).isInternalId()) {
            return getAllWithExclusions(excludedVariantIds, page, size, sort, activeRequired);
        } else {
            if(isNull(excludedVariantIds)) {
                excludedVariantIds = new ArrayList<>();
            }
            if(productId.isExternalId()) {
                Optional<Product> _product = productDAO.findByExternalId(productId.getId());
                if(_product.isPresent()) {
                    productId = ID.INTERNAL_ID(_product.get().getId());
                } else {
                    return new PageImpl<>(new ArrayList<>());
                }
            }
            return productVariantDAO.findByProductIdAndChannelIdAndExternalIdNotInAndActiveIn(productId.getId(), channelId, excludedVariantIds.stream().map(ID::getId).collect(Collectors.toList()), PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
        }
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public <I> List<ProductVariant> getAllWithExclusions(List<ID<I>> excludedIds, Sort sort, boolean... activeRequired) {
        return new ArrayList<>(); //IMPORTANT - Purposely returning empty list
    }

    @Override
    public List<ProductVariant> getAllWithExclusions(ID<String> productId, String channelId, List<ID<String>> excludedVariantIds, Sort sort, boolean... activeRequired) {
        return getAllWithExclusions(productId, channelId, excludedVariantIds, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    @Override
    public Map<String, Pair<String, Object>> validate(Map<String, Object> context, Map<String, Pair<String, Object>> fieldErrors, ProductVariant productVariant, String group) {
        Map<String, Pair<String, Object>> _fieldErrors = super.validate(context, fieldErrors, productVariant, group);
        FamilyAttributeGroup masterGroup = productVariant.getProduct().getProductFamily().getAttributes().get(group + "_GROUP");
        if(isNotEmpty(masterGroup)) {
            FamilyAttributeGroup sectionGroup = masterGroup.getChildGroups().get(AttributeGroup.DEFAULT_GROUP_ID);
            sectionGroup.getChildGroups().forEach((k, attributeGroup) ->
                attributeGroup.getAttributes().forEach((k1, attribute) -> {
                    if(attribute.getType(productVariant.getChannelId()) == FamilyAttribute.Type.VARIANT) {
                        if (attribute.getUiType() == Attribute.UIType.CHECKBOX && !productVariant.getVariantAttributes().containsKey(k1)) {
                            productVariant.getVariantAttributes().put(k1, new String[0]);
                        } else if (attribute.getUiType() == Attribute.UIType.YES_NO && !productVariant.getVariantAttributes().containsKey(k1)) {
                            productVariant.getVariantAttributes().put(k1, "N");
                        }
                    }
                    Pair<String, Object> error = attribute.validate(productVariant.getVariantAttributes().get(attribute.getId()), productVariant.getChannelId(), productVariant.getLevel());
                    //TODO - validate nested attribute values
                    if(isNotEmpty(error)) {
                        _fieldErrors.put(attribute.getId(), error);
                    }
                })
            );
        }
        return _fieldErrors;
    }

    @Override
    public Page<ProductVariant> getProductVariantPricing(ID<String> productId, String channelId, ID<String> productVariantId, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<ProductVariant> _productVariant = get(productId, channelId, productVariantId, false);
        if(_productVariant.isPresent()) {
            ProductVariant productVariant = _productVariant.get();
            Page<ProductVariant> productVariants = productVariantDAO.findByProductIdAndChannelIdAndProductVariantIdAndActiveIn(productVariant.getProductId(), productVariant.getChannelId(), productVariant.getId(), PimUtil.getActiveOptions(activeRequired), pageable);

            return productVariants;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public ProductVariant addAssets(ID<String> productId, String channelId, ID<String> productVariantId, List<ID<String>> assetIds, FileAsset.AssetFamily assetFamily) {
        return get(productId, channelId, productVariantId, false)
                .map(productVariant -> {

                    //Existing variant assets
                    Map<String, Object> variantAssetsMap = productVariant.getVariantAssets();

                    String _assetFamily = assetFamily.name();
                    //Get the assets list corresponding to the given assetFamily
                    List<Object> _variantAssets = variantAssetsMap.containsKey(_assetFamily) ? (List<Object>)variantAssetsMap.get(_assetFamily) : new ArrayList<>();

                    //order the list by sequenceNum ascending
                    List<Map<String, Object>> variantAssets = ProductUtil.orderAssets(ConversionUtil.toGenericMap(_variantAssets));

                    //List of all existing asset ids
                    List<String> existingAssetIds = new ArrayList<>();

                    //reassign the sequenceNum, so that they are continuous
                    int[] seq = {0};

                    variantAssets.forEach(asset -> {
                        asset.put("sequenceNum", seq[0] ++);
                        //Add the id to the existing ids list
                        existingAssetIds.add((String)asset.get("id"));
                    });


                    assetService.getAll(assetIds, null)
                            .forEach(asset -> {
                                //Only add, if the asset is a file, not a directory
                                if(!"Y".equals(asset.getIsDirectory()) && "Y".equals(asset.getActive())) {
                                    //Only add, if the asset won't exists already
                                    if(!existingAssetIds.contains(asset.getId())) {
                                        FileAsset variantAsset = new FileAsset(asset, seq[0] ++);
                                        variantAssets.add(variantAsset.toMap());
                                        existingAssetIds.add(variantAsset.getId());
                                    }
                                }
                            });


                    ProductUtil.validateDefaultAsset(variantAssets);
                    variantAssetsMap.put(_assetFamily, variantAssets);
                    productVariant.setVariantAssets(variantAssetsMap);
                    productVariant.setGroup("ASSETS");
                    update(productVariantId, productVariant);
                    return productVariant;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product variant with id:" + productVariantId));
    }

    @Override
    public ProductVariant deleteAsset(ID<String> productId, String channelId, ID<String> productVariantId, String assetId, FileAsset.AssetFamily assetFamily) {
        return get(productId, channelId, productVariantId,  false)
                .map(productVariant -> {
                    //Existing variant assets
                    Map<String, Object> variantAssetsMap = productVariant.getVariantAssets();

                    String _assetFamily = assetFamily.name();
                    //Get the assets list corresponding to the given assetFamily
                    List<Object> _variantAssets = variantAssetsMap.containsKey(_assetFamily) ? (List<Object>)variantAssetsMap.get(_assetFamily) : new ArrayList<>();

                    List<Map<String, Object>> variantAssets = ConversionUtil.toGenericMap(_variantAssets);

                    variantAssetsMap.put(_assetFamily, ProductUtil.deleteAsset(variantAssets, assetId));
                    productVariant.setVariantAssets(variantAssetsMap);
                    productVariant.setGroup("ASSETS");
                    update(productVariantId, productVariant);
                    return productVariant;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product variant with id:" + productId));
    }

    @Override
    public ProductVariant reorderAssets(ID<String> productId, String channelId, ID<String> productVariantId, String[] assetIds, FileAsset.AssetFamily assetFamily) {
        return get(productId, channelId, productVariantId, false)
                .map(productVariant -> {
                    //Existing variant assets
                    Map<String, Object> variantAssetsMap = productVariant.getVariantAssets();

                    String _assetFamily = assetFamily.name();
                    //Get the assets list corresponding to the given assetFamily
                    List<Object> variantAssets = variantAssetsMap.containsKey(_assetFamily) ? (List<Object>)variantAssetsMap.get(_assetFamily) : new ArrayList<>();

                    //AssetIds arrays contains the assetIds in the required order
                    variantAssetsMap.put(_assetFamily, ProductUtil.reorderAssets(ConversionUtil.toGenericMap(variantAssets), Arrays.asList(assetIds)));
                    productVariant.setVariantAssets(variantAssetsMap);
                    productVariant.setGroup("ASSETS");
                    update(productVariantId, productVariant);
                    return productVariant;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product variant with id:" + productId));

    }

    @Override
    public ProductVariant setAsDefaultAsset(ID<String> productId, String channelId, ID<String> productVariantId, String assetId, FileAsset.AssetFamily assetFamily) {
        return get(productId, channelId, productVariantId, false)
                .map(productVariant -> {
                    //Existing variant assets
                    Map<String, Object> variantAssetsMap = productVariant.getVariantAssets();

                    String _assetFamily = assetFamily.name();
                    //Get the assets list corresponding to the given assetFamily
                    List<Object> variantAssets = variantAssetsMap.containsKey(_assetFamily) ? (List<Object>)variantAssetsMap.get(_assetFamily) : new ArrayList<>();


                    ProductUtil.setDefaultAsset(ConversionUtil.toGenericMap(variantAssets), assetId);
                    variantAssetsMap.put(_assetFamily, variantAssets);
                    productVariant.setVariantAssets(variantAssetsMap);
                    productVariant.setGroup("ASSETS");
                    update(productVariantId, productVariant);
                    return productVariant;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product variant with id:" + productId));
    }


    /**
     * Method to get productVariants of a Product in paginated format.
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getAll() {
        return productVariantDAO.getAll();
    }

    @Override
    public ProductVariant create(ProductVariant productVariant) {
        Optional<ProductVariant> top = productVariantDAO.findTopByProductIdAndChannelIdAndSequenceNumOrderBySubSequenceNumDesc(productVariant.getProductId(), productVariant.getChannelId(), 0);
        productVariant.setSubSequenceNum(top.map(productVariant1 -> productVariant1.getSubSequenceNum() + 1).orElse(0));
        return super.create(productVariant);
    }

    @Override
    public boolean setProductVariantsSequence(ID<String> productId, ID<String> channelId, ID<String> sourceId, ID<String> destinationId) { //TODO - do direct look up of source and destination variants, without loading the full variants

        List<ID<String>> ids = new ArrayList<>();
        ids.add(sourceId);
        ids.add(destinationId);
        return get(productId, channelId.getId(),  false)
                .map(product -> {
                    Map<String, ProductVariant> productVariantsMap = getAll(productId, channelId.getId(), ids, null, false)
                            .stream().collect(Collectors.toMap(ProductVariant::getProductVariantId, productVariant1 -> productVariant1));

                    ProductVariant source = productVariantsMap.get(sourceId.getId());
                    ProductVariant destination = productVariantsMap.get(destinationId.getId());

                    PIMConstants.ReorderingDirection direction = DOWN;
                    if(source.getSequenceNum() > destination.getSequenceNum() ||
                            (source.getSequenceNum() == destination.getSequenceNum() && source.getSubSequenceNum() <= destination.getSubSequenceNum())) {
                        direction = UP;
                    }
                    List<ProductVariant> modifiedProductVariants = new ArrayList<>();
                    if(direction == DOWN) {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum());
                        modifiedProductVariants.add(source);
                        destination.setSubSequenceNum(source.getSubSequenceNum() + 1);
                        modifiedProductVariants.add(destination);
                        modifiedProductVariants.addAll(rearrangeOtherProductVariants(product.getId(), source, destination, direction));
                    } else {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum() + 1);
                        modifiedProductVariants.add(source);
                        modifiedProductVariants.addAll(rearrangeOtherProductVariants(product.getId(), source, destination, direction));
                    }
                    productVariantDAO.saveAll(modifiedProductVariants);
                    return true;
                }).orElse(false);
    }

    private List<ProductVariant> rearrangeOtherProductVariants(String internalProductId, ProductVariant source, ProductVariant destination, PIMConstants.ReorderingDirection direction) {
        List<ProductVariant> adjustedProductVariants = new ArrayList<>();
        List<ProductVariant> productVariants = productVariantDAO.findByProductIdAndChannelIdAndSequenceNumAndSubSequenceNumGreaterThanEqualOrderBySubSequenceNumAsc(internalProductId, destination.getChannelId(), destination.getSequenceNum(), direction == DOWN ? destination.getSubSequenceNum() : source.getSubSequenceNum());
        int subSequenceNum = direction == DOWN ? destination.getSubSequenceNum() : source.getSubSequenceNum();
        for(ProductVariant productVariant : productVariants) {
            if(productVariant.getId().equals(source.getId()) || productVariant.getId().equals(destination.getId())) {
                continue;
            }
            if(productVariant.getSubSequenceNum() == subSequenceNum) {
                productVariant.setSubSequenceNum(++subSequenceNum);
                adjustedProductVariants.add(productVariant);
            } else {
                break;
            }
        }
        return adjustedProductVariants;
    }

}
