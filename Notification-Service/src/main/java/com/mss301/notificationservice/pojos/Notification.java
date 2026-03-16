package com.mss301.notificationservice.pojos;

import com.mss301.notificationservice.enums.NotificationStatus;
import com.mss301.notificationservice.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.UNREAD;

    private LocalDateTime createdAt;
}