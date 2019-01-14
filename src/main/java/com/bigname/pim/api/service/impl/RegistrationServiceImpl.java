package com.bigname.pim.api.service.impl;

import com.bigname.pim.api.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

/**
 * Created by dona on 09-01-2019.
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

   private JavaMailSender mailSender;

    @Autowired
    public RegistrationServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String subject, String recipient, String message) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("b2bplace@blacwood.com");
            messageHelper.setTo(recipient);
            messageHelper.setSubject(subject);
            messageHelper.setText(message);
        };
        try {
            mailSender.send(messagePreparator);
        } catch (MailException e) {
            // runtime exception; compiler will not force you to handle it
        }
    }
}
