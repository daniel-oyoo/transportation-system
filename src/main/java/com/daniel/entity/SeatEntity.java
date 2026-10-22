package com.daniel.entity;

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
@Table(name = "seats")
@NoArgsConstructor
@AllArgsConstructor
public class SeatEntity { //[cite: 3]

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key placeholder[cite: 3]

    @Column(nullable = false)
    private String carId; // License plate reference[cite: 3]

    @Column(nullable = false)
    private boolean isAvailable; //[cite: 3]

    @Column(nullable = false)
    private String seatType; //[cite: 3]

    /**
     * Convenient constructor used primarily during initial generation utility seeding.
     */
    public SeatEntity(boolean isAvailable, String seatType) { //[cite: 3]
        this.isAvailable = isAvailable;
        this.seatType = seatType;
    }
}