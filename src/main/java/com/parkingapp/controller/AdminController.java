package com.parkingapp.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.parkingapp.filter.AdminAuthFilter;
import com.parkingapp.model.Admin;
import com.parkingapp.model.AdminActionLog;
import com.parkingapp.repo.AdminActionLogRepository;
import com.parkingapp.repo.AdminRepository;

import jakarta.servlet.http.HttpServletResponse;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin")
public class AdminController {
	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private AdminActionLogRepository adminActionLogRepository;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
		String username = credentials.get("username");
		String password = credentials.get("password");

		Optional<Admin> adminOpt = adminRepository.findByUsername(username);
		if (adminOpt.isPresent() && adminOpt.get().checkPassword(password)) {
			String token = UUID.randomUUID().toString();
			AdminAuthFilter.addSession(token, username, adminOpt.get().getRole());
			Map<String, String> response = new HashMap<>();
			response.put("token", token);
			response.put("role", adminOpt.get().getRole());
			return ResponseEntity.ok(response);
		}
		else if (adminOpt.isPresent() && adminOpt.get().getRole().equalsIgnoreCase("SUPER_ADMIN") && adminOpt.get().checkPasswordForAdmin(password)) {
			String token = UUID.randomUUID().toString();
			AdminAuthFilter.addSession(token, username, adminOpt.get().getRole());
			Map<String, String> response = new HashMap<>();
			response.put("token", token);
			response.put("role", adminOpt.get().getRole());
			return ResponseEntity.ok(response);
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
		}
	}

	@PostMapping("/create-admin")
	public ResponseEntity<?> createAdmin(@RequestHeader("Authorization") String token, @RequestBody Admin newAdmin) {
		String superAdmin = AdminAuthFilter.getUsername(token);

		if (!"SUPER_ADMIN".equals(AdminAuthFilter.getUserRole(token))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
		}

		if (adminRepository.findByUsername(newAdmin.getUsername()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
		}

		newAdmin.setPassword(newAdmin.getPassword()); // Hash password
		adminRepository.save(newAdmin);
//
//		CrudRepository<Admin, Long> adminActionLogRepository;
		adminActionLogRepository.save(new AdminActionLog(superAdmin, "CREATE", newAdmin.getUsername()));

		return ResponseEntity.ok("Admin created successfully");
	}

	@DeleteMapping("/delete-admin/{adminId}")
	public ResponseEntity<?> deleteAdmin(@RequestHeader("Authorization") String token, @PathVariable Long adminId) {
		String superAdmin = AdminAuthFilter.getUsername(token);

		if (!"SUPER_ADMIN".equals(AdminAuthFilter.getUserRole(token))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
		}

		Optional<Admin> adminOpt = adminRepository.findById(adminId);
		if (adminOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
		}

		String deletedAdmin = adminOpt.get().getUsername();
		adminRepository.deleteById(adminId);

		adminActionLogRepository.save(new AdminActionLog(superAdmin, "DELETE", deletedAdmin));

		return ResponseEntity.ok("Admin deleted successfully");
	}

	@PutMapping("/update-role/{adminId}")
	public ResponseEntity<?> updateAdminRole(@RequestHeader("Authorization") String token, @PathVariable Long adminId,
			@RequestBody Map<String, String> request) {
		String superAdmin = AdminAuthFilter.getUsername(token);

		if (!"SUPER_ADMIN".equals(AdminAuthFilter.getUserRole(token))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
		}

		Optional<Admin> adminOpt = adminRepository.findById(adminId);
		if (adminOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
		}

		Admin admin = adminOpt.get();
		String newRole = request.get("role");

		if (!newRole.equals("SUPER_ADMIN") && !newRole.equals("ADMIN")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role");
		}

		admin.setRole(newRole);
		adminRepository.save(admin);

		adminActionLogRepository.save(new AdminActionLog(superAdmin, "UPDATE_ROLE", admin.getUsername()));

		return ResponseEntity.ok("Admin role updated successfully");
	}

	@GetMapping("/logs")
	public ResponseEntity<List<AdminActionLog>> getAdminLogs(@RequestHeader("Authorization") String token) {

		if (!"SUPER_ADMIN".equals(AdminAuthFilter.getUserRole(token))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		List<AdminActionLog> logs = adminActionLogRepository.findAll();
		return ResponseEntity.ok(logs);
	}

//	@GetMapping("/logs/download")
//	public void downloadAdminLogs(HttpServletResponse response, @RequestHeader("Authorization") String token)
//			throws IOException {
//		if (!"SUPER_ADMIN".equals(AdminAuthFilter.getUserRole(token))) {
//			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
//			return;
//		}
//
//		response.setContentType("text/csv");
//		response.setHeader("Content-Disposition", "attachment; filename=admin_logs.csv");
//
//		List<AdminActionLog> logs = adminActionLogRepository.findAll();
//
//		PrintWriter writer = response.getWriter();
//		writer.println("Timestamp,Performed By,Action Type,Target Admin");
//
//		for (AdminActionLog log : logs) {
//			writer.printf("%s,%s,%s,%s%n", log.getTimestamp(), log.getPerformedBy(), log.getActionType(),
//					log.getTargetAdmin());
//		}
//		writer.flush();
//	}

	@GetMapping("/logs/download")
	public void downloadFilteredAdminLogs(HttpServletResponse response, @RequestHeader("Authorization") String token,
			@RequestParam(required = false) String actionType, @RequestParam(required = false) String performedBy,
			@RequestParam(required = false) String targetAdmin,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
			throws IOException {

		if (!"SUPER_ADMIN".equals(AdminAuthFilter.getUserRole(token))) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
			return;
		}

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=admin_logs.csv");

		List<AdminActionLog> logs = adminActionLogRepository.findLogs(actionType, performedBy, targetAdmin, startDate,
				endDate);

		PrintWriter writer = response.getWriter();
		writer.println("Timestamp,Performed By,Action Type,Target Admin,IP Address,Admin Role");

		for (AdminActionLog log : logs) {
			writer.printf("%s,%s,%s,%s,%s,%s%n", log.getTimestamp(), log.getPerformedBy(), log.getActionType(),
					log.getTargetAdmin(), log.getIpAddress(), log.getAdminRole());
		}
		writer.flush();
	}

}
