package com.zekademirli.hostify.controller;

import com.zekademirli.hostify.dto.response.ReservationResponse;
import com.zekademirli.hostify.services.ReservationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestParam Long userId,
            @RequestParam Long propertyId,
            @RequestParam String checkIn,
            @RequestParam String checkOut
    ) {
        ReservationResponse response = reservationService.createReservation(userId, propertyId, checkIn, checkOut);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable Long reservationId,
            @RequestParam Long userId
    ) {
        ReservationResponse response = reservationService.cancelReservation(reservationId, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reservationId}/confirm")
    public ResponseEntity<ReservationResponse> confirmReservation(
            @PathVariable Long reservationId,
            @RequestParam Long hostId
    ) {
        ReservationResponse response = reservationService.confirmReservation(reservationId, hostId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByUser(@PathVariable Long userId) {
        List<ReservationResponse> responses = reservationService.getReservationsByUser(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/get-property-reservation/{propertyId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByProperty(
            @PathVariable Long propertyId,
            @RequestParam Long hostId
    ) {
        List<ReservationResponse> responses = reservationService.getReservationsByProperty(propertyId, hostId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/property/{propertyId}/is-active")
    public ResponseEntity<List<ReservationResponse>> getActiveReservationsByProperty(
            @PathVariable Long propertyId
    ) {
        List<ReservationResponse> responses = reservationService.getActiveReservationsByProperty(propertyId);
        return ResponseEntity.ok(responses);
    }
}


