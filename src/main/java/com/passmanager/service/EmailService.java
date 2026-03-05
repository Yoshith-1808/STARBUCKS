package com.passmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetOtp(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("SecureVault - Password Reset Code");
        message.setText("Hello,\n\n" +
                "You have requested to reset your master password. " +
                "Please use the following 6-digit code to proceed:\n\n" +
                "** " + otp + " **\n\n" +
                "This code will expire in 15 minutes.\n\n" +
                "If you did not request this reset, please ignore this email.\n\n" +
                "Note: A password reset will cause all currently stored passwords to become inaccessible due to zero-knowledge encryption.\n\n" +
                "Regards,\n" +
                "SecureVault Team");
        mailSender.send(message);
    }
}
