package com.mss301.userservice.controllers;

import com.mss301.userservice.dto.AuthResponse;
import com.mss301.userservice.dto.LoginRequest;
import com.mss301.userservice.dto.RegisterRequest;
import com.mss301.userservice.enums.AccountStatus;
import com.mss301.userservice.enums.UserRole;
import com.mss301.userservice.pojos.UserAccount;
import com.mss301.userservice.repository.UserAccountRepository;
import com.mss301.userservice.security.CustomUserDetails;
import com.mss301.userservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/staff/login")
    public ResponseEntity<?> staffLogin(@RequestBody LoginRequest request) {
        return login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (userAccountRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Lỗi: Username đã tồn tại!");
        }
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Lỗi: Email đã được sử dụng!");
        }
        String usernameToSave = (request.getUsername() != null && !request.getUsername().isEmpty())
                ? request.getUsername()
                : request.getEmail();

        UserAccount newUser = new UserAccount();
        newUser.setUsername(usernameToSave);
        newUser.setEmail(request.getEmail());
        newUser.setFullName(request.getFullName());
        newUser.setPhone(request.getPhoneNumber());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        if (request.getRole() != null) {
            newUser.setRole(request.getRole());
        } else {
            newUser.setRole(UserRole.CUSTOMER);
        }

        newUser.setStatus(AccountStatus.ACTIVE);
        newUser.setCreatedAt(LocalDateTime.now());

        userAccountRepository.save(newUser);

        return ResponseEntity.ok(new AuthResponse(null, "Đăng ký thành công!", null, null, null, null, null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new AuthResponse(null, "Sai tài khoản hoặc mật khẩu!", null, null, null, null, null));
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        UserAccount user = customUserDetails.getUserAccount();

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                "Đăng nhập thành công",
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole()
        ));
    }

    @PutMapping("/user/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return userAccountRepository.findByEmail(email)
                .map(user -> {
                    user.setFullName(request.get("fullName"));
                    user.setPhone(request.get("phone"));
                    userAccountRepository.save(user);
                    return ResponseEntity.ok(new AuthResponse(null, "Cập nhật thành công!",
                            user.getId(), user.getEmail(), user.getFullName(), user.getPhone(), user.getRole()));
                })
                .orElse(ResponseEntity.status(404).body(new AuthResponse(null, "Không tìm thấy người dùng", null, null, null, null, null)));
    }

    // 2. Endpoint đổi mật khẩu
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        return userAccountRepository.findByEmail(email)
                .map(user -> {
                    // Kiểm tra mật khẩu cũ
                    if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                        return ResponseEntity.status(401).body(Map.of("message", "Mật khẩu hiện tại không chính xác"));
                    }
                    // Mã hóa và lưu mật khẩu mới
                    user.setPasswordHash(passwordEncoder.encode(newPassword));
                    userAccountRepository.save(user);
                    return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
                })
                .orElse(ResponseEntity.status(404).body(Map.of("message", "Người dùng không tồn tại")));
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        try {

            String jwtToken = token.substring(7);
            String username = jwtUtil.extractUsername(jwtToken);

            UserAccount user = userAccountRepository.findByUsername(username)
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            // Tạo response DTO
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("fullName", user.getFullName());
            userInfo.put("phone", user.getPhone());
            userInfo.put("username", user.getUsername());
            userInfo.put("role", user.getRole());

            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }
}