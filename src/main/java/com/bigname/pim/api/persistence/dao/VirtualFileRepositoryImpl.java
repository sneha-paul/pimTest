package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.VirtualFile;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class VirtualFileRepositoryImpl extends GenericRepositoryImpl<VirtualFile> implements VirtualFileRepository {
    public VirtualFileRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, VirtualFile.class);
    }

    @Override
    public List<VirtualFile> getHierarchy(String rootDirectoryId) {
        List<VirtualFile> assets = new ArrayList<>();
        List<String> assetIds = new ArrayList<>();
        GraphLookupOperation graphLookupOperation = GraphLookupOperation.builder()
                .from("virtualFile")
                .startWith("$_id")
                .connectFrom("parentDirectoryId")
                .connectTo("_id")
                .as("assetsHierarchy");

        Aggregation aggregation = newAggregation(
                match(new Criteria().orOperator(Criteria.where("rootDirectoryId").is(rootDirectoryId), Criteria.where("_id").is(rootDirectoryId))),
                graphLookupOperation
        );

        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "virtualFile", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        results.forEach(assetMap -> {
            List<VirtualFile> nodes = (List<VirtualFile>)assetMap.get("assetsHierarchy");
            for (VirtualFile node : nodes) {
                if (!assetIds.contains(node.getId())) {
                    if (node.getParentDirectoryId().isEmpty()) {
                        assets.add(0, node);
                        assetIds.add(0, node.getId());
                    } else {
                        assets.add(assetIds.indexOf(node.getParentDirectoryId()) + 1, node);
                        assetIds.add(assetIds.indexOf(node.getParentDirectoryId()) + 1, node.getId());
                    }
                }
            }

        });
        return assets;
    }
}
