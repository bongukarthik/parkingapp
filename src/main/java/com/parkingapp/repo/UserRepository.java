package com.parkingapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parkingapp.model.Admin;
import com.parkingapp.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	// Find user by email (for login/authentication)
	Optional<User> findByEmail(String email);

	// Check if an email already exists
	boolean existsByEmail(String email);

	// Find all users by role (e.g., ADMIN, CUSTOMER)
	List<User> findByRole(String role);
	
    Optional<Admin> findByUsername(String username);

}
