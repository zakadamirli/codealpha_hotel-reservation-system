package com.zekademirli.hostify.repository;

import com.zekademirli.hostify.entities.Property;
import com.zekademirli.hostify.entities.Reservation;
import com.zekademirli.hostify.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserIdOrderByCheckInDateDesc(Long userId);

    List<Reservation> findByPropertyIdOrderByCheckInDateDesc(Long propertyId);

    List<Reservation> findByPropertyIdAndStatusIn(Long propertyId, List<ReservationStatus> statuses);

    boolean existsByPropertyIdAndCheckInDateLessThanAndCheckOutDateGreaterThanAndStatusIn(Long id, LocalDate checkOut, LocalDate checkIn, List<ReservationStatus> pending);
}

