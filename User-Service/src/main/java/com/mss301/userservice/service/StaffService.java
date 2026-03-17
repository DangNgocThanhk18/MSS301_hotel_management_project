package com.mss301.userservice.service;

import com.mss301.userservice.dto.StaffRequestDTO;
import com.mss301.userservice.dto.StaffResponseDTO;
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
public class StaffService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public List<StaffResponseDTO> getAllStaff() {
        List<UserAccount> staffList = userAccountRepository.findByRoleNot(UserRole.CUSTOMER);
        return staffList.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public StaffResponseDTO createStaff(StaffRequestDTO requestDTO) {
        if (userAccountRepository.existsByUsername(requestDTO.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }
        if (requestDTO.getRole() == UserRole.CUSTOMER) {
            throw new RuntimeException("Không thể tạo role CUSTOMER!");
        }

        UserAccount staff = new UserAccount();
        staff.setUsername(requestDTO.getUsername());
        staff.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));
        staff.setFullName(requestDTO.getFullName());
        staff.setEmail(requestDTO.getEmail());
        staff.setPhone(requestDTO.getPhone());
        staff.setRole(requestDTO.getRole());
        staff.setStatus(AccountStatus.ACTIVE);
        staff.setCreatedAt(LocalDateTime.now());

        return mapToDTO(userAccountRepository.save(staff));
    }

    public StaffResponseDTO updateStaff(Long id, StaffRequestDTO requestDTO) {
        UserAccount staff = userAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        staff.setFullName(requestDTO.getFullName());
        staff.setEmail(requestDTO.getEmail());
        staff.setPhone(requestDTO.getPhone());
        staff.setRole(requestDTO.getRole());

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            staff.setPasswordHash(passwordEncoder.encode(requestDTO.getPassword()));
        }

        return mapToDTO(userAccountRepository.save(staff));
    }

    public void updateStaffStatus(Long id, String status) {
        UserAccount staff = userAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        staff.setStatus(AccountStatus.valueOf(status.toUpperCase()));
        userAccountRepository.save(staff);
    }

    public void deleteStaff(Long id) {
        UserAccount staff = userAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        staff.setStatus(AccountStatus.INACTIVE);
        userAccountRepository.save(staff);
    }

    private StaffResponseDTO mapToDTO(UserAccount account) {
        StaffResponseDTO dto = new StaffResponseDTO();
        dto.setId(account.getId());
        dto.setUsername(account.getUsername());
        dto.setFullName(account.getFullName());
        dto.setEmail(account.getEmail());
        dto.setPhone(account.getPhone());
        dto.setRole(account.getRole());
        dto.setStatus(account.getStatus());
        dto.setCreatedAt(account.getCreatedAt());
        return dto;
    }
}