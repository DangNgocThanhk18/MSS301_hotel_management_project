// src/main/java/com/example/taskservice/pojos/HousekeepingTask.java
package com.example.taskservice.pojos;

import com.example.taskservice.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "housekeeping_tasks")
@Data
public class HousekeepingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;
    private String roomNumber;  // Thêm trường này
    private Long assignedTo;
    private Long reservationId;

    private String type;     // Loại công việc
    private String priority; // Độ ưu tiên

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}