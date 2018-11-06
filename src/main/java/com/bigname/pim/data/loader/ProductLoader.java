package com.bigname.pim.data.loader;

import com.bigname.common.util.ConversionUtil;
import com.bigname.common.util.ValidationUtil;
import com.bigname.pim.api.domain.*;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.util.ConvertUtil;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Component
public class ProductLoader {
    @Autowired
    private AttributeCollectionService attributeCollectionService;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private ProductVariantService productVariantService;

    @Autowired
    private ProductService productService;

    private String attributeCollectionId = "TEST";

    private List<String> availableAttributeTypes = Arrays.asList("INPUTBOX", "TEXTAREA", "DROPDOWN", "YES_NO");

    public boolean load(String filePath) {
        //Product variant data with metadata
        List<List<String>> data = POIUtil.readData(filePath);

        System.out.println("size-------------->"+data.size());
        System.out.println("size-------------->"+data.get(0).size());

        //Load the attributeCollection from the database for the given collectionId
        attributeCollectionService.get(attributeCollectionId, FindBy.EXTERNAL_ID, false).ifPresent(attributeCollection -> {

            attributeCollection.setGroup("ATTRIBUTES");

            //All attributes from the feed are created under the Default attribute group, so build a default attributeGroup instance
            AttributeGroup defaultGroup = new AttributeGroup();
            defaultGroup.setId("DEFAULT_GROUP");


            List<String> attributeNamesMetadata = data.get(0);
            List<String> attributeTypesMetadata = data.get(1);
            List<String> familyAttributeGroupMetadata = data.get(2);
            List<String> familyAttributeSubgroupMetadata = data.get(3);
            int numOfMetadataRows = 4;
            String productFamily = "";
            List<List<String>> variantsData = data.subList(numOfMetadataRows, data.size());

            //Step 1
            //Go through each variant rows
            for (int row = 0; row < variantsData.size(); row++) {

                //Go through each column starting from the second column and add the attribute to the collection, if it is of known type and won't already exists
                for (int col = 1; col < attributeNamesMetadata.size(); col++) {
                    //check to see if the metadata attribute type is one of the known types
                    boolean isAttribute = availableAttributeTypes.contains(attributeTypesMetadata.get(col));

                    Attribute attribute = null;

                    if (isAttribute) {
                        String attributeName = attributeNamesMetadata.get(col);
                        attribute = new Attribute();
                        attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                        attribute.setUiType(Attribute.UIType.get(attributeTypesMetadata.get(col)));
                        attribute.setName(attributeName);
                        System.out.println("Attribute---> " + attribute.toString());

                        //Add the attribute, if it won't exists already in the collection
                        if(!attributeCollection.getAttributes().containsKey(AttributeGroup.DEFAULT_GROUP_ID)
                                || attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().isEmpty()
                                || !attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().containsKey(ValidatableEntity.toId(attribute.getName()))) {
                            attributeCollection.addAttribute(attribute);
                        }
                        //Get the fully orchestrated attribute corresponding to the attribute name
                        attribute = attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().get(ValidatableEntity.toId(attribute.getName()));

                        //If the current attribute is selectable, add any new attribute options, if any
                        if(ConvertUtil.toBoolean(attribute.getSelectable())) {
                            String attributeOptionValue = variantsData.get(row).get(col);

                            // If the attribute option won't exist already for the attribute, add the attribute option
                            if (ValidationUtil.isNotEmpty(attributeOptionValue) && !attribute.getOptions().containsKey(ValidatableEntity.toId(attributeOptionValue))) {
                                AttributeOption attributeOption = new AttributeOption();
                                attributeOption.setCollectionId(attributeCollectionId);
                                attributeOption.setAttributeId(attribute.getFullId());
                                attributeOption.setValue(attributeOptionValue);
                                attributeOption.setActive("Y");
                                attributeOption.orchestrate();
                                attribute.getOptions().put(ValidatableEntity.toId(attributeOptionValue), attributeOption);
                            }
                        }

                    }

                    //Add the attribute option only if the current attribute is of known type and is also selectable

                }
            }
            //Update attribute collection with the newly added attributes and corresponding attribute options
            attributeCollectionService.update(attributeCollectionId, FindBy.EXTERNAL_ID, attributeCollection);

            //Attribute Collection updated with the new attributes and new options


        });



        
        return true;
    }



    public Map<String,Object> createAttribute(Attribute attribute){
        //AttributeCollection attributeCollection = new AttributeCollection();
        attributeCollectionService.get(attributeCollectionId, FindBy.EXTERNAL_ID, false).ifPresent(attributeCollection -> {
            attributeCollection.setGroup("ATTRIBUTES");
            if(!attributeCollection.getAttributes().containsKey(AttributeGroup.DEFAULT_GROUP_ID)
                    || attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().isEmpty()
                    || !attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().containsKey(ValidatableEntity.toId(attribute.getName()))) {
                attributeCollection.addAttribute(attribute);
            }
            attributeCollectionService.update(attributeCollectionId, FindBy.EXTERNAL_ID, attributeCollection);
        });
        return null;
    }

}
