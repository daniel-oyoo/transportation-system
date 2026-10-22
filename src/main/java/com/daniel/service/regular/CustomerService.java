package com.daniel.service.regular;

import com.daniel.dto.request.BookingRequest;
import com.daniel.dto.response.BookingResponse;
import com.daniel.entity.BookingEntity;
import com.daniel.entity.SeatEntity;
import com.daniel.exception.SeatAlreadyBookedException;
import com.daniel.exception.SeatNotFoundException;
import com.daniel.repository.BookingRepository;
import com.daniel.repository.CarRepository;
import com.daniel.repository.SeatRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Service
public class CustomerService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final CarRepository carRepository;
    private final Logger logger = Logger.getLogger(CustomerService.class.getName());

    // Constructor injection (Spring Boot autowires single constructors automatically)
    public CustomerService(
            BookingRepository bookingRepository,
            SeatRepository seatRepository,
            CarRepository carRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.carRepository = carRepository;
    }

    @Transactional
    public BookingResponse book(BookingRequest request) {
        logger.info(() -> "Received a booking request by user: " + request.userId());

        // 1. Verify car exists
        if (!carRepository.existsByCarId(request.carId())) {
            throw new RuntimeException("No such car exists with ID: " + request.carId());
        }

        // 2. Fetch the specific seat for this car (Fixes the lookup bug)
        // Note: Ensure your SeatRepository has a method like findByCarIdAndId(String carId, Long seatId) 
        // or validate that the seat belongs to the requested car.
        SeatEntity seat = seatRepository.findById(request.seatId())
                .orElseThrow(() -> new SeatNotFoundException("Seat not found with ID: " + request.seatId()));

        if (!seat.getCarId().equals(request.carId())) {
            throw new SeatNotFoundException("Seat " + request.seatId() + " does not belong to car " + request.carId());
        }

        // 3. Check seat availability
        logger.info("Checking seat availability...");
        if (!seat.isAvailable()) {
            throw new SeatAlreadyBookedException("Seat with ID " + seat.getId() + " is already booked");
        }

        // 4. Update seat status
        logger.info("Booking the seat...");
        seat.setAvailable(false);
        seatRepository.save(seat);
        
        logger.info(() -> "Seat booked successfully for user: " + request.userId());

        // 5. Create and save booking entity
        BookingEntity booking = toEntity(request);
        BookingEntity saved = bookingRepository.save(booking);

        return toResponse(saved);
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::toResponse)
                .toList(); // Modern Java list collection
    }

    public List<BookingResponse> getBookingsByUser(String userId) {
        // Performance fix: Let the database filter instead of pulling all records into memory
        return bookingRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private BookingEntity toEntity(BookingRequest request) {
        return new BookingEntity(
                request.userId(),
                request.carId(),
                request.seatId(),
                request.price(),
                request.route(),
                request.from(),
                request.to(),
                request.travelDate()
        );
    }

    private BookingResponse toResponse(BookingEntity entity) {
        return new BookingResponse(
                entity.getUserId(),
                entity.getCarId(),
                entity.getSeatId(),
                entity.getRoute(),
                entity.getSource(),
                entity.getDestination(),
                entity.getTravelDate()
        );
    }
}