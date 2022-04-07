package dev.kuznetsov.userregistration.service;

import dev.kuznetsov.userregistration.model.ConfirmationToken;
import dev.kuznetsov.userregistration.repository.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> findConfirmationToken(String token) {
        return confirmationTokenRepository.findConfirmationTokenByToken(token);
    }

    public int updateConfirmationTime(String token) {
        return confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
