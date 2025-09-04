package com.zekademirli.hostify.dto.response;

import com.zekademirli.hostify.enums.RoomCategory;
import lombok.Builder;

@Builder
public record PropertyResponse(
        Long ownerId,
        String name,
        String description,
        String address,
        Double pricePerNight,
        Integer maxGuests,
        RoomCategory category) {
}
