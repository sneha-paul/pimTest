package com.bigname.pim.core.data.exportor;

import com.bigname.pim.core.domain.AttributeCollection;
import com.bigname.pim.core.domain.AttributeOption;
import com.bigname.pim.core.service.AttributeCollectionService;
import com.m7.xtreme.common.util.POIUtil;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.xcore.data.exporter.BaseExporter;
import com.m7.xtreme.xcore.util.Criteria;
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
    public boolean exportData(String filePath, Criteria criteria) {
        return false;
    }

    @Override
    public String getFileName(Type fileType) {
        return "AttributeCollectionExport" + PlatformUtil.getTimestamp() + fileType.getExt();
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
