package org.os.bayturabackend.services;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.UserResponseDTO;
import org.os.bayturabackend.entities.Provider;
import org.os.bayturabackend.entities.Role;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.exceptions.ResourceNotFoundException;
import org.os.bayturabackend.mappers.UserMapper;
import org.os.bayturabackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDTO getProfile(Long userID){
        User user = userRepository.findById(userID)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );

        if (user instanceof Provider){
            return userMapper.toProviderResponse( (Provider) user);
        }
        else{
            return userMapper.toUserResponse(user);
        }

    }
}
