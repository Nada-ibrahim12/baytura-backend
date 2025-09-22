package org.os.bayturabackend.services;

import lombok.RequiredArgsConstructor;
import org.os.bayturabackend.DTOs.RequestCreateDTO;
import org.os.bayturabackend.DTOs.RequestResponseDTO;
import org.os.bayturabackend.entities.*;
import org.os.bayturabackend.exceptions.ForbiddenException;
import org.os.bayturabackend.exceptions.ResourceNotFoundException;
import org.os.bayturabackend.mappers.RequestMapper;
import org.os.bayturabackend.repositories.PropertyRepository;
import org.os.bayturabackend.repositories.RequestRepository;
import org.os.bayturabackend.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;
    private final NotificationService notificationService;
    private final MediaService mediaService;
    private final EmailService emailService;

    public List<RequestResponseDTO> getRequests() {
        List<RequestResponseDTO> requests = requestRepository.findAll().stream().map(requestMapper::toDto).toList();
        return requests;
    }

    public List<RequestResponseDTO> getRequestsByCustomer(Long customerId) {
        return requestRepository
                .findByCustomer_UserId(customerId)
                .stream()
                .map(requestMapper::toDto)
                .toList();
    }

    public RequestResponseDTO getRequestByIdAdmin(Long requestId) {
        return requestMapper
                .toDto(
                        requestRepository.findById(requestId)
                                .orElseThrow(
                                    () -> new ResourceNotFoundException("Request not found")
                                )
                );
    }

    public RequestResponseDTO getRequestByIdCustomer(Long requestId, Long customerId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Request not found")
                );

        if (!Objects.equals(request.getCustomer().getUserId(), customerId)) {
            throw new ForbiddenException("You are not allowed to view this request");
        }

        return requestMapper.toDto(request);
    }

    public void deleteRequestByIdCustomer(Long requestId , Long customerId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(
                    () -> new ResourceNotFoundException("Request not found")
                );

        if (!Objects.equals(request.getCustomer().getUserId(), customerId)) {
            throw new ForbiddenException("You are not allowed to delete this request");
        }
        notificationService.createNotification(
                customerId,
                "Request Deleted",
                "Your request '" + request.getTitle() + "' has been deleted successfully.",
                NotificationType.REQUEST_DELETED
        );

        requestRepository.delete(request);
    }

    public void deleteRequestByIdAdmin(Long requestId) {
        Request request = requestRepository.findById(requestId)
                    .orElseThrow(
                        () -> new ResourceNotFoundException("Request not found")
                    );

        notificationService.createNotification(
                request.getCustomer().getUserId(),
                "Request Deleted",
                "Your request '" + request.getTitle() + "' has been deleted.",
                NotificationType.REQUEST_DELETED
        );
        requestRepository.delete(request);
    }

    public RequestResponseDTO createNewRequest(RequestCreateDTO reqDTO, Long customerId) throws IOException {
        Request request = requestMapper.toEntity(reqDTO);

        Customer customer = (Customer) userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        request.setCustomer(customer);
        request.setStatus(RequestStatus.PENDING);

        Request savedRequest = requestRepository.save(request);

        if (reqDTO.getFiles() != null && !reqDTO.getFiles().isEmpty()) {
            for (int i = 0; i < reqDTO.getFiles().size(); i++) {
                MultipartFile file = reqDTO.getFiles().get(i);

                if (file != null && !file.isEmpty()) {
                    String altName = (reqDTO.getAltNames() != null && i < reqDTO.getAltNames().size())
                            ? reqDTO.getAltNames().get(i)
                            : file.getOriginalFilename();

                    mediaService.addMediaToRequest(savedRequest, file, altName);
                }
            }
        }
        notificationService.createNotification(
                customerId,
                "Request Created",
                "Your request '" + savedRequest.getTitle() + "' has been created and is pending approval.",
                NotificationType.REQUEST_CREATED
        );

        return requestMapper.toDto(savedRequest);
    }

    public RequestResponseDTO changeRequestStatus(Long requestId, String status) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Request not found")
                );

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ForbiddenException("You are not allowed to change the status of this request");
        }

        RequestStatus newStatus;
        try {
            newStatus = RequestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        request.setStatus(newStatus);

        if (newStatus == RequestStatus.ACCEPTED) {
            Property property = new Property();
            property.setPropertyStatus(PropertyStatus.AVAILABLE);
            property.setArea(request.getArea());
            property.setPrice(request.getPrice());
            property.setPurpose(PropertyPurpose.RENT);
//            if (request.getPurpose() != null) {
//
//            }
            property.setAddress(request.getAddress());
            property.setLatitude(request.getLatitude());
            property.setLongitude(request.getLongitude());
            property.setTitle(request.getTitle());
            property.setType(request.getType());
            property.setDescription(request.getDescription());
            property.setOwner(request.getCustomer());

            propertyRepository.save(property);

            // ? App notification
            notificationService.createNotification(
                    request.getCustomer().getUserId(),
                    "Request Accepted",
                    "Your request '" + request.getTitle() + "' has been accepted. Property is now available.",
                    NotificationType.REQUEST_ACCEPTED
            );

            // ? Email notification
            Context context = new Context();
            context.setVariable("userName", request.getCustomer().getFirstName());
            context.setVariable("propertyTitle", property.getTitle());

            emailService.sendEmailFromTemplate(
                    request.getCustomer().getEmail(),
                    "Your Request Has Been Accepted 🎉",
                    "emails/request-status/request_accepted",
                    context
            );

            System.out.println("\nEmail sent to " + request.getCustomer().getEmail());

        }
        else if (newStatus == RequestStatus.REJECTED) {
            // ? App notification
            notificationService.createNotification(
                    request.getCustomer().getUserId(),
                    "Request Rejected",
                    "Your request '" + request.getTitle() + "' has been rejected.",
                    NotificationType.REQUEST_REJECTED
            );

            // ? Email notification
            Context context = new Context();
            context.setVariable("userName", request.getCustomer().getFirstName());
            context.setVariable("propertyTitle", request.getTitle());

            emailService.sendEmailFromTemplate(
                    request.getCustomer().getEmail(),
                    "Your Request Has Been Rejected ❌",
                    "emails/request-status/request_rejected",
                    context
            );

            System.out.println("\nEmail sent to " + request.getCustomer().getEmail());
        }

        Request updatedRequest = requestRepository.save(request);
        return requestMapper.toDto(updatedRequest);
    }

    public List<RequestResponseDTO> getRequestsAdmin(String status, String username) {
        List<Request> requests;

        if (status != null && username != null) {
            requests = requestRepository
                            .findByStatusAndCustomer_Username(
                                RequestStatus.valueOf(status.toUpperCase()),
                                username
                            );
        }
        else if (status != null) {
            requests = requestRepository.findByStatus(
                    RequestStatus.valueOf(status.toUpperCase())
            );
        }
        else if (username != null) {
            requests = requestRepository.findByCustomer_Username(username);
        }
        else {
            requests = requestRepository.findAll();
        }

        return requests.stream()
                .map(requestMapper::toDto)
                .toList();
    }

}
