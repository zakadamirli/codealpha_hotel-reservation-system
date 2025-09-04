package com.zekademirli.hostify.controller;

import com.zekademirli.hostify.dto.request.AddPropertyRequest;
import com.zekademirli.hostify.dto.request.UpdatePropertyRequest;
import com.zekademirli.hostify.dto.response.PropertyResponse;
import com.zekademirli.hostify.enums.RoomCategory;
import com.zekademirli.hostify.services.PropertyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Validated
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public ResponseEntity<PropertyResponse> addProperty(@Valid @RequestBody AddPropertyRequest request) {
        PropertyResponse response = propertyService.addProperty(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable @Positive Long id) {
        PropertyResponse response = propertyService.getPropertyById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        List<PropertyResponse> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> updateProperty(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdatePropertyRequest request) {
        PropertyResponse response = propertyService.updateProperty(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable @Positive Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Page<PropertyResponse>> getPropertiesByOwner(
            @PathVariable @NotNull @Positive Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyResponse> properties = propertyService.getPropertiesByOwnerId(ownerId, pageable);
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<PropertyResponse>> getPropertiesByCategory(
            @PathVariable @NotNull RoomCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PropertyResponse> properties = propertyService.getPropertiesByCategory(category, pageable);
        return ResponseEntity.ok(properties);
    }
}