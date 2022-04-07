package dev.kuznetsov.userregistration.service.impl;

import dev.kuznetsov.userregistration.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMimeMessage(String emailTo, String subject, String message) {
        try {
            log.info("MailMessage object prepared and ready to be sent");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            helper.setText(message, true);
            helper.setTo(emailTo);
            helper.setSubject(subject);
            helper.setFrom(username);
            mailSender.send(mimeMessage);
        } catch (Exception ex) {
            log.error(String.format("Can't send the message to %s with the subject %s", emailTo, subject), ex);
        }
    }
}
