package org.os.bayturabackend.controllers;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.dto.PropertyRequest;
import org.os.bayturabackend.dto.PropertyResponse;
import org.os.bayturabackend.entities.Media;
import org.os.bayturabackend.services.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/properties")
public class PropertyController {
    private final PropertyService propertyService;


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
    **/
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


//    @PostMapping("/compare")
//
//    @PostMapping("/media")
//    ResponseEntity<PropertyResponse> uploadMedia(Media media) {}
//
//    @DeleteMapping("/{id}/media/{id)")
//    String deleteMedia() {}


}
