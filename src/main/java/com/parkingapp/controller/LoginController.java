package com.parkingapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.parkingapp.service.AuthService;

import java.util.Map;

//@RestController
//@RequestMapping("/api/admin")
//@CrossOrigin(origins = "*")
public class LoginController {
	
}
//
//    @Autowired
//    private AuthService authService;
//
//    @PostMapping("/login")
//    public String login(@RequestBody Map<String, String> loginRequest) {
//        String username = loginRequest.get("username");
//        String password = loginRequest.get("password");
//        return authService.login(username, password);
//    }
//
//    @PostMapping("/register")
//    public String register(@RequestBody Map<String, String> registerRequest) {
//        String username = registerRequest.get("username");
//        String password = registerRequest.get("password");
//        String role = registerRequest.get("role");
//        return authService.registerAdmin(username, password, role);
//    }
//}
