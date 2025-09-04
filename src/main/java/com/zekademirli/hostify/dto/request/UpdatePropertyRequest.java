package com.zekademirli.hostify.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePropertyRequest {

    private String name;
    private String description;
    private Double pricePerNight;
    private Integer maxGuest;
    private Long ownerId;
}