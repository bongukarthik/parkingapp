package com.parkingapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parkingapp.model.ParkingSlot;
import com.parkingapp.repo.ParkingSlotRepository;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @GetMapping("/available")
    public List<ParkingSlot> getAvailableSlots() {
        return parkingSlotRepository.findByIsAvailableTrue();
    }
}
