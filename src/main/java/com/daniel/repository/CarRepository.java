package com.daniel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daniel.entity.CarEntity;

@Repository
public interface CarRepository extends JpaRepository<CarEntity,Long>{

    boolean existsByCarId(String carId);
}
