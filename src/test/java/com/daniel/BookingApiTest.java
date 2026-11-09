package com.daniel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.daniel.entity.CarEntity;
import com.daniel.entity.SeatEntity;
import com.daniel.entity.TripEntity;
import com.daniel.repository.BookingRepository;
import com.daniel.repository.CarRepository;
import com.daniel.repository.SeatRepository;
import com.daniel.repository.TripRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class BookingApiTest {

    private static final ZoneId KENYA_TIME_ZONE = ZoneId.of("Africa/Nairobi");

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

    @Autowired
    private TripRepository tripRepository;

    private CarEntity bus;
    private SeatEntity seat;
    private TripEntity trip;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        tripRepository.deleteAll();
        seatRepository.deleteAll();
        carRepository.deleteAll();

        bus = carRepository.save(new CarEntity("DEMO 100A", 1, "Coach"));
        seat = new SeatEntity(true, "window");
        seat.setCarId(bus.getCarId());
        seat = seatRepository.save(seat);
        LocalDateTime departure = LocalDate.now(KENYA_TIME_ZONE).plusDays(1).atTime(7, 0);
        trip = tripRepository.save(new TripEntity("Nairobi - Kisumu", "Nairobi", "Kisumu",
                departure, departure.plusHours(7), new BigDecimal("1250.00"), bus.getCarId()));
    }

    @Test
    void searchesTripsAndShowsAvailableSeats() throws Exception {
        mockMvc.perform(get("/api/v1/trips")
                        .param("from", "nairobi")
                        .param("to", "Kisumu")
                        .param("date", trip.getDepartureTime().toLocalDate().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fare").value(1250.0))
                .andExpect(jsonPath("$[0].currency").value("KES"))
                .andExpect(jsonPath("$[0].availableSeats").value(1));

        mockMvc.perform(get("/api/v1/trips/{tripId}/seats", trip.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void confirmsBookingUsingServerFareAndReference() throws Exception {
        String response = mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingRequest(trip.getId(), seat.getId(), "Amina Otieno", "0712345678")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.passengerName").value("Amina Otieno"))
                .andExpect(jsonPath("$.amount").value(1250.0))
                .andExpect(jsonPath("$.paymentStatus").value("PAY_AT_STATION"))
                .andReturn().getResponse().getContentAsString();

        String reference = objectMapper.readTree(response).get("bookingReference").asText();
        mockMvc.perform(get("/api/v1/bookings/{reference}", reference))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingReference").value(reference));

        assertThat(bookingRepository.count()).isEqualTo(1);
        mockMvc.perform(get("/api/v1/trips/{tripId}/seats", trip.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(false));
    }

    @Test
    void preventsBookingTheSameSeatTwice() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingRequest(trip.getId(), seat.getId(), "Amina Otieno", "0712345678")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingRequest(trip.getId(), seat.getId(), "Brian Otieno", "0712345679")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
        assertThat(bookingRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectsInvalidPassengerPhone() throws Exception {
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingRequest(trip.getId(), seat.getId(), "Amina Otieno", "not-a-phone")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void doesNotAllowSeatFromAnotherBus() throws Exception {
        SeatEntity otherSeat = new SeatEntity(true, "aisle");
        otherSeat.setCarId("OTHER 200B");
        otherSeat = seatRepository.save(otherSeat);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingRequest(trip.getId(), otherSeat.getId(), "Amina Otieno", "0712345678")))
                .andExpect(status().isBadRequest());
    }

    private String bookingRequest(Long tripId, Long seatId, String name, String phone) throws Exception {
        return objectMapper.writeValueAsString(new BookingPayload(tripId, seatId, name, phone));
    }

    private record BookingPayload(Long tripId, Long seatId, String passengerName, String passengerPhone) {
    }
}
