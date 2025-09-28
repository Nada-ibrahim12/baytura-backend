package org.os.bayturabackend.mappers;

import org.os.bayturabackend.DTOs.RequestCreateDTO;
import org.os.bayturabackend.DTOs.RequestResponseDTO;
import org.os.bayturabackend.entities.Media;
import org.os.bayturabackend.entities.PropertyPurpose;
import org.os.bayturabackend.entities.Request;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestMapper {

    public RequestResponseDTO toDto(Request request) {
        if (request == null) return null;

        RequestResponseDTO dto = new RequestResponseDTO();
        dto.setId(request.getId());
        dto.setTitle(request.getTitle());
        dto.setType(request.getType().name());
        dto.setPurpose(request.getPurpose().name());
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
        request.setPurpose(Enum.valueOf(PropertyPurpose.class, dto.getPurpose()));
        request.setDescription(dto.getDescription());
        request.setPrice(dto.getPrice());
        request.setArea(dto.getArea());
        request.setAddress(dto.getAddress());
        request.setLatitude(dto.getLatitude());
        request.setLongitude(dto.getLongitude());
//        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
//            List<Media> mediaList = new ArrayList<>();
//            for (int i = 0; i < dto.getFiles().size(); i++) {
//                MultipartFile file = dto.getFiles().get(i);
//                if (file != null && !file.isEmpty()) {
//                    String altName = (dto.getAltNames() != null && i < dto.getAltNames().size())
//                            ? dto.getAltNames().get(i)
//                            : file.getOriginalFilename();
//
//                    Media media = new Media();
//                    media.setAltName(altName);
//                    media.setPropertyDetails(request);
//                    mediaList.add(media);
//                }
//            }
//            request.setImages(mediaList);
//        }

        return request;
    }
}
