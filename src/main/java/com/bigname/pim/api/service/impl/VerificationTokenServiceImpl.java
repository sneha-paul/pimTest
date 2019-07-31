package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.domain.VerificationToken;
import com.bigname.pim.api.persistence.dao.mongo.VerificationTokenDAO;
import com.bigname.pim.api.service.VerificationTokenService;
import com.m7.xtreme.xcore.service.impl.BaseServiceSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;

@Service
public class VerificationTokenServiceImpl extends BaseServiceSupport<VerificationToken, VerificationTokenDAO, VerificationTokenService> implements VerificationTokenService {
    private VerificationTokenDAO verificationTokenDAO;

    @Autowired
    public VerificationTokenServiceImpl(VerificationTokenDAO verificationTokenDAO, Validator validator) {
        super(verificationTokenDAO, "verificationToken", validator);
        this.verificationTokenDAO = verificationTokenDAO;
    }

    @Override
    public VerificationToken findByToken(String token) {
        return verificationTokenDAO.findByToken(token);
    }

    @Override
    public void tokenSave(VerificationToken myToken) {
        verificationTokenDAO.save(myToken);
    }
}
