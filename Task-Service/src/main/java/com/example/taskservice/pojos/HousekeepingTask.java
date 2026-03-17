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
    private Long assignedTo;
    private Long reservationId;

    private String type;     // Loại công việc (VD: Dọn phòng hằng ngày, Dọn sau Check-out)
    private String priority; // Độ ưu tiên (VD: NORMAL, HIGH)

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}