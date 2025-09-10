package com.talentstream.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class BlogEmailService {

    private final JavaMailSender mailSender;

    public BlogEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Just send a simple notification mail
    public void sendNewsUpdateNotification() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("marganavineetha1711@gmail.com");
        message.setTo("vineetha.margana@tekworks.in");
        message.setSubject("Tech News Updated");
        message.setText("📢 Latest tech news articles have been fetched and stored in the database.\n\n"
                + "Please log in to the system and review them.");

        mailSender.send(message);
    }
}
