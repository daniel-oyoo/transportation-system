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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cars")
public class CarEntity { //[cite: 2]

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key placeholder[cite: 2]

    @Column(nullable = false, unique = true)
    private String carId; // License plate[cite: 2]

    @Column(nullable = false)
    private int numberOfSeats; //[cite: 2]

    @Column(nullable = false)
    private String carType; //[cite: 2]

    /**
     * Convenient constructor for creating a car entity without specifying the DB-generated ID.
     */
    public CarEntity(String carId, int numberOfSeats, String carType) { //[cite: 2]
        this.carId = carId;
        this.numberOfSeats = numberOfSeats;
        this.carType = carType;
    }
}