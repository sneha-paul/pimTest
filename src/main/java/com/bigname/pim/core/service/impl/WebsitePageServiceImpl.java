package com.bigname.pim.core.service.impl;

import com.bigname.pim.core.domain.WebsitePage;
import com.bigname.pim.core.persistence.dao.mongo.WebsiteDAO;
import com.bigname.pim.core.persistence.dao.mongo.WebsitePageDAO;
import com.bigname.pim.core.service.WebsitePageService;
import com.m7.xtreme.common.util.PlatformUtil;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import com.m7.xtreme.xcore.util.ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.Optional;

@Service
public class WebsitePageServiceImpl extends BaseServiceSupport<WebsitePage, WebsitePageDAO, WebsitePageService> implements WebsitePageService {

    private WebsitePageDAO websitePageDAO;
    private WebsiteDAO websiteDAO;

    @Autowired
    public WebsitePageServiceImpl(WebsitePageDAO websitePageDAO, Validator validator, WebsiteDAO websiteDAO) {
        super(websitePageDAO, "websitePage", validator);
        this.websitePageDAO = websitePageDAO;
        this.websiteDAO = websiteDAO;
    }

    @Override
    public Page<WebsitePage> getAll(ID<String> websiteId, int page, int size, Sort sort, boolean... activeRequired) {
        if(sort == null) {
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "sequenceNum"), new Sort.Order(Sort.Direction.DESC, "subSequenceNum"));
        }
        Sort finalSort = sort;
        return websiteDAO.findById(websiteId, false).map(website -> websitePageDAO.findByWebsiteIdAndActiveIn(website.getId(), PlatformUtil.getActiveOptions(activeRequired), PageRequest.of(page, size, finalSort))).orElse(null);
    }

    @Override
    public <I> Optional<WebsitePage> get(ID<I> websiteId, ID<I> pageId, boolean... activeRequired) {
        if(pageId.isInternalId()) {
            return get(pageId, false);
        } else {
            return websiteDAO.findById(websiteId).map(website -> websitePageDAO.findByWebsiteIdAndExternalIdAndActiveIn(website.getId(), pageId.getId(), PlatformUtil.getActiveOptions(activeRequired))).orElse(Optional.empty());
        }
    }
}
