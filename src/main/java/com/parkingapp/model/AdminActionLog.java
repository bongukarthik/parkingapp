package com.parkingapp.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_action_logs")
public class AdminActionLog {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String performedBy; // Super Admin who performed the action
	private String actionType; // CREATE, DELETE, UPDATE_ROLE
	private String targetAdmin; // Admin affected by this action
	private LocalDateTime timestamp = LocalDateTime.now();
	private String ipAddress;
	private String adminRole;

	public AdminActionLog(String performedBy, String actionType, String targetAdmin) {
		this.performedBy = performedBy;
		this.actionType = actionType;
		this.targetAdmin = targetAdmin;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPerformedBy() {
		return performedBy;
	}

	public void setPerformedBy(String performedBy) {
		this.performedBy = performedBy;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getTargetAdmin() {
		return targetAdmin;
	}

	public void setTargetAdmin(String targetAdmin) {
		this.targetAdmin = targetAdmin;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getAdminRole() {
		return adminRole;
	}

	public void setAdminRole(String adminRole) {
		this.adminRole = adminRole;
	}

	public AdminActionLog(Long id, String performedBy, String actionType, String targetAdmin, LocalDateTime timestamp,
			String ipAddress, String adminRole) {
		super();
		this.id = id;
		this.performedBy = performedBy;
		this.actionType = actionType;
		this.targetAdmin = targetAdmin;
		this.timestamp = timestamp;
		this.ipAddress = ipAddress;
		this.adminRole = adminRole;
	}

}
