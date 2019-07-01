package com.bigname.pim.data.exportor;

import com.bigname.pim.api.domain.AttributeCollection;
import com.bigname.pim.api.domain.AttributeOption;
import com.bigname.pim.api.service.AttributeCollectionService;
import com.bigname.pim.util.POIUtil;
import com.m7.xtreme.common.util.PimUtil;
import com.m7.xtreme.xcore.data.exporter.BaseExporter;
import com.m7.xtreme.xcore.util.FindBy;
import com.m7.xtreme.xcore.util.ID;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by sanoop on 13/02/2019.
 */
@Component
public class AttributeCollectionExporter implements BaseExporter<AttributeCollection, AttributeCollectionService> {
    private AttributeCollectionService attributeCollectionService;

    public AttributeCollectionExporter(AttributeCollectionService attributeCollectionService) {
        this.attributeCollectionService = attributeCollectionService;
    }

    @Override
    public boolean exportData(String filePath) {
        return false;
    }

    @Override
    public String getFileName(Type fileType) {
        return "AttributeCollectionExport" + PimUtil.getTimestamp() + fileType.getExt();
    }

    public boolean exportAttributeData(String filePath, String attributeCollectionId) {

        Optional<AttributeCollection> attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionId), false);
        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList(new String[]{"ID", "NAME", "DATA TYPE", "UI TYPE", "PARENT ATTRIBUTE ID"}));
        attributeCollection.ifPresent(attributeCollection1 ->
            attributeCollection1.getAllAttributes().forEach(attribute ->
                    data.add(Arrays.asList(new String[]{attribute.getId(), attribute.getName(), attribute.getDataType(), String.valueOf(attribute.getUiType()), attribute.getParentAttributeId()})))
        );

        POIUtil.writeData(filePath, "Attributes", data);
        return true;
    }

    public boolean exportAttributesOptionData(String filePath, String attributeCollectionId) {

        Map<String, List<List<Object>>> attributesOptions = new HashMap<>();

        Optional<AttributeCollection> attributeCollection = attributeCollectionService.get(ID.EXTERNAL_ID(attributeCollectionId), false);

        attributeCollection.ifPresent(attributeCollection1 -> attributeCollection1.getAllAttributes().forEach(attribute -> {
            if ("Y".equals(attribute.getSelectable())) {
                Map<String, AttributeOption> attributeOptionsMap = attribute.getOptions();
                List<List<Object>> attributeOptions = new ArrayList<>();
                attributeOptions.add(Arrays.asList("ID", "VALUE"));
                attributeOptionsMap.forEach((k, attributeOption) -> attributeOptions.add(Arrays.asList(attributeOption.getId(), attributeOption.getValue())));
                attributesOptions.put(attribute.getId(), attributeOptions);
            }
        }));

        POIUtil.writeData(filePath, attributesOptions);
        return true;
    }
}
