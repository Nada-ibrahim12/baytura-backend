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

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

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

        requestRepository.delete(request);
    }

    public void deleteRequestByIdAdmin(Long requestId) {
        Request request = requestRepository.findById(requestId)
                    .orElseThrow(
                        () -> new ResourceNotFoundException("Request not found")
                    );
        requestRepository.delete(request);
    }

    public RequestResponseDTO createNewRequest(RequestCreateDTO reqDTO , Long customerId) {
        Request request = requestMapper.toEntity(reqDTO);

        Customer customer = (Customer) userRepository.findById(customerId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer not found")
                );

        request.setCustomer(customer);

        request.setStatus(RequestStatus.PENDING);

        return requestMapper
                .toDto(
                        requestRepository.save(request)
                );

    }

    public RequestResponseDTO changeRequestStatus(Long requestId, String status) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Request not found")
                );

        if( request.getStatus() != RequestStatus.PENDING ) {
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
            property.setAddress(request.getAddress());
            property.setLatitude(request.getLatitude());
            property.setLongitude(request.getLongitude());
            property.setTitle(request.getTitle());
            property.setType(request.getType());
            property.setDescription(request.getDescription());
            property.setOwner(request.getCustomer());

            propertyRepository.save(property);
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
