package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Channel;
import com.m7.xcore.persistence.dao.GenericRepositoryImpl;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public class ChannelRepositoryImpl extends GenericRepositoryImpl<Channel> implements ChannelRepository {
    public ChannelRepositoryImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Channel.class);
    }
}
