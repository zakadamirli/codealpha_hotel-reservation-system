package com.zekademirli.hostify.services;

import com.zekademirli.hostify.dto.response.ReservationResponse;
import com.zekademirli.hostify.entities.Property;
import com.zekademirli.hostify.entities.Reservation;
import com.zekademirli.hostify.entities.User;
import com.zekademirli.hostify.enums.ReservationStatus;
import com.zekademirli.hostify.exceptions.BadRequestException;
import com.zekademirli.hostify.exceptions.ResourceNotFoundException;
import com.zekademirli.hostify.exceptions.UnauthorizedException;
import com.zekademirli.hostify.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PropertyService propertyService;
    private final UserService userService;

    @Transactional
    public ReservationResponse createReservation(Long userId, Long propertyId,
                                                 String checkIn, String checkOut) {

        validateCreateReservationInput(userId, propertyId, checkIn, checkOut);

        User user = userService.getOneUser(userId);
        Property property = propertyService.getOneProperty(propertyId);

        LocalDate checkInDate = parseDate(checkIn, "check-in");
        LocalDate checkOutDate = parseDate(checkOut, "check-out");

        validateDates(checkInDate, checkOutDate);

        checkAvailability(property, checkInDate, checkOutDate);

        Reservation reservation = buildReservation(user, property, checkInDate, checkOutDate);

        log.info("Creating reservation for user {} and property {}", userId, propertyId);
        Reservation savedReservation = reservationRepository.save(reservation);

        return toReservationResponse(savedReservation);
    }

    @Transactional
    public ReservationResponse cancelReservation(Long reservationId, Long userId) {
        validateCancelInput(reservationId, userId);

        Reservation reservation = findReservationById(reservationId);

        validateCancellationRights(reservation, userId);
        validateCancellationStatus(reservation);
        validateCancellationTiming(reservation);

        reservation.setStatus(ReservationStatus.CANCELLED);

        log.info("Cancelled reservation {} by user {}", reservationId, userId);
        Reservation canceledReservation = reservationRepository.save(reservation);
        return toReservationResponse(canceledReservation);
    }

    @Transactional
    public ReservationResponse confirmReservation(Long reservationId, Long hostId) {
        validateInput(reservationId, "Reservation ID");
        validateInput(hostId, "Host ID");

        Reservation reservation = findReservationById(reservationId);

        if (!reservation.getProperty().getOwner().getId().equals(hostId)) {
            throw new UnauthorizedException("Only property owner can confirm reservations");
        }

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BadRequestException("Only pending reservations can be confirmed");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);

        log.info("Confirmed reservation {} by host {}", reservationId, hostId);
        Reservation confirmedReservation = reservationRepository.save(reservation);
        return toReservationResponse(confirmedReservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByUser(Long userId) {
        validateInput(userId, "User ID");
        userService.getOneUser(userId);
        List<Reservation> reservationList = reservationRepository.findByUserIdOrderByCheckInDateDesc(userId);
        return reservationList.stream()
                .map(this::toReservationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByProperty(Long propertyId, Long hostId) {
        validateInput(propertyId, "Property ID");
        validateInput(hostId, "Host ID");

        Property property = propertyService.getOneProperty(propertyId);

        if (!property.getOwner().getId().equals(hostId)) {
            throw new UnauthorizedException("You can only view reservations for your own properties");
        }

        List<Reservation> reservationList = reservationRepository.findByPropertyIdOrderByCheckInDateDesc(propertyId);
        return reservationList.stream()
                .map(this::toReservationResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getActiveReservationsByProperty(Long propertyId) {
        validateInput(propertyId, "Property ID");
        propertyService.getOneProperty(propertyId);
        List<Reservation> reservationList = reservationRepository.findByPropertyIdAndStatusIn(
                propertyId,
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );

        return reservationList.stream()
                .map(this::toReservationResponse)
                .toList();
    }

    private void validateCreateReservationInput(Long userId, Long propertyId, String checkIn, String checkOut) {
        validateInput(userId, "User ID");
        validateInput(propertyId, "Property ID");
        validateInput(checkIn, "Check-in date");
        validateInput(checkOut, "Check-out date");
    }

    private void validateCancelInput(Long reservationId, Long userId) {
        validateInput(reservationId, "Reservation ID");
        validateInput(userId, "User ID");
    }

    private void validateInput(Object input, String fieldName) {
        if (input == null) {
            throw new BadRequestException(fieldName + " is required");
        }
        if (input instanceof String && ((String) input).trim().isEmpty()) {
            throw new BadRequestException(fieldName + " cannot be empty");
        }
    }


    private Reservation findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));
    }

    private LocalDate parseDate(String dateStr, String fieldName) {
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid " + fieldName + " date format. Expected: YYYY-MM-DD");
        }
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the past");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        if (nights < 1) {
            throw new BadRequestException("Minimum stay is 1 night");
        }
    }

    private void checkAvailability(Property property, LocalDate checkIn, LocalDate checkOut) {
        boolean exists = reservationRepository.existsByPropertyIdAndCheckInDateLessThanAndCheckOutDateGreaterThanAndStatusIn(
                property.getId(), checkOut, checkIn,
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );

        if (exists) {
            throw new BadRequestException("Property is not available for selected dates");
        }
    }

    private void validateCancellationRights(Reservation reservation, Long userId) {
        if (!reservation.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You can only cancel your own reservations");
        }
    }

    private void validateCancellationStatus(Reservation reservation) {
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BadRequestException("Reservation is already cancelled");
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel completed reservations");
        }
    }

    private void validateCancellationTiming(Reservation reservation) {
        LocalDate today = LocalDate.now();

        if (reservation.getCheckInDate().minusDays(1).isBefore(today)) {
            //i defined cancelling time limit 24 hours
            throw new BadRequestException("Cancellation must be made at least 24 hours before check-in");
        }
    }

    private Reservation buildReservation(User user, Property property,
                                         LocalDate checkIn, LocalDate checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalPrice = calculateTotalPrice(property, nights);

        return Reservation.builder()
                .user(user)
                .property(property)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .totalPrice(totalPrice)
                .status(ReservationStatus.PENDING)

                .build();
    }

    private double calculateTotalPrice(Property property, long nights) {
        double basePrice = nights * property.getPricePerNight();

        double serviceFee = basePrice * 0.1;

        return basePrice + serviceFee;
    }

    public ReservationResponse toReservationResponse(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setCheckInDate(reservation.getCheckInDate());
        response.setCheckOutDate(reservation.getCheckOutDate());
        response.setTotalPrice(reservation.getTotalPrice());
        response.setStatus(response.getStatus());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setUpdatedAt(reservation.getUpdatedAt());

        ReservationResponse.UserSummary userSummary = new ReservationResponse.UserSummary();
        userSummary.setId(reservation.getUser().getId());
        userSummary.setName(reservation.getUser().getUsername());
        userSummary.setEmail(reservation.getUser().getEmail());

        ReservationResponse.PropertySummary propertySummary = new ReservationResponse.PropertySummary();
        propertySummary.setId(reservation.getProperty().getId());
        propertySummary.setTitle(reservation.getProperty().getName());
        propertySummary.setPricePerNight(reservation.getProperty().getPricePerNight());
        return response;
    }
}