package com.mss301.userservice.pojos;

import com.mss301.userservice.enums.DocumentType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "guest")
@Data
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private String nationality;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String documentNumber;

    private LocalDateTime createdAt;
}