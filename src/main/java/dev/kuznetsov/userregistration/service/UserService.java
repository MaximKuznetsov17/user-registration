package dev.kuznetsov.userregistration.service;

import dev.kuznetsov.userregistration.model.ConfirmationToken;
import dev.kuznetsov.userregistration.model.User;
import dev.kuznetsov.userregistration.repository.UserRepository;
import dev.kuznetsov.userregistration.security.PasswordEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "User with username %s not found";
    private final static String USER_ALREADY_EXISTS_MSG = "User %s already sign up!";
    private static final long TOKEN_EXPIRTION_TIME = 15;

    private final ConfirmationTokenService confirmationTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(User user) {
        boolean isUserExists = userRepository.getUserByEmail(user.getEmail()).isPresent();
        if (isUserExists) {
            log.warn(String.format(USER_ALREADY_EXISTS_MSG, user.getEmail()));
            throw new IllegalStateException(String.format(USER_ALREADY_EXISTS_MSG, user.getEmail()));
        }
        user.setPassword(getEncodedPassword(user));
        userRepository.save(user);
        ConfirmationToken confirmationToken = generateConfirmationToken(user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return confirmationToken.getToken();
    }

    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }

    private ConfirmationToken generateConfirmationToken(User user) {
        String token = generateToken();
        return new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(TOKEN_EXPIRTION_TIME),
                user
        );
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private String getEncodedPassword(User user) {
        return passwordEncoder.bCryptPasswordEncoder().encode(user.getPassword());
    }
}
