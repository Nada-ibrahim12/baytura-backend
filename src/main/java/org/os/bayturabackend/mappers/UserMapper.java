package org.os.bayturabackend.mappers;

import org.os.bayturabackend.DTOs.*;
import org.os.bayturabackend.entities.*;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserMapper {

    public Customer toCustomer(RegisterUserDTO dto, String encodedPassword) {
        return Customer.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .password(encodedPassword)
                .role(Role.CUSTOMER)
                .build();
    }

    public Provider toProvider(RegisterProviderDTO dto, String encodedPassword) {
        return Provider.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .password(encodedPassword)
                .companyName(dto.getCompanyName())
                .companyAddress(dto.getCompanyAddress())
                .status(ProviderStatus.PENDING) // default value
                .role(Role.PROVIDER)
                .build();
    }

    public Admin toAdmin(RegisterUserDTO dto, String encodedPassword) {
        return Admin.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .password(encodedPassword)
                .role(Role.ADMIN)
                .build();
    }

    public AuthResponseDTO toAuthResponse(User user, String token, Instant expiresAt) {
        return new AuthResponseDTO(
                token,
                user.getUserId(),
                user.getRealUsername(),
                user.getEmail(),
                user.getRole().name(),
                expiresAt
        );
    }

    // ? for Customers and Admins
    public UserResponseDTO toUserResponse(User user){
        if (user == null) {
            return null;
        }
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUsername(user.getRealUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setRole(user.getRole().name());
        return dto;
    }

    public ProviderResponseDTO toProviderResponse(Provider provider){
        if (provider == null) {
            return null;
        }
        ProviderResponseDTO dto = new ProviderResponseDTO();
        dto.setUsername(provider.getRealUsername());
        dto.setEmail(provider.getEmail());
        dto.setFirstName(provider.getFirstName());
        dto.setLastName(provider.getLastName());
        dto.setPhone(provider.getPhone());
        dto.setCompanyName(provider.getCompanyName());
        dto.setCompanyAddress(provider.getCompanyAddress());
        dto.setProfilePictureUrl(provider.getProfilePictureUrl());
        dto.setRole(provider.getRole().name());
        dto.setStatus(provider.getStatus().name());
        return dto;
    }

}


