package org.os.bayturabackend.services;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.AuthResponseDTO;
import org.os.bayturabackend.DTOs.LoginRequestDTO;
import org.os.bayturabackend.DTOs.RegisterProviderDTO;
import org.os.bayturabackend.DTOs.RegisterUserDTO;
import org.os.bayturabackend.config.JwtService;
import org.os.bayturabackend.entities.Admin;
import org.os.bayturabackend.entities.Customer;
import org.os.bayturabackend.entities.Provider;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.mappers.UserMapper;
import org.os.bayturabackend.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid credentials");
        }
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + dto.getEmail())
        );
        return buildAuthResponse(user);
    }
}
