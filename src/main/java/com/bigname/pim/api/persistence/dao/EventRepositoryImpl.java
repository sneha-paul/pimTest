package com.bigname.pim.api.persistence.dao;

import com.bigname.common.util.CollectionsUtil;
import com.bigname.core.persistence.dao.GenericRepositoryImpl;
import com.bigname.pim.api.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by dona on 30-01-2019.
 */
public class EventRepositoryImpl extends GenericRepositoryImpl<Event> implements EventRepository {

    public EventRepositoryImpl(MongoTemplate mongoTemplate) {super(mongoTemplate, Event.class);}

    public  Page<Map<String, Object>> getEventData(Pageable pageable) {
        Sort sort = pageable.getSort();
        SortOperation sortOperation;
        if(sort == null) {
           sortOperation = sort(Sort.Direction.ASC, "sequenceNum").and(Sort.Direction.DESC, "subSequenceNum");
        } else {
            Sort.Order order = sort.iterator().next();
            //sortOperation = sort(order.getDirection(), order.getProperty());
            sortOperation = sort(Sort.by(new Sort.Order(Sort.Direction.ASC, "timeStamp")));
        }
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("user")
                .localField("user")
                .foreignField("_id")
                .as("event");

        Aggregation aggregation = newAggregation(
                lookupOperation,
                replaceRoot().withValueOf(ObjectOperators.valueOf(AggregationSpELExpression.expressionOf("arrayElemAt(event, 0)")).mergeWith(ROOT)),
                project().andExclude("description", "event "),
                sortOperation,
                skip(pageable.getOffset()),
                limit((long) pageable.getPageSize())
        );

       /* List<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, "event", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList());*/
        return PageableExecutionUtils.getPage(
               mongoTemplate.aggregate(aggregation, "event", Map.class).getMappedResults().stream().map(CollectionsUtil::generifyMap).collect(Collectors.toList()),
               pageable,
               () -> mongoTemplate.count(new Query().addCriteria(null), Event.class));

    }


}
