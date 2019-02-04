package com.bigname.pim.api.service.impl;

import com.bigname.core.service.BaseServiceSupport;
import com.bigname.pim.api.domain.Channel;
import com.bigname.pim.api.persistence.dao.ChannelDAO;
import com.bigname.pim.api.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

}

