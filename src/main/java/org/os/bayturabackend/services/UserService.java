package org.os.bayturabackend.services;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.UpdateProfileDTO;
import org.os.bayturabackend.DTOs.UserResponseDTO;
import org.os.bayturabackend.entities.Provider;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.exceptions.DuplicateResourceException;
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


    public UserResponseDTO updateProfile(Long userId, UpdateProfileDTO dto){
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );

        if(dto.getUsername() != null) {
            if(userRepository.existsByUsername(dto.getUsername())
                    && !user.getRealUsername().equals(dto.getUsername())) {
                throw new DuplicateResourceException("Username already exists");
            }
            user.setUsername(dto.getUsername());
        }
        if(dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if(dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if(dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }

        if(user instanceof Provider provider){
            if(dto.getCompanyName() != null) {
                provider.setCompanyName(dto.getCompanyName());
            }
            if(dto.getCompanyAddress() != null) {
                provider.setCompanyAddress(dto.getCompanyAddress());
            }
        }

        userRepository.save(user);

        if (user instanceof Provider){
            return userMapper.toProviderResponse( (Provider) user);
        }
        else {
            return userMapper.toUserResponse(user);
        }
    }

    public void deleteProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );
        userRepository.delete(user);
    }
}
