package com.parkingapp.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import com.parkingapp.model.Admin;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {  // Make sure Admin is the entity
    Optional<Admin> findByUsername(String username);
}
