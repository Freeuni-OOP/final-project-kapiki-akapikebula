package com.kapiki_akapikebula.app.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;
    @InjectMocks
    private EmailService emailService;
    @Test
    void sendSimpleMessage_ShouldCallMailSender() {
        emailService.sendSimpleMessage("test@user.com", "Subject", "Text Body");

        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}