package com.bigname.pim.core.service;

/**
 * Created by dona on 09-01-2019.
 */

public interface RegistrationService {
    void sendVerificationEmail(String subject, String recipient, String message);
}

