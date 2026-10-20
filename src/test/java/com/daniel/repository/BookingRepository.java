package com.daniel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.daniel.entity.BookingEntity;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity,Long>{}
