package com.daniel.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.daniel.dto.request.TripBookingRequest;
import com.daniel.dto.response.SeatAvailabilityResponse;
import com.daniel.dto.response.TripBookingResponse;
import com.daniel.dto.response.TripResponse;
import com.daniel.entity.BookingEntity;
import com.daniel.entity.SeatEntity;
import com.daniel.entity.TripEntity;
import com.daniel.repository.BookingRepository;
import com.daniel.repository.SeatRepository;
import com.daniel.repository.TripRepository;

@Service
public class TravelService {

    private static final ZoneId KENYA_TIME_ZONE = ZoneId.of("Africa/Nairobi");

    private final TripRepository tripRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    public TravelService(TripRepository tripRepository, SeatRepository seatRepository,
            BookingRepository bookingRepository) {
        this.tripRepository = tripRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public List<TripResponse> searchTrips(String origin, String destination, LocalDate date) {
        LocalDate travelDate = date == null ? LocalDate.now(KENYA_TIME_ZONE) : date;
        LocalDateTime start = travelDate.atStartOfDay();
        LocalDateTime end = travelDate.plusDays(1).atStartOfDay();
        return tripRepository.searchScheduled(normalize(origin), normalize(destination), start, end)
                .stream()
                .map(this::toTripResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeatAvailabilityResponse> getSeats(Long tripId) {
        TripEntity trip = getTrip(tripId);
        Set<Long> bookedSeats = bookingRepository.findAllByTripIdAndStatus(tripId, "CONFIRMED").stream()
                .map(BookingEntity::getSeatId)
                .collect(Collectors.toSet());
        return seatRepository.findAllByCarIdOrderById(trip.getCarId()).stream()
                .map(seat -> new SeatAvailabilityResponse(
                        seat.getId(), seat.getSeatType(), !bookedSeats.contains(seat.getId())))
                .toList();
    }

    @Transactional
    public TripBookingResponse book(TripBookingRequest request) {
        TripEntity trip = getTrip(request.tripId());
        if (!"SCHEDULED".equals(trip.getStatus())
                || !trip.getDepartureTime().isAfter(LocalDateTime.now(KENYA_TIME_ZONE))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This trip is no longer available for booking");
        }

        SeatEntity seat = seatRepository.findById(request.seatId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
        if (!trip.getCarId().equals(seat.getCarId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat does not belong to this trip's bus");
        }
        if (bookingRepository.existsByTripIdAndSeatIdAndStatus(trip.getId(), seat.getId(), "CONFIRMED")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This seat has already been booked");
        }

        BookingEntity booking = new BookingEntity(
                trip.getCarId(), seat.getId(), trip.getFare(), trip.getRouteName(),
                trip.getOrigin(), trip.getDestination(), trip.getDepartureTime().toLocalDate(),
                trip.getId(), request.passengerName().trim(), request.passengerPhone().trim());
        try {
            return toBookingResponse(bookingRepository.saveAndFlush(booking), trip);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This seat has already been booked. Please choose another seat.", exception);
        }
    }

    @Transactional(readOnly = true)
    public TripBookingResponse findBooking(String reference) {
        BookingEntity booking = bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        TripEntity trip = getTrip(booking.getTripId());
        return toBookingResponse(booking, trip);
    }

    private TripEntity getTrip(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
    }

    private TripResponse toTripResponse(TripEntity trip) {
        int capacity = Math.toIntExact(seatRepository.countByCarId(trip.getCarId()));
        long booked = bookingRepository.countByTripIdAndStatus(trip.getId(), "CONFIRMED");
        return new TripResponse(trip.getId(), trip.getRouteName(), trip.getOrigin(),
                trip.getDestination(), trip.getDepartureTime(), trip.getArrivalTime(),
                trip.getFare(), trip.getCurrency(), Math.max(0, capacity - (int) booked));
    }

    private TripBookingResponse toBookingResponse(BookingEntity booking, TripEntity trip) {
        return new TripBookingResponse(booking.getBookingReference(), booking.getPassengerName(),
                trip.getOrigin(), trip.getDestination(),
                booking.getCarId(), booking.getSeatId(), trip.getDepartureTime(), booking.getPrice(),
                trip.getCurrency(), booking.getStatus(), booking.getPaymentStatus());
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toUpperCase(Locale.ROOT);
    }
}
