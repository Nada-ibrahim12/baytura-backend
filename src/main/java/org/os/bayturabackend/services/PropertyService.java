package org.os.bayturabackend.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.PropertyRequestDTO;
import org.os.bayturabackend.DTOs.PropertyResponseDTO;
import org.os.bayturabackend.entities.*;
import org.os.bayturabackend.exceptions.ForbiddenException;
import org.os.bayturabackend.exceptions.ResourceNotFoundException;
import org.os.bayturabackend.mappers.PropertyMapper;
import org.os.bayturabackend.repositories.PropertyRepository;
import org.os.bayturabackend.repositories.UserRepository;
import org.os.bayturabackend.specifications.PropertySpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;
    private final UserRepository userRepository;

    public List<PropertyResponseDTO> getProperties(
            String type,
            String query,
            Double minPrice, Double maxPrice,
            Double minArea, Double maxArea,
            String username
    ) {
        Specification<Property> spec = PropertySpecification.buildSpec(
                type, query, minPrice, maxPrice, minArea, maxArea, username
        );

        return propertyRepository.findAll(spec).stream()
                .map(PropertyMapper::toDto)
                .toList();
    }


    public PropertyResponseDTO getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property with ID " + id + " not found"));

        return PropertyMapper.toDto(property);
    }


    public List<PropertyResponseDTO> getMyProperties(Long ownerId) {
        return propertyRepository
                .findByOwner_UserId(ownerId)
                .stream()
                .map(PropertyMapper::toDto)
                .toList();
    }


    public PropertyResponseDTO createProperty(PropertyRequestDTO request, Long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!(user instanceof Provider provider) || !provider.getIsApproved()) {
            throw new ForbiddenException("Provider not approved to create properties");
        }

        Property property = new Property();
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setPrice(request.getPrice());
        property.setArea(request.getArea());
        property.setAddress(request.getAddress());
        property.setLatitude(request.getLatitude());
        property.setLongitude(request.getLongitude());
        property.setType(PropertyType.valueOf(request.getType().toUpperCase()));
        property.setOwner(provider);
        property.setPropertyStatus(PropertyStatus.AVAILABLE);
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());

        Property saved = propertyRepository.save(property);
        return PropertyMapper.toDto(saved);
    }




    public PropertyResponseDTO updateProperty(
            Long propertyId,
            PropertyRequestDTO request,
            Long ownerId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!property.getOwner().getUserId().equals(ownerId)) {
            throw new AccessDeniedException("You are not allowed to update this property");
        }

        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setPrice(request.getPrice());
        property.setArea(request.getArea());
        property.setAddress(request.getAddress());
        property.setLatitude(request.getLatitude());
        property.setLongitude(request.getLongitude());
        property.setType(PropertyType.valueOf(request.getType().toUpperCase()));
        property.setUpdatedAt(LocalDateTime.now());

        Property saved = propertyRepository.save(property);
        return PropertyMapper.toDto(saved);
    }


    public PropertyResponseDTO updatePropertyStatus(
            Long propertyId,
            String status,
            Long ownerId
    ){
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (!property.getOwner().getUserId().equals(ownerId)) {
            throw new AccessDeniedException("You are not allowed to update this property");
        }
        property.setPropertyStatus(PropertyStatus.valueOf(status.toUpperCase()));
        Property saved = propertyRepository.save(property);
        return PropertyMapper.toDto(saved);
    }


    public void deletePropertyOwner(
            Long propertyId,
            Long ownerId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (!property.getOwner().getUserId().equals(ownerId)) {
            throw new AccessDeniedException("You are not allowed to delete this property");
        }
        propertyRepository.delete(property);
    }

    public void deletePropertyAdmin(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        propertyRepository.delete(property);
    }





//    // change property status
//    public PropertyResponse changeStatus(Long id, PropertyStatus status) {
//        Property property = propertyRepository.findById(id).orElseThrow(() -> new RuntimeException("Property not found"));
//        property.setPropertyStatus(status);
//        propertyRepository.save(property);
//        return mapToDTO(property);
//    }



}
