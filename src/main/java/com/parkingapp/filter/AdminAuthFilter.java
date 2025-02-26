package com.parkingapp.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminAuthFilter extends OncePerRequestFilter {
	private static final Map<String, String> sessionTokens = new HashMap<>();
	private static final Map<String, String> userRoles = new HashMap<>();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");

		if (request.getRequestURI().startsWith("/api/admin/login")) {
			System.out.println("entered");
			filterChain.doFilter(request, response);
			return;
		}

		// 🔹 Strip "Bearer " prefix if present
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			authHeader = authHeader.substring(7); // Remove "Bearer "
		}

		System.out.println("🔍 Checking token: " + authHeader);

		// 🔹 Validate token for admin routes
		if (request.getRequestURI().startsWith("/api/admin")
				&& (authHeader == null || !sessionTokens.containsKey(authHeader))) {
			System.out.println("❌ Unauthorized access attempt");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			return;
		}

		// 🔹 Restrict access for regular admins
		if (request.getRequestURI().startsWith("/api/admin/manage-admins")
				&& !"SUPER_ADMIN".equals(userRoles.get(authHeader))) {
			System.out.println("❌ Access denied for non-super admin");
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
			return;
		}

		System.out.println("✅ Access granted");
		filterChain.doFilter(request, response);
	}

	// 🔹 Store session
	public static void addSession(String token, String username, String role) {
		sessionTokens.put(token, username);
		userRoles.put(token, role);
		 // Create the authentication object with the username and role
	    User principal = new User(username, "", AuthorityUtils.createAuthorityList(role)); // No password needed
	    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
	    
	    // Set the authentication in the security context
	    SecurityContextHolder.getContext().setAuthentication(authentication);
		System.out.println("✅ Session added: " + username + " (" + role + ")");
	}

	// 🔹 Remove session
	public static void removeSession(String token) {
		sessionTokens.remove(token);
		userRoles.remove(token);
		System.out.println("🚪 Session removed for token: " + token);
	}

	// 🔹 Get user role
	public static String getUserRole(String token) {
		System.out.println("🔍 Checking role for token: " + token);
		return userRoles.get(token); // Fixed recursion issue
	}

	// 🔹 Get username
	public static String getUsername(String token) {
		return sessionTokens.get(token); // No JWT decoding needed
	}
}
