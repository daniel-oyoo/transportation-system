package com.daniel;

import com.daniel.dto.request.BookingRequest;
import com.daniel.entity.CarEntity;
import com.daniel.entity.SeatEntity;
import com.daniel.repository.BookingRepository;
import com.daniel.repository.CarRepository;
import com.daniel.repository.SeatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private CarEntity testCar;
    private SeatEntity testSeat;

    @BeforeEach
    void setUp() {
        // Clear previous state for clean test runs
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        carRepository.deleteAll();

        // Setup mock test car
        testCar = new CarEntity("KAA 1234A", 30, "Bus");
        testCar = carRepository.save(testCar);

        // Setup mock test available seat linked to the car
        testSeat = new SeatEntity(true, "window");
        testSeat.setCarId(testCar.getCarId());
        testSeat = seatRepository.save(testSeat);
    }

    @Test
    @DisplayName("Successfully book an available seat")
    void testBookSeatSuccess() throws Exception {
        BookingRequest request = new BookingRequest(
                "user-101",
                testCar.getCarId(),
                testSeat.getId(),
                new BigDecimal("50.00"),
                "Route A",
                "City X",
                "City Y",
                LocalDate.now().plusDays(1)
        );

        mockMvc.perform(post("/api/v1/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user-101"))
                .andExpect(jsonPath("$.carId").value(testCar.getCarId()))
                .andExpect(jsonPath("$.seatId").value(testSeat.getId()));
    }

    @Test
    @DisplayName("Fail to book a seat that is already booked (Conflict 409)")
    void testBookSeatAlreadyBooked() throws Exception {
        // Mark seat as already unavailable
        testSeat.setAvailable(false);
        seatRepository.save(testSeat);

        BookingRequest request = new BookingRequest(
                "user-102",
                testCar.getCarId(),
                testSeat.getId(),
                new BigDecimal("50.00"),
                "Route A",
                "City X",
                "City Y",
                LocalDate.now().plusDays(1)
        );

        mockMvc.perform(post("/api/v1/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Fail to book a non-existent seat (Not Found 404)")
    void testBookSeatNotFound() throws Exception {
        BookingRequest request = new BookingRequest(
                "user-103",
                testCar.getCarId(),
                99999L, // Non-existent seat ID
                new BigDecimal("50.00"),
                "Route A",
                "City X",
                "City Y",
                LocalDate.now().plusDays(1)
        );

        mockMvc.perform(post("/api/v1/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Fail to book when car does not exist (Internal Server Error / Bad Request)")
    void testBookCarNotFound() throws Exception {
        BookingRequest request = new BookingRequest(
                "user-104",
                "KXX 9999Z", // Non-existent car plate
                testSeat.getId(),
                new BigDecimal("50.00"),
                "Route A",
                "City X",
                "City Y",
                LocalDate.now().plusDays(1)
        );

        mockMvc.perform(post("/api/v1/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()); // Caught by generic Exception handler
    }
}