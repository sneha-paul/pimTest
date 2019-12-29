package com.bigname.pim.config;

import com.m7.xtreme.xcore.config.BaseMongoConfig;
import com.mongodb.MongoClientURI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = {"com.m7.xtreme.xplatform.persistence.dao.primary.mongo", "com.bigname.pim.api.persistence.dao.mongo"}, mongoTemplateRef = "primaryMongoTemplate")
@Import({
//    SecondaryMongoConfig.class
})
public class PimMongoConfig extends BaseMongoConfig {

}
