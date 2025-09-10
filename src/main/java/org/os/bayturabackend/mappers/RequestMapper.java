package org.os.bayturabackend.mappers;

import org.os.bayturabackend.DTOs.RequestCreateDTO;
import org.os.bayturabackend.DTOs.RequestResponseDTO;
import org.os.bayturabackend.entities.Request;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public RequestResponseDTO toDto(Request request) {
        if (request == null) return null;

        RequestResponseDTO dto = new RequestResponseDTO();
        dto.setId(request.getId());
        dto.setTitle(request.getTitle());
        dto.setType(request.getType().name());
        dto.setDescription(request.getDescription());
        dto.setPrice(request.getPrice());
        dto.setArea(request.getArea());
        dto.setAddress(request.getAddress());
        dto.setLatitude(request.getLatitude());
        dto.setLongitude(request.getLongitude());
        dto.setImages(MediaMapper.toDtoList(request.getImages()));

        dto.setStatus(request.getStatus().name());
        dto.setCustomerName(request.getCustomer() != null ? request.getCustomer().getFirstName() + " " + request.getCustomer().getLastName() : null);

        return dto;
    }

    public Request toEntity(RequestCreateDTO dto) {
        if (dto == null) return null;

        Request request = new Request();
        request.setTitle(dto.getTitle());
        request.setType(Enum.valueOf(org.os.bayturabackend.entities.PropertyType.class, dto.getType()));
        request.setDescription(dto.getDescription());
        request.setPrice(dto.getPrice());
        request.setArea(dto.getArea());
        request.setAddress(dto.getAddress());
        request.setLatitude(dto.getLatitude());
        request.setLongitude(dto.getLongitude());

        return request;
    }
}
