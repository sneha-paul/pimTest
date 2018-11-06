package com.bigname.pim.data.loader;

import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.AttributeGroup;
import com.bigname.pim.api.domain.ValidatableEntity;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.api.service.FamilyService;
import com.bigname.pim.api.service.ProductService;
import com.bigname.pim.api.service.ProductVariantService;
import com.bigname.pim.util.FindBy;
import com.bigname.pim.util.POIUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

        AttributeGroup defaultGroup = new AttributeGroup();
        defaultGroup.setId("DEFAULT_GROUP");

        for (int i=0;i<data.get(0).size();i++){
            if("IGNORE".equals(data.get(0).get(i))){
                continue;
            }
            Attribute attribute = new Attribute();
            attribute.setAttributeGroup(AttributeGroup.getDefaultGroup());
            attribute.setUiType(Attribute.UIType.get(data.get(0).get(i)));
            attribute.setName(data.get(1).get(i));
            System.out.println("Attribute---> "+attribute.toString());
            createAttribute(attribute);
           /* for (int j=0;j<2;j++){
                System.out.println("Data value : "+data.get(j).get(i));
            }*/

        }



        
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
