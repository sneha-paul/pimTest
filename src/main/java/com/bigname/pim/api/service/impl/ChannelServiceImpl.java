package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Channel;
import com.bigname.pim.api.persistence.dao.ChannelDAO;
import com.bigname.pim.api.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Manu V NarayanaPrasad (manu@blacwood.com)
 * @since 1.0
 */

@Service
public class ChannelServiceImpl extends BaseServiceSupport<Channel, ChannelDAO, ChannelService> implements ChannelService {


    private ChannelDAO channelDAO;

    @Autowired
    public ChannelServiceImpl(ChannelDAO channelDAO, Validator validator) {
        super(channelDAO, "channel", validator);
        this.channelDAO = channelDAO;
    }

    @Override
    public Channel createOrUpdate(Channel channel) {
        return channelDAO.save(channel);
    }

    @Override
    public List<Channel> create(List<Channel> channels) {
        channels.forEach(channel -> {channel.setCreatedUser(getCurrentUser());channel.setCreatedDateTime(LocalDateTime.now());});
        return channelDAO.insert(channels);
    }

    @Override
    public List<Channel> update(List<Channel> channels) {
        channels.forEach(channel -> {channel.setLastModifiedUser(getCurrentUser());channel.setLastModifiedDateTime(LocalDateTime.now());});
        return channelDAO.saveAll(channels);
    }

    @Override
    public List<Channel> findAll(Map<String, Object> criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public List<Channel> findAll(Criteria criteria) {
        return dao.findAll(criteria);
    }

    @Override
    public Optional<Channel> findOne(Map<String, Object> criteria) {
        return dao.findOne(criteria);
    }

    @Override
    public Optional<Channel> findOne(Criteria criteria) {
        return dao.findOne(criteria);
    }
}

