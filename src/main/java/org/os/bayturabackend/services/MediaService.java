package org.os.bayturabackend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.MediaResponse;
import org.os.bayturabackend.entities.Media;
import org.os.bayturabackend.entities.Property;
import org.os.bayturabackend.entities.Request;
import org.os.bayturabackend.exceptions.ResourceNotFoundException;
import org.os.bayturabackend.mappers.MediaMapper;
import org.os.bayturabackend.repositories.MediaRepository;
import org.os.bayturabackend.repositories.PropertyRepository;
import org.os.bayturabackend.repositories.RequestRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.os.bayturabackend.entities.NotificationType;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;
    private final PropertyRepository propertyRepository;
    private final Cloudinary cloudinary;
    private final NotificationService notificationService;
    private final RequestRepository requestRepository;

    public String uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("resource_type", "auto"));
        return uploadResult.get("secure_url").toString();
    }

    public void addMediaToRequest(Request request, MultipartFile file, String altName) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        String publicId = (String) uploadResult.get("public_id");
        String url = (String) uploadResult.get("secure_url");

        Media media = new Media();
        media.setPropertyDetails(request);
        media.setAltName(altName);
        media.setUrl(url);
        media.setPublicId(publicId);

        mediaRepository.save(media);
    }


    public MediaResponse addMedia(Long propertyId, MultipartFile file, Long ownerId, String altName) {
        try {
            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(() -> new RuntimeException("Property not found"));

            if (!property.getOwner().getUserId().equals(ownerId)) {
                throw new RuntimeException("You are not allowed to upload media to this property");
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            Media media = new Media();
            media.setUrl(url);
            media.setAltName(altName != null ? altName : file.getOriginalFilename()); // ✅ use altName if provided
            media.setPublicId(publicId);
            media.setPropertyDetails(property);

            Media saved = mediaRepository.save(media);

            notificationService.createNotification(
                    property.getOwner().getUserId(),
                    "Media Uploaded",
                    "A new media file was uploaded to property: " + property.getTitle(),
                    NotificationType.MEDIA_UPLOADED
            );

            return MediaMapper.toDto(saved);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }


    public MediaResponse addMedia(Long propertyId, MultipartFile file , Long ownerId) {
        try {

            Property property = propertyRepository.findById(propertyId)
                    .orElseThrow(
                            () -> new RuntimeException("Property not found")
                    );

            if (!property.getOwner().getUserId().equals(ownerId)) {
                throw new RuntimeException("You are not allowed to upload media to this property");
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            Media media = new Media();
            media.setUrl(url);
            media.setAltName(file.getOriginalFilename());
            media.setPublicId(publicId);
            media.setPropertyDetails(property);

            Media saved = mediaRepository.save(media);

            notificationService.createNotification(
                    property.getOwner().getUserId(),
                    "Media Uploaded",
                    "A new media file was uploaded to property: " + property.getTitle(),
                    NotificationType.MEDIA_UPLOADED
            );

            return MediaMapper.toDto(saved);

        }
        catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    public List<MediaResponse> getMediaOfProperty(Long propertyId) {
        List<Media> allMedia = mediaRepository.findByPropertyDetails_Id(propertyId);
        List<MediaResponse> mediaResponses = new ArrayList<>();
        for (Media media : allMedia) {
            mediaResponses.add(MediaMapper.toDto(media));
        }
        return mediaResponses;
    }

    public MediaResponse getMedia(Long mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found with id: " + mediaId));
        return MediaMapper.toDto(media);
    }


    public String deleteMedia(Long mediaId , Long ownerId , Long propertyId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Media not found with id: " + mediaId)
                );

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(
                    () -> new ResourceNotFoundException("Property not found with id: " + propertyId)
                );

        if (!property.getOwner().getUserId().equals(ownerId)) {
            throw new AccessDeniedException("You are not allowed to delete this media");
        }

        if (!media.getPropertyDetails().getId().equals(propertyId)) {
            throw new AccessDeniedException("You are not allowed to delete this media");
        }

        try {
            cloudinary.uploader().destroy(media.getPublicId(), ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete media from Cloudinary: " + e.getMessage());
        }

        mediaRepository.delete(media);

        notificationService.createNotification(
                property.getOwner().getUserId(),
                "Media Deleted",
                "A media file was deleted from property: " + property.getTitle(),
                NotificationType.MEDIA_DELETED
        );
        return "Media deleted successfully";
    }
}
