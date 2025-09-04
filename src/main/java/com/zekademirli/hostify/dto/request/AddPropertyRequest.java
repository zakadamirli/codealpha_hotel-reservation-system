package com.zekademirli.hostify.dto.request;

import com.zekademirli.hostify.enums.RoomCategory;

public record  AddPropertyRequest(
     String name,
     String description,
     RoomCategory category,
     String address,
     Double pricePerNight,
     Integer maxGuest,
     Long ownerId
){}