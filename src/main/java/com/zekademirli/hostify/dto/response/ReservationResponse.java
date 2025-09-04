package com.zekademirli.hostify.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ReservationResponse {
    private Long id;
    private UserSummary user;
    private PropertySummary property;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class UserSummary {
        private Long id;
        private String name;
        private String email;
    }

    @Data
    public static class PropertySummary {
        private Long id;
        private String title;
        private Double pricePerNight;
    }
}
