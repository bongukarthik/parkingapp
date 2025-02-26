package com.parkingapp.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parkingapp.model.Booking;
import com.parkingapp.model.ParkingSlot;
import com.parkingapp.model.User;
import com.parkingapp.repo.BookingRepository;
import com.parkingapp.repo.ParkingSlotRepository;
import com.parkingapp.repo.UserRepository;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
	@Autowired
	private BookingRepository bookingRepository;

	@Autowired
	private ParkingSlotRepository parkingSlotRepository;

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/reserve")
	public ResponseEntity<?> reserveSlot(@RequestBody Map<String, Object> bookingRequest) {
		Long userId = ((Number) bookingRequest.get("userId")).longValue();
		Long slotId = ((Number) bookingRequest.get("slotId")).longValue();

		User user = userRepository.findById(userId).orElse(null);
		ParkingSlot slot = parkingSlotRepository.findById(slotId).orElse(null);

		if (user == null || slot == null || !slot.isAvailable()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
		}

		if (user.getWalletBalance() < slot.getPrice()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient wallet balance");
		}

		// Deduct amount from wallet
		user.setWalletBalance(user.getWalletBalance() - slot.getPrice());
		userRepository.save(user);

		// Mark slot as booked
		slot.setAvailable(false);
		parkingSlotRepository.save(slot);

		// Create booking
		Booking booking = new Booking();
		booking.setUser(user);
		booking.setSlot(slot);
		booking.setBookingTime(LocalDateTime.now());
		booking.setPaid(true);
		bookingRepository.save(booking);

		return ResponseEntity.ok(booking);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getBookingsForUser(@PathVariable Long userId) {
		List<Booking> bookings = bookingRepository.findByUserIdOrderByBookingTimeDesc(userId);
		return ResponseEntity.ok(bookings);
	}

}
