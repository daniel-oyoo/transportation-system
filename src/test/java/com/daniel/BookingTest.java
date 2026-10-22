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
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        carRepository.deleteAll();

        testCar = carRepository.save(new CarEntity("KAA 1234A", 30, "Bus"));
        testSeat = new SeatEntity(true, "window");
        testSeat.setCarId(testCar.getCarId());
        testSeat = seatRepository.save(testSeat);
    }

    @Test
    @DisplayName("Successfully book an available seat")
    void booksAvailableSeat() throws Exception {
        mockMvc.perform(post("/api/v1/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest(
                                testCar.getCarId(), testSeat.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user-101"))
                .andExpect(jsonPath("$.carId").value(testCar.getCarId()))
                .andExpect(jsonPath("$.seatId").value(testSeat.getId()));

        org.assertj.core.api.Assertions.assertThat(bookingRepository.count()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(
                seatRepository.findById(testSeat.getId()).orElseThrow().isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Returns conflict when the seat is already booked")
    void rejectsUnavailableSeat() throws Exception {
        testSeat.setAvailable(false);
        seatRepository.save(testSeat);

        mockMvc.perform(post("/api/v1/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest(
                                testCar.getCarId(), testSeat.getId()))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Returns not found when the seat does not exist")
    void rejectsMissingSeat() throws Exception {
        mockMvc.perform(post("/api/v1/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest(
                                testCar.getCarId(), 99999L))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Returns internal server error when the car does not exist")
    void rejectsMissingCar() throws Exception {
        mockMvc.perform(post("/api/v1/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequest(
                                "KXX 9999Z", testSeat.getId()))))
                .andExpect(status().isInternalServerError());
    }

    private BookingRequest bookingRequest(String carId, Long seatId) {
        return new BookingRequest(
                "user-101",
                carId,
                seatId,
                new BigDecimal("50.00"),
                "Route A",
                "City X",
                "City Y",
                LocalDate.now().plusDays(1)
        );
    }
}
