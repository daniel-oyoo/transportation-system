package com.daniel.entity;

import java.math.BigDecimal;
import java.util.*;

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
@Table(name="bookings")
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//placeholder for pk
     @Column
    private String userId;
     @Column
    private String carId;//plate number
     @Column
    private String seatId;
     @Column
    private BigDecimal price;
    private String route;
     @Column
    private String source;
     @Column
    private String destination;
     @Column
    private Date travelDate;

    
    public BookingEntity(String userId, 
                         String carId, 
                         String seatId,
                          BigDecimal price, 
                          String route,
                           String source,
                           String destination, 
                           Date travelDate
                        ){
        this.userId=userId;
        this.carId=carId;
        this.seatId=seatId;
        this.price=price;
        this.route=route;
        this.source=source;
        this.destination=destination;
        this.travelDate=travelDate;
    }
}
