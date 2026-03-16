package com.example.taskservice.pojos;

import com.example.taskservice.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "maintenance_task")
@Data
public class MaintenanceTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;

    private String description;

    private Long assignedTo;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.PENDING;
}