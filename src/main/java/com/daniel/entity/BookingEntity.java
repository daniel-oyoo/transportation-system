package com.daniel.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity { //[cite: 1]

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key placeholder[cite: 1]

    @Column(nullable = false)
    private String userId; //[cite: 1]

    @Column(nullable = false)
    private String carId; // License plate reference[cite: 1]

    @Column(nullable = false)
    private Long seatId; //[cite: 1]

    @Column(nullable = false)
    private BigDecimal price; //[cite: 1]

    @Column
    private String route; //[cite: 1]

    @Column
    private String source; //[cite: 1]

    @Column
    private String destination; //[cite: 1]

    @Column(nullable = false)
    private LocalDate travelDate; // Modern Java Time API[cite: 1]

    /**
     * Convenient constructor for creating a booking entity without specifying the DB-generated ID.
     */
    public BookingEntity(
            String userId, 
            String carId, 
            Long seatId,
            BigDecimal price, 
            String route,
            String source,
            String destination, 
            LocalDate travelDate
    ) { //[cite: 1]
        this.userId = userId;
        this.carId = carId;
        this.seatId = seatId;
        this.price = price;
        this.route = route;
        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
    }
}