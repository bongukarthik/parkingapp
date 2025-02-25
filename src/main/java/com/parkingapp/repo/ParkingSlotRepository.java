package com.parkingapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parkingapp.model.ParkingSlot;

import java.util.List;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    // Find all slots for a specific location
    List<ParkingSlot> findByLocationId(Long locationId);

    // Find available slots for a location
    List<ParkingSlot> findByLocationIdAndIsAvailableTrue(Long locationId);
    
    // Find all available slots
    List<ParkingSlot> findByIsAvailableTrue();

    // Find all unavailable slots
    List<ParkingSlot> findByIsAvailableFalse();
}
