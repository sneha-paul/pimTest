package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Channel;
import com.m7.xtreme.xcore.persistence.dao.GenericRepository;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface ChannelRepository extends GenericRepository<Channel, Criteria> {
}
