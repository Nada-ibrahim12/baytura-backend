package org.os.bayturabackend.services;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.dto.PropertyRequest;
import org.os.bayturabackend.dto.PropertyResponse;
import org.os.bayturabackend.entities.Property;
import org.os.bayturabackend.entities.PropertyStatus;
import org.os.bayturabackend.entities.Role;
import org.os.bayturabackend.entities.User;
import org.os.bayturabackend.repositories.MediaRepository;
import org.os.bayturabackend.repositories.PropertyRepository;
import org.os.bayturabackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;

    // get all property
    public List<PropertyResponse> allProperties() {
        List<Property> properties = propertyRepository.findAll();
        List<PropertyResponse> responses = new ArrayList<>();

        for (Property property : properties) {
            responses.add(mapToDTO(property));
        }
        return responses;
    }
    // get property
    public PropertyResponse getProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        return mapToDTO(property);

    }
    public Property getPropertyEntity(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        return property;

    }

    // post property
    public PropertyResponse createProperty(PropertyRequest propertyRequest) {
        Property property = new Property();
        property.setTitle(propertyRequest.getTitle());
        property.setDescription(propertyRequest.getDescription());
        property.setType(propertyRequest.getType());
        property.setPrice(propertyRequest.getPrice());
        property.setArea(propertyRequest.getArea());
        property.setAddress(propertyRequest.getAddress());
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        property.setLatitude(property.getLatitude());
        property.setLongitude(property.getLongitude());


        User owner = userRepository.findById(propertyRequest.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        property.setOwner(owner);

        if (owner.getRole() == Role.CUSTOMER) {
            property.setPropertyStatus(PropertyStatus.PENDING);
        } else if (owner.getRole() == Role.ADMIN) {
            property.setPropertyStatus(PropertyStatus.AVAILABLE);
        }

        Property saved = propertyRepository.save(property);
        return mapToDTO(saved);
    }


    // update property
    public PropertyResponse updateProperty(Long id, PropertyRequest propertyRequest) {
        Property property  = propertyRepository.findById(id).orElseThrow(() -> new RuntimeException("Property not found"));
        property.setTitle(propertyRequest.getTitle());
        property.setDescription(propertyRequest.getDescription());
        property.setType(propertyRequest.getType());
        property.setPrice(propertyRequest.getPrice());
        property.setArea(propertyRequest.getArea());

        Property updated = propertyRepository.save(property);
        return mapToDTO(updated);
    }

    // delete property
    public String deleteProperty(Long id) {
        propertyRepository.deleteById(id);
        return "Property deleted successfully";
    }


    public PropertyResponse mapToDTO(Property property) {
        PropertyResponse dto = new PropertyResponse();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setType(property.getType());
        dto.setPrice(property.getPrice());
        dto.setArea(property.getArea());
        dto.setAddress(property.getAddress());
        dto.setCreatedAt(property.getCreatedAt());
        dto.setUpdatedAt(property.getUpdatedAt());
        dto.setOwner(property.getOwner().getUserId());
        dto.setPropertyStatus(property.getPropertyStatus());
        dto.setLatitude(property.getLatitude());
        dto.setLongitude(property.getLongitude());
        dto.setImages(property.getImages());
        return dto;
    }


}
