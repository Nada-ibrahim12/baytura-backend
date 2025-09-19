package org.os.bayturabackend.mappers;

import org.os.bayturabackend.DTOs.MediaResponse;
import org.os.bayturabackend.entities.Media;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MediaMapper {

    public static MediaResponse toDto(Media media) {
        if (media == null) return null;

        MediaResponse dto = new MediaResponse();
        dto.setId(media.getId());
        dto.setUrl(media.getUrl());
        dto.setAltName(media.getAltName());
        dto.setPublicId(media.getPublicId());
        return dto;
    }

    public static List<MediaResponse> toDtoList(List<Media> images) {
        if (images == null) return null;
        return images.stream().map(MediaMapper::toDto).collect(Collectors.toList());
    }
}
