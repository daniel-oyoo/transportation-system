package com.daniel.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daniel.entity.BookingEntity;
import com.daniel.entity.SeatEntity;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity,Long>{

    List<BookingEntity> findByUserId(String userId);
}
