package org.os.bayturabackend.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.os.bayturabackend.DTOs.ChangeStatusDTO;
import org.os.bayturabackend.DTOs.MediaResponse;
import org.os.bayturabackend.DTOs.PropertyRequestDTO;
import org.os.bayturabackend.DTOs.PropertyResponseDTO;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.services.MediaService;
import org.os.bayturabackend.services.PropertyService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/")
public class PropertyController {
    private final PropertyService propertyService;
    private final MediaService mediaService;


    // ? for all users
    @GetMapping("properties")
    public ResponseEntity<List<PropertyResponseDTO>> getProperties(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String purpose,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minArea,
            @RequestParam(required = false) Double maxArea,
            @RequestParam(required = false) String owner, Authentication auth
    ) {
        User user = (User)auth.getPrincipal();
        Long userId = user.getUserId();
        return ResponseEntity.ok(
                propertyService.getProperties(
                        type,
                        purpose,
                        searchQuery,
                        minPrice,
                        maxPrice,
                        minArea,
                        maxArea,
                        owner,
                        userId
                )
        );
    }


    // ? for all users
    @GetMapping("properties/{id}")
    public ResponseEntity<PropertyResponseDTO> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }


    // ? provider and customer (get the properties he owns)
    @GetMapping("properties/my")
    @PreAuthorize("hasAnyRole('PROVIDER','CUSTOMER')")
    public ResponseEntity<List<PropertyResponseDTO>> getMyProperties(Authentication auth){
        User user = (User)auth.getPrincipal();
        Long userId = user.getUserId();
        return ResponseEntity.ok(propertyService.getMyProperties(userId));
    }


    // ? provider only
    @PostMapping(value = "properties", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<PropertyResponseDTO> createProperty(
            Authentication auth,
            @ModelAttribute PropertyRequestDTO propertyRequestDTO){
        log.info("request DTO: {}", propertyRequestDTO.getFiles());

        User user = (User)auth.getPrincipal();
        Long OwnerId = user.getUserId();
        return ResponseEntity.ok(propertyService.createProperty(propertyRequestDTO, OwnerId));
    }

    // ? provider and customer (update the properties he owns)
    @PutMapping("properties/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER','CUSTOMER')")
    public ResponseEntity<PropertyResponseDTO> updateProperty(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody PropertyRequestDTO propertyRequestDTO){
        User user = (User)auth.getPrincipal();
        Long OwnerId = user.getUserId();
        return ResponseEntity.ok(propertyService.updateProperty(id, propertyRequestDTO, OwnerId));
    }


    @PutMapping("properties/{id}/change-status")
    @PreAuthorize("hasAnyRole('PROVIDER','CUSTOMER')")
    public ResponseEntity<PropertyResponseDTO> changePropertyStatus(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody ChangeStatusDTO changeStatusDTO){
        User user = (User)auth.getPrincipal();
        Long ownerId = user.getUserId();
        return ResponseEntity.ok(
                propertyService.updatePropertyStatus(
                        id,
                        changeStatusDTO.getStatus(),
                        ownerId
                )
        );
    }


    @DeleteMapping("properties/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER' , 'CUSTOMER')")
    public ResponseEntity<String> deletePropertyOwner(
            Authentication auth,
            @PathVariable Long id
    ){
        User user = (User)auth.getPrincipal();
        Long ownerId = user.getUserId();
        propertyService.deletePropertyOwner(id, ownerId);
        return ResponseEntity.ok("Property deleted successfully.");
    }

    @DeleteMapping("admin/properties/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePropertyAdmin(
            @PathVariable Long id
    ){
        propertyService.deletePropertyAdmin(id);
        return ResponseEntity.ok("Property deleted successfully.");
    }

//    ! =========================================================================================================================

    // ? Media Endpoints


    // ? provider and customer (upload the media he owns)
    @PostMapping("properties/{id}/media-upload")
    @PreAuthorize("hasAnyRole('PROVIDER','CUSTOMER')")
    public ResponseEntity<PropertyResponseDTO> uploadMedia(
            Authentication auth,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        User user = (User)auth.getPrincipal();
        Long OwnerId = user.getUserId();
        mediaService.addMedia(id, file, OwnerId);
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }


    // ? for all users
    @GetMapping("properties/media/{id}")
    public ResponseEntity<MediaResponse> getMedia(@PathVariable Long id) {
        return ResponseEntity.ok(mediaService.getMedia(id));
    }


    // ? for all users
    @GetMapping("properties/{id}/media")
    public ResponseEntity<List<MediaResponse>> getMediaOfProperty(@PathVariable Long id) {
        return ResponseEntity.ok(mediaService.getMediaOfProperty(id));
    }


    // ? provider and customer (delete the media he owns)
    @DeleteMapping("properties/{propertyId}/media/{mediaId}")
    @PreAuthorize("hasAnyRole('PROVIDER','CUSTOMER')")
    public ResponseEntity<String> deleteMedia(
            Authentication auth,
            @PathVariable Long propertyId,
            @PathVariable Long mediaId
    ) {
        User user = (User)auth.getPrincipal();
        Long OwnerId = user.getUserId();
        mediaService.deleteMedia(mediaId , OwnerId , propertyId);
        return ResponseEntity.ok("Media deleted successfully.");
    }


//    ! =========================================================================================================================

    // ? Favorite Endpoints

    @PostMapping("properties/{id}/favorite")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> addToFavorite(Authentication authentication, @PathVariable Long id) {
        User user = (User)authentication.getPrincipal();
        Long customerId = user.getUserId();
        return ResponseEntity.ok(propertyService.addToFavorite(customerId, id));

    }

    @DeleteMapping("properties/{id}/unfavorite")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> removeFromFavorite(Authentication authentication, @PathVariable Long id) {
        User user = (User)authentication.getPrincipal();
        Long customerId = user.getUserId();
        return ResponseEntity.ok(propertyService.removeFromFavorite(customerId, id));
    }

}
