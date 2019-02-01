package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.pim.api.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by dona on 30-01-2019.
 */
public class EventRepositoryImpl extends  GenericRepositoryImpl<Event> implements EventRepository {

    public EventRepositoryImpl(MongoTemplate mongoTemplate) {super(mongoTemplate, Event.class);}

    public Page<Map<String, Object>> getEventData(Pageable pageable) {
        Sort sort = pageable.getSort();
        SortOperation sortOperation;
        if(sort == null) {
            sortOperation = sort(Sort.Direction.ASC, "sequenceNum").and(Sort.Direction.DESC, "subSequenceNum");
        } else {
            Sort.Order order = sort.iterator().next();
            sortOperation = sort(order.getDirection(), order.getProperty());
        }
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("user")
                .localField("user")
                .foreignField("_id")
                .as("userDetails");

        Aggregation aggregation = newAggregation(
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(event, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "userDetails"),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );

        List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "event", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());

        return (Page<Map<String, Object>>) results;
    }


}
