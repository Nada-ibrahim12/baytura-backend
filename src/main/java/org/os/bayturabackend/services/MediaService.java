package org.os.bayturabackend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.MediaResponse;
import org.os.bayturabackend.entities.Media;
import org.os.bayturabackend.entities.Property;
import org.os.bayturabackend.repositories.MediaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final PropertyService propertyService;
    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));
        return uploadResult.get("secure_url").toString();
    }

    public MediaResponse addMedia(Long propertyId, MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            Property property = propertyService.getPropertyEntity(propertyId);

            Media media = new Media();
            media.setUrl(url);
            media.setAltName(file.getOriginalFilename());
            media.setPublicId(publicId);
            media.setPropertyDetails(property);

            Media saved = mediaRepository.save(media);

            return mapToDTO(saved);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    public List<MediaResponse> getMediaOfProperty(Long propertyId) {
        List<Media> allMedia = mediaRepository.findByPropertyDetails_Id(propertyId);
        List<MediaResponse> mediaResponses = new ArrayList<>();
        for (Media media : allMedia) {
            mediaResponses.add(mapToDTO(media));
        }
        return mediaResponses;
    }

    public MediaResponse getMedia(Long mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found with id: " + mediaId));
        return mapToDTO(media);
    }

    public String deleteMedia(Long mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found with id: " + mediaId));

        try {
            // delete from cloudinary as well
            cloudinary.uploader().destroy(media.getPublicId(), ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete media from Cloudinary: " + e.getMessage());
        }

        mediaRepository.delete(media);
        return "Media deleted successfully";
    }

    private MediaResponse mapToDTO(Media media) {
        MediaResponse dto = new MediaResponse();
        dto.setId(media.getId());
        dto.setUrl(media.getUrl());
        dto.setAltName(media.getAltName());
        dto.setPublicId(media.getPublicId());
        return dto;
    }
}
