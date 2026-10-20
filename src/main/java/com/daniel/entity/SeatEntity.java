package com.daniel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Data
@Table(name="seats")
@NoArgsConstructor
@AllArgsConstructor
public class SeatEntity {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//placeholder for pk
    @Column
    private String carId;//plate number
     @Column
    private boolean isAvailable;
    @Column
    private String seatType;

    public SeatEntity(boolean available,String seatType){
        this.isAvailable=available;
        this.seatType=seatType;
    }
}
