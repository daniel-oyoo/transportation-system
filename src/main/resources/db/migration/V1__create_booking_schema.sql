CREATE TABLE cars (
    id BIGSERIAL PRIMARY KEY,
    car_id VARCHAR(255) NOT NULL UNIQUE,
    number_of_seats INTEGER NOT NULL,
    car_type VARCHAR(255) NOT NULL
);

CREATE TABLE seats (
    id BIGSERIAL PRIMARY KEY,
    car_id VARCHAR(255) NOT NULL,
    is_available BOOLEAN NOT NULL,
    seat_type VARCHAR(255) NOT NULL
);

CREATE INDEX idx_seats_car_id ON seats(car_id);

CREATE TABLE trips (
    id BIGSERIAL PRIMARY KEY,
    route_name VARCHAR(255) NOT NULL,
    origin VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    fare NUMERIC(10, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(255) NOT NULL,
    car_id VARCHAR(255) NOT NULL
);

CREATE INDEX idx_trips_search ON trips(status, departure_time, origin, destination);

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    car_id VARCHAR(255) NOT NULL,
    seat_id BIGINT NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    route VARCHAR(255),
    source VARCHAR(255),
    destination VARCHAR(255),
    travel_date DATE NOT NULL,
    booking_reference VARCHAR(36) UNIQUE,
    trip_id BIGINT,
    passenger_name VARCHAR(255),
    passenger_phone VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    payment_status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_booking_trip_seat UNIQUE (trip_id, seat_id)
);

CREATE INDEX idx_bookings_trip_status ON bookings(trip_id, status);
