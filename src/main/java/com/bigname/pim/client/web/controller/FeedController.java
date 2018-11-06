package com.bigname.pim.client.web.controller;

import com.bigname.pim.api.domain.Attribute;
import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.data.loader.ProductLoader;
import com.bigname.pim.util.FindBy;
import org.apache.commons.collections.map.AbstractHashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
@Controller
@RequestMapping("pim/feeds")
public class FeedController {

    private ProductLoader productLoader;

    public FeedController(ProductLoader productLoader) {
        this.productLoader = productLoader;
    }

    @RequestMapping(value = "load", method = RequestMethod.GET)
    public Map<String, Object> loadData(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        productLoader.load("C:\\DevStudio\\Projects\\PIM\\src\\main\\resources\\feed\\10_REGULAR.xlsx");
        return model;
    }

    /*@RequestMapping(value = "/{collectionId}/attribute", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> saveAttribute(@PathVariable(value = "collectionId") String id, Attribute attribute) {
        Map<String, Object> model = new HashMap<>();
        Optional<AttributeCollection> attributeCollection = attributeCollectionService.get(id, FindBy.EXTERNAL_ID, false);
        // TODO - cross field validation to see if one of attributeGroup ID and attributeGroup name is not empty
        if(attributeCollection.isPresent() && isValid(attribute, model)) {
            attributeCollection.get().setGroup("ATTRIBUTES");
            attributeCollection.get().setGroup(attribute.getGroup());
            attributeCollection.get().addAttribute(attribute);
            attributeCollectionService.update(id, FindBy.EXTERNAL_ID, attributeCollection.get());

        }
        return model;
    }*/
}
