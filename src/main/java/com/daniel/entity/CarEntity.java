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
@Table(name="cars")
public class CarEntity {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//placeholder for pk
     @Column
    private String carId;//plate
     @Column
    private int numberOfSeats;
     @Column
    private String carType;

    public CarEntity(String carId,int numberOfSeats,String carType){
        this.carId=carId;
        this.numberOfSeats=numberOfSeats;
        this.carType=carType;
    }
}
