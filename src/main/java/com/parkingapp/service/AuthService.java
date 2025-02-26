package com.parkingapp.service;

import com.parkingapp.model.Admin;
import com.parkingapp.repo.AdminRepository;
import com.parkingapp.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String login(String username, String password) {
        Optional<Admin> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            Admin admin = user.get();
            if (passwordEncoder.matches(password, admin.getPassword())) {
                return "Login successful!";
            }
        }
        return "Invalid username or password!";
    }

    public String registerAdmin(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return "Username already exists!";
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password)); // Encrypt password
        admin.setRole(role);
        adminRepository.save(admin);

        return "Admin registered successfully!";
    }
}
