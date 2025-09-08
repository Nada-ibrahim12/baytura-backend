package org.os.bayturabackend.mappers;

import org.os.bayturabackend.DTOs.AuthResponseDTO;
import org.os.bayturabackend.DTOs.RegisterProviderDTO;
import org.os.bayturabackend.DTOs.RegisterUserDTO;
import org.os.bayturabackend.DTOs.UserResponseDTO;
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
                .isApproved(false) // default value
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
}


