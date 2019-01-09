package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.VirtualFile;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

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
    public List<Map<String, Object>> getHierarchy(String rootDirectoryId) {
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

        return results;
    }
}
