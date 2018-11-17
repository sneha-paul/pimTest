package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.Channel;
import com.bigname.pim.api.persistence.dao.ChannelDAO;
import com.bigname.pim.api.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

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
}

