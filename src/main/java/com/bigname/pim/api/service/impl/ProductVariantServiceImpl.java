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
    public Page<ProductVariant> findAll(String searchField, String keyword, String productId, FindBy findBy, String channelId, Pageable pageable, boolean... activeRequired) {
        return productDAO.findById(productId, findBy)
                .map(product -> productVariantDAO.findAll(searchField, keyword, product.getId(), channelId, pageable, activeRequired))
                .orElse(new PageImpl<ProductVariant>(new ArrayList<>()));
    }

    @Override
    public ProductVariant update(String variantId, FindBy variantIdFindBy, ProductVariant productVariant) {
        //Variant cannot be loading using the externalId alone at the base layer. So if the id is EXTERNAL, convert it to the INTERNAL one
        if(variantIdFindBy == FindBy.EXTERNAL_ID) {
            Optional<ProductVariant> _variant = get(productVariant.getProductId(), FindBy.INTERNAL_ID, productVariant.getChannelId(), variantId, variantIdFindBy, false);
            if(_variant.isPresent()) {
                variantId = _variant.get().getId();
                variantIdFindBy = FindBy.INTERNAL_ID;
            } else {
                throw new EntityNotFoundException("Unable to find product variant with variantId:" + variantId + ", productId:" + productVariant.getProductId() + ", channelId:" + productVariant.getChannelId());
            }
        } else {
            Preconditions.checkState(variantId.equals(productVariant.getId()), "Illegal operation");
        }
        return super.update(variantId, variantIdFindBy, productVariant);
    }

    /*@Override
    protected ProductVariant createOrUpdate(ProductVariant productVariant) {
        return productVariantDAO.save(productVariant);
    }*/

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public Optional<ProductVariant> get(String id, FindBy findBy, boolean... activeRequired) {
        return findBy == FindBy.INTERNAL_ID ? super.get(id, findBy, activeRequired) : Optional.empty();
    }

    @Override
    public Optional<ProductVariant> get(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, boolean... activeRequired) {
        if(variantIdFindBy == FindBy.INTERNAL_ID) {
            return get(productVariantId, FindBy.INTERNAL_ID, activeRequired);
        } else {
            if(productIdFindBy == FindBy.EXTERNAL_ID) {
                Optional<Product> _product = productDAO.findByExternalId(productId);
                if(_product.isPresent()) {
                    productId = _product.get().getId();
                } else {
                    return Optional.empty();
                }
            }
            return productVariantDAO.findByProductIdAndChannelIdAndExternalIdAndActiveIn(productId, channelId, productVariantId, PimUtil.getActiveOptions(activeRequired));
        }
    }

    @Override
    public Optional<ProductVariant> get(String productVariantId, FindBy findBy, String channelId, boolean... activeRequired) {
        return findBy == FindBy.INTERNAL_ID ? productVariantDAO.findByIdAndChannelIdAndActiveIn(productVariantId, channelId, PimUtil.getActiveOptions(activeRequired)) : productVariantDAO.findByExternalIdAndChannelIdAndActiveIn(productVariantId, channelId, PimUtil.getActiveOptions(activeRequired));
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public boolean toggle(String id, FindBy findBy, Toggle active) {
        return findBy == FindBy.INTERNAL_ID && super.toggle(id, findBy, active);
    }

    @Override
    public boolean toggle(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, Toggle active) {
        Optional<ProductVariant> _variant = get(productId, productIdFindBy, channelId, productVariantId, variantIdFindBy, false);

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
    public ProductVariant cloneInstance(String id, FindBy findBy, Entity.CloneType type) {
        return findBy == FindBy.INTERNAL_ID ? super.cloneInstance(id, findBy, type) : null;
    }

    @Override
    public ProductVariant cloneInstance(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, Entity.CloneType type) {
        return get(productId, productIdFindBy, channelId, productVariantId, variantIdFindBy, false)
                .map(productVariant -> cloneInstance(productVariant.getId(), FindBy.INTERNAL_ID, type)).orElse(null);
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public Page<ProductVariant> getAll(int page, int size, Sort sort, boolean... activeRequired) {
        return new PageImpl<>(new ArrayList<>()); //IMPORTANT - Purposely returning empty page
    }

    @Override
    public Page<ProductVariant> getAll(String productId, FindBy productIdFindBy,  String channelId, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            //sort = new Sort(Sort.Direction.ASC, "externalId");
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        if(productIdFindBy == FindBy.EXTERNAL_ID) {
            Optional<Product> _product = productDAO.findByExternalId(productId);
            if(_product.isPresent()) {
                productId = _product.get().getId();
            } else {
                return null;
            }
        }
        return productVariantDAO.findByProductIdAndChannelIdAndActiveIn(productId, channelId, PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
    }

    @Override
    public List<ProductVariant> getAll(String[] productIds, FindBy productIdFindBy,  String channelId, boolean... activeRequired) {
        if(productIdFindBy == FindBy.EXTERNAL_ID) {
            productIds = productDAO.findByExternalIdInAndActiveIn(productIds, PimUtil.getActiveOptions(activeRequired)).stream().map(Entity::getId).collect(Collectors.toList()).toArray(new String[0]);
            if(productIds.length == 0) {
                return new ArrayList<>();
            }
        }
        return productVariantDAO.findByProductIdInAndChannelIdAndActiveIn(productIds, channelId, PimUtil.getActiveOptions(activeRequired));
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public List<ProductVariant> getAll(Sort sort, boolean... activeRequired) {
        return new ArrayList<>(); //IMPORTANT - Purposely returning empty list
    }

    @Override
    public List<ProductVariant> getAll(String productId, FindBy productIdFindBy, String channelId, Sort sort, boolean... activeRequired) {
        return getAll(productId, productIdFindBy, channelId, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public Page<ProductVariant> getAll(String[] ids, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        return findBy == FindBy.INTERNAL_ID ? productVariantDAO.findByIdInAndActiveIn(ids, PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort)) : new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<ProductVariant> getAll(String productId, FindBy productIdFindBy, String channelId, String[] variantIds, FindBy variantIdFindBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        if(variantIdFindBy == FindBy.INTERNAL_ID) {
            return getAll(variantIds, FindBy.INTERNAL_ID, page, size, sort, activeRequired);
        } else {
            if(productIdFindBy == FindBy.EXTERNAL_ID) {
                Optional<Product> _product = productDAO.findByExternalId(productId);
                if(_product.isPresent()) {
                    productId = _product.get().getId();
                } else {
                    return new PageImpl<>(new ArrayList<>());
                }
            }
            return productVariantDAO.findByProductIdAndChannelIdAndExternalIdInAndActiveIn(productId, channelId, variantIds, PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
        }
    }

    //Don't use this method when the variantId is of type EXTERNAL_ID. Use the below method with productId and channelId instead
    @Override
    public List<ProductVariant> getAll(String[] ids, FindBy findBy, Sort sort, boolean... activeRequired) {
        return getAll(ids, findBy, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    @Override
    public List<ProductVariant> getAll(String productId, FindBy productIdFindBy, String channelId, String[] variantIds, FindBy variantIdFindBy, Sort sort, boolean... activeRequired) {
        return getAll(productId, productIdFindBy, channelId, variantIds, variantIdFindBy, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public Page<ProductVariant> getAllWithExclusions(String[] excludedIds, FindBy findBy, int page, int size, Sort sort, boolean... activeRequired) {
        return new PageImpl<>(new ArrayList<>()); //IMPORTANT - Purposely returning empty page
    }

    @Override
    public Page<ProductVariant> getAllWithExclusions(String productId, FindBy productIdFindBy, String channelId, String[] excludedVariantIds, FindBy variantIdFindBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "externalId"));
        }
        if(variantIdFindBy == FindBy.INTERNAL_ID) {
            return getAllWithExclusions(excludedVariantIds, FindBy.INTERNAL_ID, page, size, sort, activeRequired);
        } else {
            if(productIdFindBy == FindBy.EXTERNAL_ID) {
                Optional<Product> _product = productDAO.findByExternalId(productId);
                if(_product.isPresent()) {
                    productId = _product.get().getId();
                } else {
                    return new PageImpl<>(new ArrayList<>());
                }
            }
            return productVariantDAO.findByProductIdAndChannelIdAndExternalIdNotInAndActiveIn(productId, channelId, excludedVariantIds, PimUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, sort));
        }
    }

    //Don't use this method, use the below method with productId and channelId instead
    @Override
    public List<ProductVariant> getAllWithExclusions(String[] excludedIds, FindBy findBy, Sort sort, boolean... activeRequired) {
        return new ArrayList<>(); //IMPORTANT - Purposely returning empty list
    }

    @Override
    public List<ProductVariant> getAllWithExclusions(String productId, FindBy productIdFindBy, String channelId, String[] excludedVariantIds, FindBy variantIdFindBy, Sort sort, boolean... activeRequired) {
        return getAllWithExclusions(productId, productIdFindBy, channelId, excludedVariantIds, variantIdFindBy, 0, PIMConstants.MAX_FETCH_SIZE, sort, activeRequired).getContent();
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
    public Page<ProductVariant> getProductVariantPricing(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        Optional<ProductVariant> _productVariant = get(productId, productIdFindBy, channelId, productVariantId, variantIdFindBy,  false);
        if(_productVariant.isPresent()) {
            ProductVariant productVariant = _productVariant.get();
            Page<ProductVariant> productVariants = productVariantDAO.findByProductIdAndChannelIdAndProductVariantIdAndActiveIn(productVariant.getProductId(), productVariant.getChannelId(), productVariant.getId(), PimUtil.getActiveOptions(activeRequired), pageable);

            return productVariants;
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
    }

    @Override
    public ProductVariant addAssets(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, String[] assetIds, FileAsset.AssetFamily assetFamily) {
        return get(productId, productIdFindBy, channelId, productVariantId, variantIdFindBy,  false)
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


                    assetService.getAll(assetIds, FindBy.INTERNAL_ID, null)
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
                    update(productVariantId, FindBy.EXTERNAL_ID, productVariant);
                    return productVariant;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product variant with id:" + productVariantId));
    }

    @Override
    public ProductVariant deleteAsset(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, String assetId, FileAsset.AssetFamily assetFamily) {
        return get(productId, productIdFindBy, channelId, productVariantId, variantIdFindBy,  false)
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
                    update(productVariantId, FindBy.EXTERNAL_ID, productVariant);
                    return productVariant;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product variant with id:" + productId));
    }

    @Override
    public ProductVariant reorderAssets(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, String[] assetIds, FileAsset.AssetFamily assetFamily) {
        return get(productId, productIdFindBy, channelId, productVariantId, variantIdFindBy,  false)
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
                    update(productVariantId, FindBy.EXTERNAL_ID, productVariant);
                    return productVariant;
                })
                .orElseThrow(() -> new EntityNotFoundException("Unable to find product variant with id:" + productId));

    }

    @Override
    public ProductVariant setAsDefaultAsset(String productId, FindBy productIdFindBy, String channelId, String productVariantId, FindBy variantIdFindBy, String assetId, FileAsset.AssetFamily assetFamily) {
        return get(productId, productIdFindBy, channelId, productVariantId, variantIdFindBy,  false)
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
                    update(productVariantId, FindBy.EXTERNAL_ID, productVariant);
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
    public boolean setProductVariantsSequence(String productId, FindBy productIdFindBy, String channelId, FindBy channelIdFindBy, String sourceId, FindBy sourceIdFindBy, String destinationId, FindBy destinationIdFindBy) {

        return get(sourceId, sourceIdFindBy, channelId,  false)
                .map(product -> {
                    Map<String, ProductVariant> productVariantsMap = getAll(productId, productIdFindBy, channelId, new String[] {sourceId, destinationId}, FindBy.EXTERNAL_ID, null, false)
                            .stream().collect(Collectors.toMap(ProductVariant::getProductVariantId, productVariant1 -> productVariant1));

                    ProductVariant source = productVariantsMap.get(sourceId);
                    ProductVariant destination = productVariantsMap.get(destinationId);

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
                        modifiedProductVariants.addAll(rearrangeOtherProductVariants(productId, source, destination, direction));
                    } else {
                        source.setSequenceNum(destination.getSequenceNum());
                        source.setSubSequenceNum(destination.getSubSequenceNum() + 1);
                        modifiedProductVariants.add(source);
                        modifiedProductVariants.addAll(rearrangeOtherProductVariants(productId, source, destination, direction));
                    }
                    productVariantDAO.saveAll(modifiedProductVariants);
                    return true;
                }).orElse(false);
    }

    private List<ProductVariant> rearrangeOtherProductVariants(String productId, ProductVariant source, ProductVariant destination, PIMConstants.ReorderingDirection direction) {
        List<ProductVariant> adjustedProductVariants = new ArrayList<>();
        List<ProductVariant> productVariants = productVariantDAO.findByProductIdAndChannelIdAndSequenceNumAndSubSequenceNumGreaterThanEqualOrderBySubSequenceNumAsc(productId, destination.getChannelId(), destination.getSequenceNum(), direction == DOWN ? destination.getSubSequenceNum() : source.getSubSequenceNum());
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
