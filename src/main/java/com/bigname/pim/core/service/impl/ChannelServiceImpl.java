package com.bigname.pim.core.service.impl;

import com.bigname.pim.core.domain.Channel;
import com.bigname.pim.core.persistence.dao.mongo.ChannelDAO;
import com.bigname.pim.core.service.ChannelService;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
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

}

