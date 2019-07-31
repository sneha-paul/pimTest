package com.bigname.pim.api.service;

import com.bigname.pim.api.domain.VerificationToken;
import com.bigname.pim.api.persistence.dao.mongo.VerificationTokenDAO;
import com.m7.xtreme.xcore.service.BaseService;

public interface VerificationTokenService extends BaseService<VerificationToken, VerificationTokenDAO> {

    VerificationToken findByToken(String token);

    void tokenSave(VerificationToken myToken);
}
