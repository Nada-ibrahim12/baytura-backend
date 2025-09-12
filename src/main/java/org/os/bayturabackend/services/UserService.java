package org.os.bayturabackend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.UpdateProfileDTO;
import org.os.bayturabackend.DTOs.UserResponseDTO;
import org.os.bayturabackend.entities.Provider;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.exceptions.DuplicateResourceException;
import org.os.bayturabackend.exceptions.ResourceNotFoundException;
import org.os.bayturabackend.mappers.UserMapper;
import org.os.bayturabackend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Cloudinary cloudinary;


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

    public UserResponseDTO uploadPFP(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are allowed");
        }

        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");

            user.setProfilePictureUrl(imageUrl);
            user.setProfilePictureId(publicId);

            userRepository.save(user);

        }
        catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error uploading file",
                    e
            );
        }

        if (user instanceof Provider){
            return userMapper.toProviderResponse( (Provider) user);
        }
        else {
            return userMapper.toUserResponse(user);
        }
    }

    public UserResponseDTO deletePFP(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found")
                );

        try {

            if (user.getProfilePictureId() != null) {
                cloudinary.uploader().destroy(user.getProfilePictureId(), ObjectUtils.emptyMap());
            }

            user.setProfilePictureUrl("https://github.com/user-attachments/assets/e4c05593-81cc-4aca-b8e6-571128095fbc");
            user.setProfilePictureId(null);

            userRepository.save(user);

        }
        catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error deleting profile picture",
                    e
            );
        }

        if (user instanceof Provider){
            return userMapper.toProviderResponse( (Provider) user);
        }
        else {
            return userMapper.toUserResponse(user);
        }
    }

}
