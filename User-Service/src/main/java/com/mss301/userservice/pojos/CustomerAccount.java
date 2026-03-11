package com.mss301.userservice.pojos;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "customer_account")
@Data
public class CustomerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String passwordHash;

    private String phone;

    private String fullName;

    private String status;

    private LocalDateTime createdAt;
}

