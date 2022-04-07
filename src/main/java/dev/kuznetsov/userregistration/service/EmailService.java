package dev.kuznetsov.userregistration.service;

public interface EmailService {
    void sendMimeMessage(String emailTo, String subject, String message);
}
