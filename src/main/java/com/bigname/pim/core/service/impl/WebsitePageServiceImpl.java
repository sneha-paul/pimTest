package com.bigname.pim.core.service.impl;

import com.bigname.pim.core.domain.WebsitePage;
import com.bigname.pim.core.persistence.dao.mongo.WebsitePageDAO;
import com.bigname.pim.core.service.WebsitePageService;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

@Service
public class WebsitePageServiceImpl extends BaseServiceSupport<WebsitePage, WebsitePageDAO, WebsitePageService> implements WebsitePageService {

    private WebsitePageDAO websitePageDAO;

    @Autowired
    public WebsitePageServiceImpl(WebsitePageDAO websitePageDAO, Validator validator) {
        super(websitePageDAO, "websitePage", validator);
        this.websitePageDAO = websitePageDAO;
    }

}
