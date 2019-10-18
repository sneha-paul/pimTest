package com.bigname.pim.step;

import com.bigname.pim.api.domain.ProductVariant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class BulkWriter implements ItemWriter<ProductVariant> {

    private  static  final String PRODUCT_INDEX_NAME = "product_index";
    private  static  final String PRODUCT_INDEX_TYPE = "product_type";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void write(List<? extends ProductVariant> list) throws Exception {

        System.out.println("BulkIndex processing");
        BulkRequest bulkRequest = new BulkRequest();

        list.forEach(productVariant -> {
            IndexRequest indexRequest = new IndexRequest(PRODUCT_INDEX_NAME,PRODUCT_INDEX_TYPE,productVariant.getId()).
                    source(objectMapper.convertValue(productVariant, Map.class));
            bulkRequest.add(indexRequest);
        });
        System.out.println("bulkIndex completed.");
        try {
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

