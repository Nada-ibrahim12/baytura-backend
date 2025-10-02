package org.os.bayturabackend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.ProviderResponseDTO;
import org.os.bayturabackend.DTOs.UpdateProfileDTO;
import org.os.bayturabackend.DTOs.UserResponseDTO;
import org.os.bayturabackend.entities.NotificationType;
import org.os.bayturabackend.entities.Provider;
import org.os.bayturabackend.entities.ProviderStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Cloudinary cloudinary;
    private final NotificationService notificationService;
    private final EmailService emailService;


    public List<UserResponseDTO> getAllUsers(String role, String status, String companyName) {
        List<User> users = userRepository.findAll();

        return users.stream()
                .filter(user -> role == null || user.getRole().name().equalsIgnoreCase(role))

                .filter(user -> {
                    if (status == null) {
                        return true;
                    }
                    ProviderStatus providerStatus = ProviderStatus.valueOf(status.toUpperCase());
                    if (user instanceof Provider provider) {
                        return provider.getStatus() != null && provider.getStatus().equals(providerStatus);
                    }
                    return false;
                })

                .filter(user -> {
                    if (companyName == null) return true;
                    if (user instanceof Provider provider) {
                        return provider.getCompanyName() != null &&
                                provider.getCompanyName().toLowerCase().contains(companyName.toLowerCase());
                    }
                    return false;
                })

                .map(this::userToDto)
                .toList();
    }



    public List<ProviderResponseDTO> getProviders() {
        List<User> users = userRepository.findAll();
        List<ProviderResponseDTO> providers = new ArrayList<>();
        for (User user : users) {
            if (user instanceof Provider) {
                providers.add(toDto(user));
            }
        }
        return providers;
    }

    private UserResponseDTO userToDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(user.getUserId());
        dto.setUsername(user.getRealUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());

        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setRole(user.getRole().name());

        return dto;
    }


    private ProviderResponseDTO toDto(User user) {
        Provider provider = (Provider) user;

        ProviderResponseDTO dto = new ProviderResponseDTO();
        dto.setId(user.getUserId());
        dto.setUsername(provider.getRealUsername());
        dto.setEmail(provider.getEmail());
        dto.setFirstName(provider.getFirstName());
        dto.setLastName(provider.getLastName());
        dto.setPhone(provider.getPhone());

        dto.setProfilePictureUrl(provider.getProfilePictureUrl());
        dto.setRole(provider.getRole().name());

        dto.setCompanyName(provider.getCompanyName());
        dto.setCompanyAddress(provider.getCompanyAddress());
        dto.setStatus(String.valueOf(provider.getStatus()));

        return dto;
    }

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

        notificationService.createNotification(
                userId,
                "Profile Updated",
                "Your profile details have been successfully updated.",
                NotificationType.PROFILE_UPDATED
        );

        String subject = "Profile Updated Successfully";
        String content = "Hello " + user.getFirstName() + ",\n\n" +
                "Your profile details have been successfully updated on Baytaura.\n\n" +
                "— The Baytaura Team";

        emailService.sendEmail(user.getEmail(), subject, content);

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

        notificationService.createNotification(
                userId,
                "Profile Deleted",
                "Your account has been deleted. We're sad to see you go.",
                NotificationType.PROFILE_DELETED
        );

        String subject = "Account Deleted";
        String content = "Hello " + user.getFirstName() + ",\n\n" +
                "Your Baytaura account has been deleted. We're sad to see you leave.\n\n" +
                "— The Baytaura Team";

        emailService.sendEmail(user.getEmail(), subject, content);
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

        notificationService.createNotification(
                userId,
                "Profile Picture Updated",
                "Your profile picture has been changed successfully.",
                NotificationType.PROFILE_PICTURE_UPDATED
        );

        String subject = "Profile Picture Updated";
        String content = "Hello " + user.getFirstName() + ",\n\n" +
                "Your profile picture has been updated successfully on Baytaura.\n\n" +
                "— The Baytaura Team";

        emailService.sendEmail(user.getEmail(), subject, content);

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

            user.setProfilePictureUrl("https://imgs.search.brave.com/7dqSnfmG9C_8QGLxDhfy7dasxlc5U_SLrFoOufim3oc/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly91cGxv/YWQud2lraW1lZGlh/Lm9yZy93aWtpcGVk/aWEvY29tbW9ucy9i/L2JjL1Vua25vd25f/cGVyc29uLmpwZw");
            user.setProfilePictureId(null);

        }
        catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error deleting profile picture",
                    e
            );
        }

        userRepository.save(user);
        notificationService.createNotification(
                userId,
                "Profile Picture Removed",
                "Your profile picture has been deleted and replaced with the default image.",
                NotificationType.PROFILE_PICTURE_DELETED
        );

        String subject = "Profile Picture Removed";
        String content = "Hello " + user.getFirstName() + ",\n\n" +
                "Your profile picture has been removed and replaced with the default image.\n\n" +
                "— The Baytaura Team";

        emailService.sendEmail(user.getEmail(), subject, content);

        if (user instanceof Provider){
            return userMapper.toProviderResponse( (Provider) user);
        }
        else {
            return userMapper.toUserResponse(user);
        }
    }

}
