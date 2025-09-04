package com.zekademirli.hostify.mapper;

import com.zekademirli.hostify.dto.response.PropertyResponse;
import com.zekademirli.hostify.entities.Property;
import org.springframework.stereotype.Component;

@Component
public class PropertyMapper {

    public PropertyResponse toPropertyResponse(Property property) {
        return PropertyResponse.builder()
                .ownerId(property.getOwner().getId())
                .name(property.getName())
                .description(property.getDescription())
                .address(property.getAddress())
                .pricePerNight(property.getPricePerNight())
                .maxGuests(property.getMaxGuests())
                .category(property.getCategory())
                .build();
    }
}
