package com.parkingapp.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parkingapp.model.Booking;
import com.parkingapp.repo.BookingRepository;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingRepository bookingRepository;

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveSlot(@RequestBody Booking booking) {
        booking.setBookingTime(LocalDateTime.now());
        booking.setPaid(false);
        return ResponseEntity.ok(bookingRepository.save(booking));
    }
}
