package com.bigname.pim.config;

import com.m7.xtreme.xcore.config.BaseMongoConfig;
import com.mongodb.MongoClientURI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
@Configuration
public class PimMongoConfig extends BaseMongoConfig {

    /*

    @Bean(name = "secondaryMongoTemplate")
    public MongoTemplate secondaryMongoTemplate() {
        return new MongoTemplate(secondaryFactory());
    }

    @Bean
    public MongoDbFactory secondaryFactory() {
        return new SimpleMongoDbFactory(new MongoClientURI("mongodb://localhost:27017/pim3bb"));
    }
    */
}
