package org.os.bayturabackend.controllers;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.dto.MediaResponse;
import org.os.bayturabackend.dto.PropertyRequest;
import org.os.bayturabackend.dto.PropertyResponse;
import org.os.bayturabackend.entities.Media;
import org.os.bayturabackend.entities.PropertyStatus;
import org.os.bayturabackend.repositories.MediaRepository;
import org.os.bayturabackend.services.MediaService;
import org.os.bayturabackend.services.PropertyService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/properties")
public class PropertyController {
    private final PropertyService propertyService;
    private final MediaService mediaService;


    // Property Endpoints

    /**
     Create a new property listing
     🔹 POSTMAN Example:
     URL: POST http://localhost:8080/api/properties
     Body (JSON):
     {
     "title": "Luxury Villa",
     "description": "Sea view, 5 bedrooms",
     "type": "VILLA",
     "price": 2500000,
     "area": 450.0,
     "address": "North Coast, Egypt",
     "owner": { "id": 1 }
     }
    */
    @PostMapping
    public ResponseEntity<PropertyResponse> addProperty(@RequestBody PropertyRequest property) {
        return ResponseEntity.ok(propertyService.createProperty(property));
    }

    /**
     Get all property listings
     🔹 POSTMAN Example:
     URL: GET http://localhost:8080/api/properties
    */
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        return ResponseEntity.ok(propertyService.allProperties());
    }

    /**
     Get a single property by ID
     🔹 POSTMAN Example:
     URL: GET http://localhost:8080/api/properties/1
    */
    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getProperty(id));
    }

    /**
     Update an existing property
     🔹 POSTMAN Example:
     URL: PUT http://localhost:8080/api/properties/1
     Body (JSON):
     {
     "title": "Updated Villa",
     "description": "Now with pool",
     "type": "VILLA",
     "price": 2700000,
     "area": 470.0,
     "address": "North Coast, Egypt",
     "owner": { "id": 1 }
     }
    */
    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> editProperty(@PathVariable Long id, @RequestBody PropertyRequest property) {
        return ResponseEntity.ok(propertyService.updateProperty(id, property));
    }

    /**
     Delete a property by ID
     🔹 POSTMAN Example:
     URL: DELETE http://localhost:8080/api/properties/1
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.deleteProperty(id));
    }

    // Status Endpoints
    @PutMapping("/{id}/property-available")
    public ResponseEntity<PropertyResponse> propertyAvailable(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.makePropertyAvailable(id));
    }

    @PutMapping("/{id}/change-status")
    public ResponseEntity<PropertyResponse> changeStatus(
            @PathVariable Long id,
            @RequestBody PropertyStatus status
    ) {
        try {
            PropertyResponse updatedProperty = propertyService.changeStatus(id, status);
            return ResponseEntity.ok(updatedProperty);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


//    @PostMapping("/compare")
//    ResponseEntity<PropertyResponse> compareProperty(@RequestBody PropertyRequest property) {}


    // Media Endpoints
    @PostMapping("/{id}/media-upload")
    public ResponseEntity<PropertyResponse> uploadMedia(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        mediaService.addMedia(id, file);
        return ResponseEntity.ok(propertyService.getProperty(id));
    }

    @GetMapping("/media/{id}")
    public ResponseEntity<MediaResponse> getMedia(@PathVariable Long id) {
        return ResponseEntity.ok(mediaService.getMedia(id));
    }

    @GetMapping("/{id}/media")
    public ResponseEntity<List<MediaResponse>> getMediaOfProperty(@PathVariable Long id) {
        return ResponseEntity.ok(mediaService.getMediaOfProperty(id));
    }

    @DeleteMapping("/{propertyId}/media/{mediaId}")
    public ResponseEntity<String> deleteMedia(
            @PathVariable Long propertyId,
            @PathVariable Long mediaId
    ) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.ok("Media deleted successfully.");
    }

    // Search and Filters Endpoints

    /**
     Search properties by keyword (title, description, or address).

     🔹 POSTMAN Example:
     URL: GET http://localhost:8080/api/properties/search
     Body (Raw text):
     "villa"

     This will return all properties where the title, description, or address contains "villa".
    */
    @GetMapping("/search")
    public ResponseEntity<List<PropertyResponse>> searchProperties(@RequestParam  String query) {
        return ResponseEntity.ok(propertyService.search(query));
    }

    /**
     Filter properties by optional criteria (type, price, area, owner.....).
    
     🔹 POSTMAN Examples:
     Combine filters:
     GET /api/properties/filter?type=VILLA&minPrice=1000000&maxPrice=3000000
     GET /api/properties/filter?owner=Nada&maxPrice=2500000
     */
    @GetMapping("/filter")
    public ResponseEntity<List<PropertyResponse>> filterProperties(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minArea,
            @RequestParam(required = false) Double maxArea,
            @RequestParam(required = false) String owner
    ) {
        return ResponseEntity.ok(
                propertyService.filter(type, minPrice, maxPrice, minArea, maxArea, owner)
        );
    }


}
