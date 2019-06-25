package com.bigname.pim.api.persistence.dao;

import com.bigname.pim.api.domain.Channel;
import com.m7.xtreme.xcore.persistence.dao.mongo.GenericDAO;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */
public interface ChannelDAO extends GenericDAO<Channel>, ChannelRepository {
}
