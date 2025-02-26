package com.parkingapp.model;

import org.apache.catalina.util.StringUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String password;
	private String role; // "SUPER_ADMIN" or "ADMIN"

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setPassword(String rawPassword) {
		this.password = new BCryptPasswordEncoder().encode(rawPassword);
	}

	public String getPassword() {
		return password;
	}

	public boolean checkPassword(String rawPassword) {
		return new BCryptPasswordEncoder().matches(rawPassword, this.password);
	}
	public boolean checkPasswordForAdmin(String rawPassword) {
		return rawPassword.equals(password);
	}
}
