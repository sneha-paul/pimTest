package com.bigname.pim.api.service;

/**
 * Created by dona on 09-01-2019.
 */

public interface RegistrationService {
    void sendVerificationEmail(String subject, String recipient, String message);
}

