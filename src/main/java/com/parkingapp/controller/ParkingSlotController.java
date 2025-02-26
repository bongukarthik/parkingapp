package com.parkingapp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parkingapp.model.ParkingSlot;
import com.parkingapp.repo.ParkingSlotRepository;

@RestController
@RequestMapping("/api/slots")
public class ParkingSlotController {
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addSlot(@RequestBody ParkingSlot slot) {
        slot.setAvailable(true); // New slots are available by default
        parkingSlotRepository.save(slot);
        return ResponseEntity.ok("Slot added successfully!");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSlot(@PathVariable Long id, @RequestBody ParkingSlot updatedSlot) {
        Optional<ParkingSlot> existingSlot = parkingSlotRepository.findById(id);
        if (existingSlot.isPresent()) {
            ParkingSlot slot = existingSlot.get();
            slot.setLocation(updatedSlot.getLocation());
            slot.setPrice(updatedSlot.getPrice());
            slot.setAvailable(updatedSlot.isAvailable());
            parkingSlotRepository.save(slot);
            return ResponseEntity.ok("Slot updated successfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Slot not found");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSlot(@PathVariable Long id) {
        if (parkingSlotRepository.existsById(id)) {
            parkingSlotRepository.deleteById(id);
            return ResponseEntity.ok("Slot deleted successfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Slot not found");
    }
}
