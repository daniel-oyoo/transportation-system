package com.daniel.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.daniel.entity.TripEntity;

public interface TripRepository extends JpaRepository<TripEntity, Long> {

    @Query("""
            select t from TripEntity t
            where t.status = 'SCHEDULED'
              and t.departureTime >= :start
              and t.departureTime < :end
              and (:origin is null or lower(t.origin) = lower(:origin))
              and (:destination is null or lower(t.destination) = lower(:destination))
            order by t.departureTime
            """)
    List<TripEntity> searchScheduled(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
