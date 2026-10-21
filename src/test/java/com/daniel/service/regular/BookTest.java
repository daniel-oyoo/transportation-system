package com.daniel.service.regular;

import com.daniel.dto.request.BookingRequest;
import com.daniel.dto.response.BookingResponse;
import com.daniel.entity.BookingEntity;
import com.daniel.entity.SeatEntity;
import com.daniel.exception.SeatAlreadyBookedException;
import com.daniel.exception.SeatNotFoundException;
import com.daniel.repository.BookingRepository;
import com.daniel.repository.SeatRepository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookTest{

    @Mock 
    BookingRepository bookingRepository;

    @Mock
    SeatRepository seatRepository;

    @InjectMocks 
    CustomerService customerService;

    //happy path

}