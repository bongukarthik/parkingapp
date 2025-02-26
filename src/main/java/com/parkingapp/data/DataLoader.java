package com.parkingapp.data;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.parkingapp.model.Admin;
import com.parkingapp.repo.AdminRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private final AdminRepository adminRepository;
//    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataLoader(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... args) {
        if (adminRepository.findByUsername("admin").isEmpty()) { // Prevent duplicate admin creation
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("nimda"); // Store encrypted password
            admin.setRole("SUPER_ADMIN");
            adminRepository.save(admin);
            System.out.println("Super admin account created!");
        }
    }
}
