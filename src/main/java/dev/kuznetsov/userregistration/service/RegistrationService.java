package dev.kuznetsov.userregistration.service;

import dev.kuznetsov.userregistration.builder.UriComponentsCreator;
import dev.kuznetsov.userregistration.dto.RegistrationRequest;
import dev.kuznetsov.userregistration.model.ConfirmationToken;
import dev.kuznetsov.userregistration.model.User;
import dev.kuznetsov.userregistration.model.UserRole;
import dev.kuznetsov.userregistration.service.impl.EmailServiceImpl;
import dev.kuznetsov.userregistration.validator.EmailValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class RegistrationService {
    private final static String EMAIL_NOT_VALID_MSG = "Email %s not valid!";
    private final static String EMAIL_ALREADY_CONFIRMED_MSG = "Email already confirmed!";
    private final static String TOKEN_EXPIRED_MSG = "Token expired!";

    private final EmailValidator emailValidator;
    private final UserService userService;
    private final EmailServiceImpl emailService;
    private final ConfirmationTokenService confirmationTokenService;
    private final SpringTemplateEngine thymeleafTemplateEngine;
    private final UriComponentsCreator uriComponentsCreator;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            log.warn(String.format(EMAIL_NOT_VALID_MSG, request.getEmail()));
            throw new IllegalStateException(String.format(EMAIL_NOT_VALID_MSG, request.getEmail()));
        }
        String token = userService.signUpUser(new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                UserRole.USER));
        emailService.sendMimeMessage(request.getEmail(),
                buildSubject(request),
                buildEmailMessage(request, token));
        return token;
    }

    private String buildEmailMessage(RegistrationRequest request, String token) {
        UriComponents uriComponents = uriComponentsCreator.buildTokenConfirmationUri(token);
        Map<String, Object> templateModel = buildMessageTemplateMap(request, uriComponents.toUriString());
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        return thymeleafTemplateEngine.process("email-confirmation-template.html", thymeleafContext);
    }

    private Map<String,Object> buildMessageTemplateMap(RegistrationRequest request, String link) {
        Map<String, Object> templateMap = new HashMap<>();
        templateMap.put("firstName", request.getFirstName());
        templateMap.put("lastName", request.getLastName());
        templateMap.put("confirmationLink", link);
        return templateMap;
    }

    private String buildSubject(RegistrationRequest request) {
        return "Account activation";
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.findConfirmationToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found!"));
        if (confirmationToken.getConfirmedAt() != null) {
            log.error(EMAIL_ALREADY_CONFIRMED_MSG);
            throw new IllegalStateException(EMAIL_ALREADY_CONFIRMED_MSG);
        }
        LocalDateTime expiredAt = confirmationToken.getExpiredAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            log.error(TOKEN_EXPIRED_MSG);
            throw new IllegalStateException(TOKEN_EXPIRED_MSG);
        }
        confirmationTokenService.updateConfirmationTime(token);
        userService.enableUser(confirmationToken.getUser().getEmail());
        return "confirmed";
    }
}
