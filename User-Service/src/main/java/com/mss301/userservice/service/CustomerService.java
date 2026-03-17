package com.mss301.userservice.service;

import com.mss301.userservice.dto.*;
import com.mss301.userservice.enums.AccountStatus;
import com.mss301.userservice.enums.UserRole;
import com.mss301.userservice.pojos.UserAccount;
import com.mss301.userservice.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    // Đổi sang dùng UserAccountRepository
    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<CustomerResponseDTO> getAllCustomers() {
        // Chỉ lấy những tài khoản có Role là CUSTOMER
        return userRepository.findByRole(UserRole.CUSTOMER).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng.");
        }

        UserAccount user = new UserAccount();
        user.setFullName(requestDTO.getFullName());
        user.setEmail(requestDTO.getEmail());
        user.setPhone(requestDTO.getPhone());

        // Mật khẩu lưu vào trường passwordHash
        user.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));

        // Frontend không nhập Username, ta lấy Email làm Username luôn
        user.setUsername(requestDTO.getEmail());

        user.setStatus(AccountStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(UserRole.CUSTOMER);

        return mapToDTO(userRepository.save(user));
    }

    public CustomerResponseDTO updateCustomer(Long id, CustomerUpdateDTO requestDTO) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));

        user.setFullName(requestDTO.getFullName());
        user.setPhone(requestDTO.getPhone());

        return mapToDTO(userRepository.save(user));
    }

    public void updateCustomerStatus(Long id, CustomerStatusDTO requestDTO) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));

        try {
            AccountStatus newStatus = AccountStatus.valueOf(requestDTO.getNewStatus().toUpperCase());
            user.setStatus(newStatus);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ.");
        }
    }

    public void deleteCustomer(Long id) {
        UserAccount user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));

        user.setStatus(AccountStatus.INACTIVE);
        userRepository.save(user);
    }

    private CustomerResponseDTO mapToDTO(UserAccount user) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setStatus(user.getStatus() != null ? user.getStatus().name() : "");
        dto.setRole(user.getRole() != null ? user.getRole().name() : UserRole.CUSTOMER.name());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}