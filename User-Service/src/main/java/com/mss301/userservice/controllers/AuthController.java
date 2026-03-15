package com.mss301.userservice.controllers;

import com.mss301.userservice.dto.AuthResponse;
import com.mss301.userservice.dto.LoginRequest;
import com.mss301.userservice.dto.RegisterRequest;
import com.mss301.userservice.pojos.UserAccount;
import com.mss301.userservice.repository.UserAccountRepository;
import com.mss301.userservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userAccountRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Lỗi: Username đã tồn tại!");
        }
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Lỗi: Email đã được sử dụng!");
        }

        UserAccount newUser = new UserAccount();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());

        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        newUser.setRole("ROLE_CUSTOMER");

        userAccountRepository.save(newUser);

        return ResponseEntity.ok(new AuthResponse(null, "Đăng ký thành công!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new AuthResponse(null, "Sai tài khoản hoặc mật khẩu!"));
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt, "Đăng nhập thành công"));
    }
}