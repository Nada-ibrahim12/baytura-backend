package org.os.bayturabackend.controllers;


import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.UpdateProfileDTO;
import org.os.bayturabackend.DTOs.UserResponseDTO;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

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

}
