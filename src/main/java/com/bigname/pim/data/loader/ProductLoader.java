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

    public boolean load(String filePath) {
        List<List<String>> data = POIUtil.readData(filePath);
        System.out.println("size-------------->"+data.size());
        System.out.println("size-------------->"+data.get(0).size());
        List<String> availableAttributeTypes = Arrays.asList("INPUTBOX", "TEXTAREA", "DROPDOWN", "YES_NO");
        attributeCollectionService.get(attributeCollectionId, FindBy.EXTERNAL_ID, false).ifPresent(attributeCollection -> {

            attributeCollection.setGroup("ATTRIBUTES");

            AttributeGroup defaultGroup = new AttributeGroup();
            defaultGroup.setId("DEFAULT_GROUP");

            List<String> attributeNames = data.get(0);
            List<String> attributeTypes = data.get(1);
            List<String> familyAttributeGroup = data.get(2);
            List<String> familyAttributeSubgroup = data.get(3);

            List<List<String>> variantsData = data.subList(4, data.size());

            for (int i = 1; i < attributeNames.size(); i++) {
                if (!availableAttributeTypes.contains(attributeTypes.get(i))) {
                    continue;
                }
                String attributeName = attributeNames.get(i);
                Attribute attribute = new Attribute();
                attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
                attribute.setUiType(Attribute.UIType.get(attributeTypes.get(i)));
                attribute.setName(attributeName);
                System.out.println("Attribute---> " + attribute.toString());
                if(!attributeCollection.getAttributes().containsKey(AttributeGroup.DEFAULT_GROUP_ID)
                        || attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().isEmpty()
                        || !attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().containsKey(ValidatableEntity.toId(attribute.getName()))) {
                    attributeCollection.addAttribute(attribute);
                }
                attribute = attributeCollection.getAttributes().get(AttributeGroup.DEFAULT_GROUP_ID).getAttributes().get(ValidatableEntity.toId(attribute.getName()));
                for (int j = 0; j < variantsData.size(); j++) {
                    if(!ConvertUtil.toBoolean(attribute.getSelectable())) {
                        continue;
                    }
                    String attributeOptionValue = variantsData.get(j).get(i);
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
            attributeCollectionService.update(attributeCollectionId, FindBy.EXTERNAL_ID, attributeCollection);
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
