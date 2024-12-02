package org.backend.organizer.Service;

import jakarta.persistence.EntityNotFoundException;
import org.backend.organizer.Model.RefreshToken;
import org.backend.organizer.Model.User;
import org.backend.organizer.Repository.RefreshTokenRepository;
import org.backend.organizer.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${organizer.app.jwtRefreshExpirationMs}")
    private Long refreshExpirationMs;

    @Autowired
    RefreshTokenRepository repository;
    @Autowired
    UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
        refreshToken.setToken(UUID.randomUUID().toString());




        Optional<RefreshToken> optToken = this.repository.findByUser(user);
        if (optToken.isPresent()) {
            optToken.get().setExpiryDate(refreshToken.getExpiryDate());
            return repository.save(optToken.get());
        }

        refreshToken = repository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            repository.delete(token);
            throw new IllegalArgumentException();
        }

        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        repository.deleteByUser(user);
    }
}
