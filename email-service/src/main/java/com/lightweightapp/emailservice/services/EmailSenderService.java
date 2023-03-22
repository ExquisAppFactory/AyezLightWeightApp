package com.lightweightapp.emailservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailSenderId;

    public void sendEmail(String receiver, String subject, String body)
    {
        SimpleMailMessage message =  new SimpleMailMessage();
        message.setFrom(emailSenderId);
        message.setTo(receiver);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);
    }
}
