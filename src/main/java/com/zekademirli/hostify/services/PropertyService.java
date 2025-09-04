package com.zekademirli.hostify.services;

import com.zekademirli.hostify.dto.request.AddPropertyRequest;
import com.zekademirli.hostify.dto.request.UpdatePropertyRequest;
import com.zekademirli.hostify.dto.response.PropertyResponse;
import com.zekademirli.hostify.entities.Property;
import com.zekademirli.hostify.entities.User;
import com.zekademirli.hostify.enums.RoomCategory;
import com.zekademirli.hostify.exceptions.*;
import com.zekademirli.hostify.mapper.PropertyMapper;
import com.zekademirli.hostify.repository.PropertyRepository;
import com.zekademirli.hostify.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserService userService;
    private final PropertyMapper propertyMapper;
    private final UserRepository userRepository;

    @Transactional
    public PropertyResponse addProperty(AddPropertyRequest request) {

        log.info("Adding new property for owner ID: {}", request.ownerId());

        validatePropertyRequest(request);

        User owner = userService.getOneUser(request.ownerId());

        Property property = buildProperty(request, owner);

        Property savedProperty = propertyRepository.save(property);
        log.info("Property successfully added with ID: {}", savedProperty.getId());

        return propertyMapper.toPropertyResponse(savedProperty);
    }

    @Transactional(readOnly = true)
    public PropertyResponse getPropertyById(Long id) {
        log.info("Getting property by ID: {}", id);
        Property property = propertyRepository.findDetailedById(id).orElseThrow(
                () -> new ResourceNotFoundException("Property not found: " + id)
        );
        return propertyMapper.toPropertyResponse(property);
    }

    @Transactional
    public Property getOneProperty(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<PropertyResponse> getAllProperties() {
        log.info("Getting all properties");
        List<Property> properties = propertyRepository.findAll();

        return properties.stream()
                .map(propertyMapper::toPropertyResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PropertyResponse updateProperty(Long propertyId, UpdatePropertyRequest request) {

        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ResourceNotFoundException("Property not found: " + propertyId)
        );
        if (request.getOwnerId() == null) {
            throw new BadRequestException("Owner ID is required");
        }

        if (!property.getOwner().getId().equals(request.getOwnerId())) {
            throw new UnauthorizedException("You are not authorized to update this property");
        }

        if (request.getPricePerNight() != null && request.getPricePerNight() <= 0) {
            throw new BadRequestException("Price per night must be positive");
        }
        if (request.getMaxGuest() != null && request.getMaxGuest() <= 0) {
            throw new BadRequestException("Max guests must be positive");
        }

        if (request.getDescription() != null) {
            property.setDescription(request.getDescription());
        }
        if (request.getName() != null) {
            property.setName(request.getName());
        }

        property.setPricePerNight(request.getPricePerNight());
        property.setMaxGuests(request.getMaxGuest());

        Property updatedProperty = propertyRepository.save(property);//for readability

        return propertyMapper.toPropertyResponse(updatedProperty);
    }

    @Transactional
    public void deleteProperty(Long propertyId) {
        log.info("Deleting property by ID: {}", propertyId);

        Property property = propertyRepository.findById(propertyId).orElseThrow(
                () -> new ResourceNotFoundException("Property not found: " + propertyId)
        );
        propertyRepository.delete(property);
    }

    @Transactional(readOnly = true)
    public Page<PropertyResponse> getPropertiesByOwnerId(@NotNull @Positive Long ownerId,
                                                         @NotNull Pageable pageable) {
        log.info("Getting properties by owner ID: {}", ownerId);
        validateOwnerId(ownerId);
        validatePageable(pageable);
        if (!userRepository.existsById(ownerId)) {
            log.warn("Owner not found with ID: {}", ownerId);
        }

        try {
            Page<Property> properties = propertyRepository.findAllByOwnerId(ownerId, pageable);
            if (properties.isEmpty()) {
                log.info("No properties found for owner ID: {}", ownerId);
            } else {
                log.info("Found {} properties for owner ID: {}",
                        properties.getTotalElements(), ownerId);
            }
            return properties.map(propertyMapper::toPropertyResponse);

        } catch (DataAccessException exception) {
            log.error("Database error while fetching properties for owner ID: {}", ownerId, exception);
            throw new ServiceException("Error retrieving properties for owner", exception);
        }
    }

    @Transactional
    public Page<PropertyResponse> getPropertiesByCategory(
            @NotNull RoomCategory category,
            @NotNull Pageable pageable) {
        log.info("Getting properties by room category: {}", category);
        validateCategory(category);
        validatePageable(pageable);

        try {
            Page<Property> properties = propertyRepository.findAllByCategory(category, pageable);
            if (properties.isEmpty()) {
                log.info("No properties found for room category: {}", category);
            } else {
                log.info("Found {} properties for room category: {}", properties.getTotalElements(), category);
            }
            return properties.map(propertyMapper::toPropertyResponse);
        } catch (DataAccessException exception) {
            log.error("Database error while fetching properties for room category: {}", category, exception);
            throw new ServiceException("Error retrieving properties for room category", exception);
        }
    }

    private void validateOwnerId(Long ownerId) {
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID cannot be null");
        }
        if (ownerId <= 0) {
            throw new IllegalArgumentException("Owner ID must be positive");
        }
    }

    private void validateCategory(RoomCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Room category cannot be null");
        }
    }

    private void validatePageable(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }

        if (pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("Page size cannot exceed 100");
        }

        if (pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Page size must be positive");
        }
    }


    private void validatePropertyRequest(AddPropertyRequest request) {
        if (request == null) {
            throw new PropertyValidationException("Property request cannot be null");
        }

        if (!StringUtils.hasText(request.name())) {
            throw new PropertyValidationException("Property name is required");
        }

        if (request.name().length() > 100) {
            throw new PropertyValidationException("Property name cannot exceed 100 characters");
        }

        if (!StringUtils.hasText(request.address())) {
            throw new PropertyValidationException("Address is required");
        }

        if (request.pricePerNight() == null || request.pricePerNight() <= 0) {
            throw new PropertyValidationException("Price per night must be greater than zero");
        }

        if (request.maxGuest() == null || request.maxGuest() <= 0) {
            throw new PropertyValidationException("Max guests must be greater than zero");
        }

        if (request.maxGuest() > 55) {
            throw new PropertyValidationException("Max guests cannot exceed 55");
        }

        if (request.ownerId() == null) {
            throw new PropertyValidationException("Owner ID is required");
        }
    }

    private Property buildProperty(AddPropertyRequest request, User owner) {
        return Property.builder()
                .name(sanitizeString(request.name()))
                .description(sanitizeString(request.description()))
                .address(sanitizeString(request.address()))
                .pricePerNight(request.pricePerNight())
                .maxGuests(request.maxGuest())
                .owner(owner)
                .category(request.category())
                .build();
    }

    private String sanitizeString(String input) {
        if (!StringUtils.hasText(input)) {
            return null;
        }
        return input.trim();
    }
}
