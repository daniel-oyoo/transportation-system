package com.daniel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.daniel.entity.SeatEntity;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity,Long>{

    Optional<SeatEntity> findByCarId(String carId);
}
