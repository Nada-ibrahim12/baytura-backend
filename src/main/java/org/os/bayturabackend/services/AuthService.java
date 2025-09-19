package org.os.bayturabackend.services;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.AuthResponseDTO;
import org.os.bayturabackend.DTOs.LoginRequestDTO;
import org.os.bayturabackend.DTOs.RegisterProviderDTO;
import org.os.bayturabackend.DTOs.RegisterUserDTO;
import org.os.bayturabackend.config.JwtService;
import org.os.bayturabackend.entities.*;
import org.os.bayturabackend.exceptions.ForbiddenException;
import org.os.bayturabackend.mappers.UserMapper;
import org.os.bayturabackend.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final EmailService emailService;


    private AuthResponseDTO buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        Instant expiresAt = jwtService.extractExpiration(token).toInstant();
        return userMapper.toAuthResponse(user, token, expiresAt);
    }


    public AuthResponseDTO registerCustomer(RegisterUserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email is already used");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username is already used");
        }
        Customer customer = userMapper.toCustomer(dto, passwordEncoder.encode(dto.getPassword()));

        String subject = "Welcome to BAYTAURA!";
        String content = "Hi " + dto.getFirstName() + ",\n\n" +
                "Thank you for joining Baytaura! You can now explore properties and start your journey with us.\n\n" +
                "— The Baytaura Team";
        emailService.sendEmail(dto.getEmail(), subject, content);
        userRepository.save(customer);
        return buildAuthResponse(customer);
    }

    public AuthResponseDTO registerProvider(RegisterProviderDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email is already used");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username is already used");
        }
        Provider provider = userMapper.toProvider(dto, passwordEncoder.encode(dto.getPassword()));

        String subject = "Welcome to BAYTAURA!";
        String content = "Hi " + dto.getFirstName() + ",\n\n" +
                "Thank you for registering as a provider! Your account is pending approval. " +
                "Once approved, you can start listing properties.\n\n" +
                "— The Baytaura Team";
        emailService.sendEmail(dto.getEmail(), subject, content);

        userRepository.save(provider);
        return buildAuthResponse(provider);
    }

    public AuthResponseDTO registerAdmin(RegisterUserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email is already used");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username is already used");
        }
        Admin admin = userMapper.toAdmin(dto, passwordEncoder.encode(dto.getPassword()));
        userRepository.save(admin);
        return buildAuthResponse(admin);
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );
        }
        catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new BadCredentialsException("Invalid email or password")
                );

        if (user instanceof Provider provider && provider.getStatus() != ProviderStatus.ACCEPTED) {
                throw new ForbiddenException("Your provider account is not approved yet.");
        }

        return buildAuthResponse(user);
    }

}
