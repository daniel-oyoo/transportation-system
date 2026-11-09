package com.daniel.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daniel.entity.BookingEntity;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity,Long>{

    Optional<BookingEntity> findByBookingReference(String bookingReference);

    boolean existsByTripIdAndSeatIdAndStatus(Long tripId, Long seatId, String status);

    List<BookingEntity> findAllByTripIdAndStatus(Long tripId, String status);

    long countByTripIdAndStatus(Long tripId, String status);
}
