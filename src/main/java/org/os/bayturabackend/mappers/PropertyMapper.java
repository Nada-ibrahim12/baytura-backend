package org.os.bayturabackend.mappers;

import org.os.bayturabackend.DTOs.PropertyRequestDTO;
import org.os.bayturabackend.DTOs.PropertyResponseDTO;
import org.os.bayturabackend.entities.Property;
import org.os.bayturabackend.entities.PropertyStatus;
import org.springframework.stereotype.Component;

@Component
public class PropertyMapper {

    public static PropertyResponseDTO toDto(Property property) {
        if (property == null) return null;

        PropertyResponseDTO dto = new PropertyResponseDTO();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setType(property.getType().name());
        dto.setPurpose(property.getPurpose().name());
        dto.setDescription(property.getDescription());
        dto.setPrice(property.getPrice());
        dto.setArea(property.getArea());
        dto.setAddress(property.getAddress());
        dto.setLatitude(property.getLatitude());
        dto.setLongitude(property.getLongitude());
        dto.setImages(MediaMapper.toDtoList(property.getImages()));

        dto.setPropertyStatus(property.getPropertyStatus().name());
        dto.setCreatedAt(property.getCreatedAt());
        dto.setUpdatedAt(property.getUpdatedAt());
        dto.setOwnerName(property.getOwner() != null ? property.getOwner().getFirstName() + " " + property.getOwner().getLastName() : null);

        return dto;
    }

    public Property toEntity(PropertyRequestDTO dto) {
        if (dto == null) return null;

        Property property = new Property();
        property.setTitle(dto.getTitle());
        property.setType(Enum.valueOf(org.os.bayturabackend.entities.PropertyType.class, dto.getType().toUpperCase()));
        property.setPurpose(Enum.valueOf(org.os.bayturabackend.entities.PropertyPurpose.class, dto.getPurpose().toUpperCase()));
        property.setDescription(dto.getDescription());
        property.setPrice(dto.getPrice());
        property.setArea(dto.getArea());
        property.setAddress(dto.getAddress());
        property.setLatitude(dto.getLatitude());
        property.setLongitude(dto.getLongitude());

        return property;
    }

}
