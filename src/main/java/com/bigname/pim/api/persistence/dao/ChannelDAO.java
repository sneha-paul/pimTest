package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface ChannelDAO extends BaseDAO<Channel>, MongoRepository<Channel, String>, ChannelRepository {
}
