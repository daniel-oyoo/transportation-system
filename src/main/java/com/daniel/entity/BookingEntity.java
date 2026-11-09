package com.daniel.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "bookings", uniqueConstraints = {
        @UniqueConstraint(name = "uk_booking_trip_seat", columnNames = { "trip_id", "seat_id" })
})
@NoArgsConstructor
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String carId;

    @Column(nullable = false)
    private Long seatId;

    @Column(nullable = false)
    private BigDecimal price;

    @Column
    private String route;

    @Column
    private String source;

    @Column
    private String destination;

    @Column(nullable = false)
    private LocalDate travelDate;

    @Column(unique = true, length = 36)
    private String bookingReference;

    private Long tripId;

    private String passengerName;

    private String passengerPhone;

    @Column(nullable = false)
    private String status = "CONFIRMED";

    @Column(nullable = false)
    private String paymentStatus = "PAY_AT_STATION";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public BookingEntity(String carId, Long seatId, BigDecimal price, String route,
            String source, String destination, LocalDate travelDate, Long tripId,
            String passengerName, String passengerPhone) {
        this.carId = carId;
        this.seatId = seatId;
        this.price = price;
        this.route = route;
        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
        this.tripId = tripId;
        this.passengerName = passengerName;
        this.passengerPhone = passengerPhone;
    }

    @jakarta.persistence.PrePersist
    void prepareForInsert() {
        if (bookingReference == null) {
            bookingReference = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}