package org.os.bayturabackend.controllers;


import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.FavoritePropertiesDTO;
import org.os.bayturabackend.DTOs.UpdateProfileDTO;
import org.os.bayturabackend.DTOs.UserResponseDTO;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.services.PropertyService;
import org.os.bayturabackend.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final PropertyService propertyService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(Authentication auth){
        User user = (User)auth.getPrincipal();
        Long userId = user.getUserId();
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            Authentication auth,
            @RequestBody UpdateProfileDTO updateProfileDTO
    ){
        User user = (User)auth.getPrincipal();
        Long userId = user.getUserId();
        return ResponseEntity.ok(
                userService.updateProfile(
                        userId,
                        updateProfileDTO
                )
        );
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(Authentication auth){
        User user = (User) auth.getPrincipal();
        Long userId = user.getUserId();
        userService.deleteProfile(userId);
        return ResponseEntity.ok("Profile deleted successfully.");
    }

    @PutMapping(value = "/profile/pfp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> updateProfilePicture(
            Authentication auth,
            @RequestParam("file") MultipartFile file
    ){
        User user = (User)auth.getPrincipal();
        Long userId = user.getUserId();
        return ResponseEntity.ok(
                userService.uploadPFP(
                        userId,
                        file
                )
        );
    }

    @DeleteMapping("/profile/pfp")
    public ResponseEntity<UserResponseDTO> deleteProfilePicture(Authentication auth) {
        User user = (User) auth.getPrincipal();
        Long userId = user.getUserId();

        UserResponseDTO response = userService.deletePFP(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/favorites")
    public ResponseEntity<List<FavoritePropertiesDTO>> getFavorites(Authentication auth){
        User user = (User)auth.getPrincipal();
        Long userId = user.getUserId();
        return ResponseEntity.ok(propertyService.getUserFavorites(userId));

    }


}
