package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.Website;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class WebsiteRepositoryImpl extends GenericRepositoryImpl<Website> implements WebsiteRepository {

    public WebsiteRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Website.class);
    }

    public Page<Map<String, Object>> getWebsiteCatalogs(String websiteId, Pageable pageable) {
        Sort sort = pageable.getSort();
        SortOperation sortOperation;
        if(sort == null) {
            sortOperation = sort(Sort.Direction.ASC, "sequenceNum").and(Sort.Direction.DESC, "subSequenceNum");
        } else {
            Sort.Order order = sort.iterator().next();
            sortOperation = sort(order.getDirection(), order.getProperty());
        }
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("catalog")
                .localField("catalogId")
                .foreignField("_id")
                .as("websiteCatalog");

        Aggregation aggregation = newAggregation(
                match(Criteria.where("websiteId").is(websiteId)),
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(websiteCatalog, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "websiteCatalog"),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );

        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "websiteCatalog", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        return new PageImpl<>(results, pageable, results.size());
    }

}
