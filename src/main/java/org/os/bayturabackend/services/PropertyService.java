package org.os.bayturabackend.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.*;
import org.os.bayturabackend.entities.*;
import org.os.bayturabackend.exceptions.ForbiddenException;
import org.os.bayturabackend.exceptions.ResourceNotFoundException;
import org.os.bayturabackend.mappers.PropertyMapper;
import org.os.bayturabackend.repositories.*;
import org.os.bayturabackend.specifications.PropertySpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final CustomerRepository customerRepository;
    private final FavoriteRepository favoriteRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final MediaService mediaService;

    public PaginatedResponse<PropertyResponseDTO> getProperties(
            String type, String purpose, String query,
            Double minPrice, Double maxPrice,
            Double minArea, Double maxArea,
            String username, Long userId,
            int page, int size
    ) {
        Specification<Property> spec = PropertySpecification.buildSpec(
                type, purpose, query, minPrice, maxPrice, minArea, maxArea, username
        );
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Property> propertyPage = propertyRepository.findAll(spec, pageable);

        if (query != null && !query.isBlank()) {
            Map<String, Object> filters = new HashMap<>();
            if (minPrice != null) filters.put("minPrice", minPrice);
            if (maxPrice != null) filters.put("maxPrice", maxPrice);
            if (minArea != null) filters.put("minArea", minArea);
            if (maxArea != null) filters.put("maxArea", maxArea);
            if (type != null && !type.isBlank()) filters.put("type", type);
            if (purpose != null && !purpose.isBlank()) filters.put("purpose", purpose);
            if (username != null && !username.isBlank()) filters.put("username", username);

            Customer customer = customerRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setSearchQuery(query);
            searchHistory.setFiltersUsed(filters);
            searchHistory.setCustomer(customer);

            searchHistoryRepository.save(searchHistory);
        }

        List<PropertyResponseDTO> properties = propertyPage.getContent()
                .stream()
                .map(PropertyMapper::toDto)
                .toList();

        return new PaginatedResponse<>(
                properties,
                propertyPage.getNumber(),
                propertyPage.getSize(),
                propertyPage.getTotalElements(),
                propertyPage.getTotalPages(),
                propertyPage.hasNext(),
                propertyPage.hasPrevious()
        );

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

        if (!(user instanceof Provider provider) || !(provider.getStatus() == ProviderStatus.ACCEPTED)) {
            throw new ForbiddenException("Provider not approved to create properties");
        }

        boolean isFirstProperty = propertyRepository.countByOwner(user) == 0;

        Property property = new Property();
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setPurpose(PropertyPurpose.valueOf(request.getPurpose().toUpperCase()));
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

        if (request.getFiles() != null) {
            for (int i = 0; i < request.getFiles().size(); i++) {
                MultipartFile file = request.getFiles().get(i);
                String altName = (request.getAltNames() != null && i < request.getAltNames().size())
                        ? request.getAltNames().get(i)
                        : file.getOriginalFilename();

                if (file != null && !file.isEmpty()) {
                    mediaService.addMedia(saved.getId(), file, property.getOwner().getUserId(), altName);
                }
            }
        }

        notificationService.createNotification(
                provider.getUserId(),
                "Property Created",
                "Your property '" + saved.getTitle() + "' has been created successfully.",
                NotificationType.PROPERTY_CREATED
        );

        if (isFirstProperty) {
            String subject = "Congratulations on your first property!";
            String content = "Hi " + provider.getFirstName() + ",\n\n" +
                    "You just created your first property: '" + saved.getTitle() + "'. " +
                    "Start adding more properties and enjoy all the benefits of being a Baytaura provider!\n\n" +
                    "— The Baytaura Team";

            emailService.sendEmail(provider.getEmail(), subject, content);
        }

        return PropertyMapper.toDto(saved);
    }


    public String addToFavorite(Long customerId, Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        boolean alreadyFavorited = favoriteRepository.existsByCustomerAndProperty(customer, property);
        if (alreadyFavorited) {
            return "Property '" + property.getTitle() + "' is already in favorites for customer ID " + customerId;
        }

        Favorite favorite = new Favorite();
        favorite.setCustomer(customer);
        favorite.setProperty(property);

        favoriteRepository.save(favorite);

        notificationService.createNotification(
                property.getOwner().getUserId(),
                "Property Favorited",
                "Your property '" + property.getTitle() + "' was added to favorites by " + customer.getUsername(),
                NotificationType.FAVORITE
        );

        return "User " + customerId + " saved property '" + property.getTitle() + "' to favorites successfully.";
    }


    public String removeFromFavorite(Long customerId, Long favoriteId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite property not found"));

        if (!favorite.getCustomer().getUserId().equals(customerId)) {
            throw new IllegalArgumentException("This favorite does not belong to the given customer");
        }

        favoriteRepository.delete(favorite);

        return "Favorite with ID " + favoriteId + " has been removed successfully.";
    }

    public List<FavoritePropertiesDTO> getUserFavorites(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        List<Favorite> favorites = favoriteRepository.findByCustomer(customer);

        return favorites.stream()
                .map(this::toDto)
                .toList();
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
        if (request.getPurpose() == null) {
            property.setPurpose(PropertyPurpose.RENT);
        } else {
            property.setPurpose(PropertyPurpose.valueOf(request.getPurpose().toUpperCase()));
        }
        property.setPrice(request.getPrice());
        property.setArea(request.getArea());
        property.setAddress(request.getAddress());
        property.setLatitude(request.getLatitude());
        property.setLongitude(request.getLongitude());
        property.setType(PropertyType.valueOf(request.getType().toUpperCase()));
        property.setUpdatedAt(LocalDateTime.now());
        Property saved = propertyRepository.save(property);

        notificationService.createNotification(
                property.getOwner().getUserId(),
                "Property Updated",
                "Your property '" + saved.getTitle() + "' was updated successfully.",
                NotificationType.PROPERTY_MODIFIED
        );

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


//    by owner
    public void deletePropertyOwner(
            Long propertyId,
            Long ownerId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (!property.getOwner().getUserId().equals(ownerId)) {
            throw new AccessDeniedException("You are not allowed to delete this property");
        }
        notificationService.createNotification(
                property.getOwner().getUserId(),
                "Property Deleted",
                "Your property '" + property.getTitle() + "' was deleted.",
                NotificationType.PROPERTY_DELETED
        );
        propertyRepository.delete(property);
    }

//    by admin
    public void deletePropertyAdmin(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        propertyRepository.delete(property);

        notificationService.createNotification(
                property.getOwner().getUserId(),
                "Property Deleted by Admin",
                "Your property '" + property.getTitle() + "' was deleted by an administrator.",
                NotificationType.PROPERTY_DELETED
        );
    }

    public FavoritePropertiesDTO toDto(Favorite favorite) {
        PropertyResponseDTO propertyDto = PropertyMapper.toDto(favorite.getProperty());

        return new FavoritePropertiesDTO(
                favorite.getFavoriteId(),
                propertyDto
        );
    }

}
