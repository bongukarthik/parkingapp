package com.parkingapp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parkingapp.model.User;
import com.parkingapp.repo.UserRepository;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/topup")
    public ResponseEntity<?> topUpWallet(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Double amount = ((Number) request.get("amount")).doubleValue();

        if (amount <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid top-up amount");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        user.setWalletBalance(user.getWalletBalance() + amount);
        userRepository.save(user);

        return ResponseEntity.ok("Wallet updated successfully! New balance: " + user.getWalletBalance());
    }
}
